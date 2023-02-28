package org.tools4j.tabular.properties;

import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ben
 * Date: 10/11/17
 * Time: 4:51 PM
 */
public class StringResolver {
    private final StringSubstitutor stringSubstitutor;

    public StringResolver(final Map<String, String> ... otherResources) {
        Map<String, String> allProperties;
        if(otherResources.length > 1){
            allProperties = new HashMap<>();
            for (Map<String, String> otherResource : otherResources) {
                allProperties.putAll(otherResource);
            }
        } else {
            allProperties = otherResources[0];
        }
        stringSubstitutor = new StringSubstitutor(allProperties);
    }

    public String resolve(String str){
        return stringSubstitutor.replace(str);
    }
}
