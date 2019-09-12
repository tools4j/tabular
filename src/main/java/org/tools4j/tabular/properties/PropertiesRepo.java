package org.tools4j.tabular.properties;

import org.tools4j.tabular.util.IndentableStringBuilder;

import java.io.File;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;

/**
 * User: ben
 * Date: 24/10/17
 * Time: 6:18 AM
 */
public class PropertiesRepo {
    private final java.util.regex.Pattern ESCAPED_VARIABLE_PATTERN = java.util.regex.Pattern.compile("\\\\(\\$\\{)");
    private final Map<String,String> properties;

    public PropertiesRepo() {
        this(new HashMap<>());
    }

    public PropertiesRepo(final Properties properties) {
        this.properties = new HashMap<>();
        for(final Object key: properties.keySet()){
            this.properties.put((String) key, (String) properties.get(key));
        }
    }

    public PropertiesRepo(final Map<String, String> properties) {
        this.properties = new HashMap<>();
        this.properties.putAll(properties);
    }

    public PropertiesRepo(final File file) {
        this(new PropertiesFromFile(file).resolve());
    }

    public PropertiesRepo(final List<Reader> files) {
        this(new PropertiesFromReaders(files).resolve());
    }

    public PropertiesRepo(final String pathAndFilenameOfPropertiesFileWithoutExtension) {
        this(new PropertiesFromFileName(pathAndFilenameOfPropertiesFileWithoutExtension).asMap());
    }

    public PropertiesRepo(final PropertiesRepo repo) {
        this(repo.asMap());
    }

    public PropertiesRepo getWithPrefix(String prefix) {
        if(!prefix.endsWith(".")){
            prefix += ".";
        }
        final Properties returnProperties = new Properties();
        for(final String key: properties.keySet()){
            if(key.startsWith(prefix)){
                final String newKey = key.replace(prefix, "");
                returnProperties.put(newKey, properties.get(key));
            }
        }
        return new PropertiesRepo(returnProperties);
    }

    public int size() {
        return properties.size();
    }

    public String get(final String key) {
        return cleanValueOfEscapedVariables(properties.get(key));
    }

    public boolean getAsBoolean(final String key, final boolean defaultValue) {
        final String value = get(key);
        if(value == null){
            return defaultValue;
        } else {
            return Boolean.parseBoolean(value);
        }
    }

    public long getAsLong(final String key, final long defaultValue) {
        final String value = get(key);
        if(value == null){
            return defaultValue;
        } else {
            return Long.parseLong(value);
        }
    }

    public double getAsDouble(final String key, final double defaultValue) {
        final String value = get(key);
        if(value == null){
            return defaultValue;
        } else {
            return Double.parseDouble(value);
        }
    }

    public int getAsInt(final String key, final int defaultValue) {
        final String value = get(key);
        if(value == null){
            return defaultValue;
        } else {
            return Integer.parseInt(value);
        }
    }

    public Set<String> keySet() {
        final Set<String> returnSet = new LinkedHashSet<>();
        for(final Object key: properties.keySet()){
            returnSet.add((String) key);
        }
        return returnSet;
    }

    public String cleanValueOfEscapedVariables(String value){
        if(value == null){
            return null;
        }
        final Matcher matcher = ESCAPED_VARIABLE_PATTERN.matcher(value);
        value = matcher.replaceAll("$1");
        return value;
    }

    public String get(final String key, final String defaultValue) {
        final String value = get(key);
        if(value == null){
            return defaultValue;
        } else {
            return cleanValueOfEscapedVariables(value);
        }
    }

    public Set<String> getNextUniqueKeyParts() {
        final Set<String> uniqueKeyParts = new LinkedHashSet<>();
        for(final Object key: properties.keySet()){
            final String keyStr = (String) key;
            final String firstPartOfKey = keyStr.replaceFirst("\\..*", "");
            uniqueKeyParts.add(firstPartOfKey);
        }
        return uniqueKeyParts;
    }

    public void putAll(final PropertiesRepo other) {
        properties.putAll(other.properties);
    }

    @Override
    public String toString() {
        return "PropertiesRepo{" +
                "properties=" + properties +
                '}';
    }

    public Map<String, String> asMap() {
        return new HashMap<>(properties);
    }

    public PropertiesRepo resolveVariablesWithinValues() {
        return new PropertiesRepo(new ResolvedMap(asMap()).resolve());
    }

    public PropertiesRepo resolveVariablesWithinValues(final PropertiesRepo ... additionalPropertiesToHelpWithResolution) {
        final Map<String, String> additionalProperties = new HashMap<>();
        for(int i=0; i<additionalPropertiesToHelpWithResolution.length; i++){
            additionalProperties.putAll(additionalPropertiesToHelpWithResolution[i].asMap());
        }
        return new PropertiesRepo(new ResolvedMap(asMap(), additionalProperties).resolve());
    }

    public String toPrettyString() {
        return toPrettyString("    ");
    }

    public String toPrettyString(final String indent) {
        final IndentableStringBuilder sb = new IndentableStringBuilder(indent);
        sb.append("PropertiesRepo{\n");
        sb.activateIndent();
        for(final String key: properties.keySet()){
            sb.append(key).append("=").append(get(key)).append("\n");
        }
        sb.decactivateIndent();
        sb.append("}");
        return sb.toString();
    }

    public static PropertiesRepo empty() {
        return new PropertiesRepo(Collections.emptyMap());
    }

    public void put(final String key, final String value) {
        properties.put(key, value);
    }

    public String getMandatory(final String key) {
        return getMandatory(key, "Property '" + key + "' must be specified.");
    }

    public String getMandatory(final String key, final String message) {
        if(!properties.containsKey(key)){
            throw new IllegalStateException(message);
        } else {
            return properties.get(key);
        }
    }

    public boolean isEmpty() {
        return size() > 0;
    }
}
