package org.tools4j.launcher.service;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: ben
 * Date: 8/11/17
 * Time: 5:40 PM
 */
public class RowFromMap implements Row {
    private final Map<String, String> map;

    public RowFromMap(final Map<String, String> map) {
        this.map = map;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return map.containsKey(value);
    }

    @Override
    public String get(final Object key) {
        return map.get(key);
    }

    @Override
    public String put(final String key, final String value) {
        return map.put(key, value);
    }

    @Override
    public String remove(final Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends String> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public String get(final String key) {
        return map.get(key);
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<String> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return map.entrySet();
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
