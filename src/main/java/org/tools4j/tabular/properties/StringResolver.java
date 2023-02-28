package org.tools4j.tabular.properties;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ben
 * Date: 10/11/17
 * Time: 4:51 PM
 */
public class StringResolver {
    public static final String THE_KEY = "THE_STRING";
    private final MapResolver mapResolver;

    public StringResolver(final Map<String, String> ... otherResources) {
        this.mapResolver = new MapResolver(otherResources);
    }

    public String resolve(String str){
        final Map<String, String> primaryMap = new HashMap<>();
        primaryMap.put(THE_KEY, str);
        final Map<String, String> resolvedMap = mapResolver.resolve(primaryMap);
        return resolvedMap.get(THE_KEY);
    }
}
