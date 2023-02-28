package org.tools4j.tabular.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * User: ben
 * Date: 26/10/17
 * Time: 5:40 PM
 */
public class MapResolver {
    /*
     * Regex explained (Additional backslashes for java escaping removed):
     * (?<!\\) Look backward to ensure that this ${ is not escaped with a backslash '\'
     * \$\{ Followed by a dollar sign and squiggly bracket '${', this denotes the start of a variable replacement.
     * ([^\}^(?:\$\{)]+) Followed by one or more characters which are not '}' (end of variable), and which are not the start of a nested variable '${'
     * \} Followed by closing squiggly bracket, this denotes the end of a variable replacement.
     * 
     * Therefore, the regex will:
     * 1. Look for the start of a variable '${' which is not prefixed by an escape character '\'
     * 2. Look for one or more characters which are not the end of the variable '}' and which are not the start of another nested variable '${'. (Nested variables should be replaced first.)
     * 3. Followed by the close of a variable '}'
     */
    private final java.util.regex.Pattern VARIABLE_PATTERN = java.util.regex.Pattern.compile("(?<!\\\\)\\$\\{([^\\}^(?:\\$\\{)]+)\\}");
    private final Map<String, String> otherResources;

    public MapResolver(final Map<String, String> ... secondaryMaps) {
        if(secondaryMaps.length == 0) otherResources = Collections.emptyMap();
        else if(secondaryMaps.length == 1) otherResources = secondaryMaps[0];
        else {
            this.otherResources = new HashMap<>();
            for (final Map<String, String> secondaryMap : secondaryMaps) {
                this.otherResources.putAll(secondaryMap);
            }
        }    
    }

    /**
     * @return A map with the same keys as given in unresolvedMap, but with 'resolved' values.
     * 'resolved' means that any values which originally had variables in them, denoted by ${varname} syntax
     * have been resolved using variables from either the primary or secondary maps.
     *
     * The unresolvedMap is the map which you want to resolve.  The otherResources maps is to provide additional variables
     * that might be used during resolution.  The key/value pairs of otherResources are not returned from this resolve
     * method.  Nor are any values in the otherResources maps resolved.
     *
     * This method should be able to handle nested resolutions.  e.g. if a key pair value like this:
     * <p>
     *     myValue=${${var1}.${var2}}
     *     var1=one
     *     var2=two
     *     one.two=Hi there!
     * </p>
     * var1 and var2 will be resolved first, to:
     * <p>
     *     myValue=${one.two}
     *     var1=one
     *     var2=two
     *     one.two=Hi there!
     * </p>
     * And then one.two will be resolved:
     * <p>
     *     myValue=Hi there!
     *     var1=one
     *     var2=two
     *     one.two=Hi there!
     * </p>
     */
    public Map<String,String> resolve(Map<String, String> unresolvedMap){
        final Set<String> resolvedKeys = new HashSet<>(unresolvedMap.size());
        final Set<String> unresolvedKeys = new HashSet<>(unresolvedMap.size());
        final Map<String, String> map = new LinkedHashMap<>(unresolvedMap.size() + otherResources.size());

        //Initialize the maps and sets
        map.putAll(otherResources);
        map.putAll(unresolvedMap);
        unresolvedKeys.addAll(unresolvedMap.keySet());

        //Loop
        for(int i=0; i<1000; i++) {
            int numberOfReplacementsInThisLoop = 0;
            for (final String key : unresolvedKeys) {
                numberOfReplacementsInThisLoop = getNumberOfReplacementsInThisLoop(resolvedKeys, map, numberOfReplacementsInThisLoop, key);
            }
            unresolvedKeys.removeAll(resolvedKeys);
            if(numberOfReplacementsInThisLoop == 0){
                break;
            }
        }

        //Now just pull out items that were originally given in the unresolved map
        Map<String, String> returnMap = new LinkedHashMap<>(unresolvedMap.size());
        for(final String key: resolvedKeys){
            returnMap.put(key, map.get(key));
        }
        for(final String key: unresolvedKeys){
            returnMap.put(key, map.get(key));
        }

        returnMap = removeFirstEscapeFromEscapedVariableSymbols(returnMap);
        return returnMap;
    }

    private int getNumberOfReplacementsInThisLoop(Set<String> resolved, Map<String, String> map, int numberOfReplacementsInThisLoop, String key) {
        while (true) {
            int numberOfReplacementsForThisKeyInThisLoop = 0;
            int findFrom = 0;
            while (true) {
                final String value = map.get(key);
                if (findFrom >= value.length()) {
                    break;
                }
                //This is a 'circuit breaker'.  If the String does not contain '${' then assume
                //that there are no variables to solve.  This avoids the expensive operation of
                //continuously running regex matches.
                if(!value.contains("${")){
                    break;
                }
                final Matcher matcher = VARIABLE_PATTERN.matcher(value);
                if (!matcher.find(findFrom)) {
                    break;
                }
                final String variableName = matcher.group(1);
                final String variableValue = map.get(variableName);
                if (variableValue == null) {
                    resolved.add(key);
                } else {
                    final String newValue = value.replace(matcher.group(0), variableValue);
                    map.put(key, newValue);
                    numberOfReplacementsInThisLoop++;
                    numberOfReplacementsForThisKeyInThisLoop++;
                }
                findFrom = matcher.end();
            }
            if(numberOfReplacementsForThisKeyInThisLoop == 0){
                break;
            }
        }
        return numberOfReplacementsInThisLoop;
    }

    static Map<String, String> removeFirstEscapeFromEscapedVariableSymbols(Map<String, String> map){
        Map<String, String> returnMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : map.entrySet()) {
            returnMap.put(e.getKey(), replaceAllEscapeCharsNotPrecededByEscapeChars(e.getValue()));
        }
        return returnMap;
    }

    static String replaceAllEscapeCharsNotPrecededByEscapeChars(String str){
        return str.replaceAll("(?<!\\\\)\\\\\\$", "\\$");
    }
}
