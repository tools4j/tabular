package org.tools4j.tabular.util;

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
public class ResolvedMap {
    private final java.util.regex.Pattern VARIABLE_PATTERN = java.util.regex.Pattern.compile("(?<!\\\\)\\$\\{([^\\}^(?:\\$\\{)]+)\\}");
    private final Map<String, String> primaryMap;
    private final Map<String, String> secondaryMap;

    public ResolvedMap(final Map<String, String> primaryMap, final Map<String, String> ... secondaryMaps) {
        this.primaryMap = primaryMap;
        this.secondaryMap = new HashMap<>();
        for (final Map<String, String> secondaryMap : secondaryMaps) {
            this.secondaryMap.putAll(secondaryMap);
        }
    }

    /**
     * @return A map with the same keys as given in primaryMap, but with 'resolved' values.
     * 'resolved' means that any values which originally had variables in them, denoted by ${varname} syntax
     * have been resolved using variables from either the primary or secondary maps.
     *
     * The primaryMap is the map which you want to resolve.  The secondaryMap is to provide additional variables
     * that might be used during resolution.  The key/value pairs of secondaryMap are not returned from this resolve
     * method.
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
    public Map<String,String> resolve(){
        final Set<String> resolved = new HashSet<>(primaryMap.size());
        final Set<String> unresolved = new HashSet<>(primaryMap.size());
        final Map<String, String> map = new LinkedHashMap<>(this.primaryMap.size() + this.secondaryMap.size());

        //Initialize the maps and sets
        map.putAll(this.secondaryMap);
        map.putAll(this.primaryMap);
        unresolved.addAll(map.keySet());

        //Loop
        for(int i=0; i<1000; i++) {
            int numberOfReplacementsInThisLoop = 0;
            for (final String key : unresolved) {
                while (true) {
                    int numberOfReplacementsForThisKeyInThisLoop = 0;
                    int findFrom = 0;
                    while (true) {
                        final String value = map.get(key);
                        if (findFrom >= value.length()) {
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
            }
            unresolved.removeAll(resolved);
            if(numberOfReplacementsInThisLoop == 0){
                break;
            }
        }

        //Now just pull out items that were originally given in the primary map
        Map<String, String> returnMap = new LinkedHashMap<>(this.primaryMap.size() + this.secondaryMap.size());
        for(final String key: primaryMap.keySet()){
            returnMap.put(key, map.get(key));
        }

        returnMap = removeEscapeCharacters(returnMap);
        return returnMap;
    }

    static Map<String, String> removeEscapeCharacters(Map<String, String> map){
        Map<String, String> returnMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : map.entrySet()) {
            returnMap.put(e.getKey(), replaceAllEscapeCharsNotPrecededByEscapeChars(e.getValue()));
        }
        return returnMap;
    }

    static String replaceAllEscapeCharsNotPrecededByEscapeChars(String str){
        return str.replaceAll("(?<!\\\\)\\\\", "");
    }
}
