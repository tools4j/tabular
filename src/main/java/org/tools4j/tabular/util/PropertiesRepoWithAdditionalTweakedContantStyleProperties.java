package org.tools4j.tabular.util;

import org.tools4j.tabular.util.PropertiesRepo;
import org.tools4j.tabular.util.PropertiesRepoLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * This class will _repeat_ any properties with 'Constant Style' keys and replace with 'Property Style' keys.
 * e.g. if a property exists "JAVA_HOME", an _additional_ property with key "java.home" will
 * be added to the PropertiesRepo.
 *
 * The load method looks for any properties whose key starts with an upper-case letter and only
 * consists of upper-case letters and underscores.
 *
 * This class exists so that environment variables can be used to override Java properties.  In the
 * case of Linux (and I maybe Mac) environment variables cannot be specified with dots ".".  In such
 * cases, the user/configurer can use underscores (and capital letters) instead.  And the keys will
 * be transformed.
 */
public class PropertiesRepoWithAdditionalTweakedContantStyleProperties implements PropertiesRepoLoader {
    private final PropertiesRepo source;

    public PropertiesRepoWithAdditionalTweakedContantStyleProperties(final PropertiesRepo source) {
        this.source = source;
    }

    @Override
    public PropertiesRepo load() {
        final Map<String, String> properties = source.asMap();
        final Map<String, String> convertedProperties = new HashMap<>();
        for(final String key: properties.keySet()){
            if(isConstantStyle(key)){
                convertedProperties.put(toPropertiesStyle(key), properties.get(key));
            }
        }
        properties.putAll(convertedProperties);
        return new PropertiesRepo(properties);
    }

    private String toPropertiesStyle(final String key) {
        if(key == null){
            return null;
        }
        return key.toLowerCase().replaceAll("_", ".");
    }

    private boolean isConstantStyle(final String key) {
        return key.matches("[A-Z]+?[[A-Z]_]*");
    }
}
