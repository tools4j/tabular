package org.tools4j.launcher.util;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ben
 * Date: 10/11/17
 * Time: 4:51 PM
 */
public class ResolvedString {
    public static final String THE_KEY = "THE_STRING";
    private final String str;
    private final Map<String, String>[] valuesUsedToResolveVariablesInString;

    public ResolvedString(final String str, final Map<String, String> ... valuesUsedToResolveVariablesInString) {
        this.str = str;
        this.valuesUsedToResolveVariablesInString = valuesUsedToResolveVariablesInString;
    }

    public String resolve(){
        final Map<String, String> primaryMap = new HashMap<>();
        primaryMap.put(THE_KEY, str);
        final Map<String, String> resolvedMap = new ResolvedMap(primaryMap, valuesUsedToResolveVariablesInString).resolve();
        return resolvedMap.get(THE_KEY);
    }
}
