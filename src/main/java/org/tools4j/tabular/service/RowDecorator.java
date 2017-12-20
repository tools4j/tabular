package org.tools4j.tabular.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * User: ben
 * Date: 8/11/17
 * Time: 5:40 PM
 */
public class RowDecorator implements Row {
    private final Row row;

    public RowDecorator(final Row row) {
        this.row = row;
    }

    @Override
    public int size() {
        return row.size();
    }

    @Override
    public boolean isEmpty() {
        return row.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return row.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return row.containsKey(value);
    }

    @Override
    public String get(final Object key) {
        return row.get(key);
    }

    @Override
    public String put(final String key, final String value) {
        return row.put(key, value);
    }

    @Override
    public String remove(final Object key) {
        return row.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends String> m) {
        row.putAll(m);
    }

    @Override
    public void clear() {
        row.clear();
    }

    @Override
    public String get(final String key) {
        return row.get(key);
    }

    @Override
    public Set<String> keySet() {
        return row.keySet();
    }

    @Override
    public Collection<String> values() {
        return row.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return row.entrySet();
    }

    @Override
    public String toString() {
        return row.toString();
    }
}
