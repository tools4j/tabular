package org.tools4j.tabular.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * User: ben
 * Date: 8/11/17
 * Time: 5:39 PM
 */
public interface Row extends Map<String, String>{
    int size();

    boolean isEmpty();

    boolean containsKey(Object key);

    boolean containsValue(Object value);

    String get(String key);

    Set<String> keySet();

    Collection<String> values();

    Set<Map.Entry<String,String>> entrySet();

    default String toCsv(){
        final StringBuilder sb = new StringBuilder();
        for(final String value: values()){
            if(sb.length() > 0) sb.append(", ");
            sb.append(value);
        }
        return sb.toString();
    }
}
