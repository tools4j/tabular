package org.tools4j.launcher.service;

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
}
