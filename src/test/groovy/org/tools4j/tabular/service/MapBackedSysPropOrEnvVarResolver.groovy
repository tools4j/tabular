package org.tools4j.tabular.service

import org.tools4j.tabular.config.SysPropAndEnvVarResolver;

class MapBackedSysPropOrEnvVarResolver implements SysPropAndEnvVarResolver {
    private final Map<String, String> map;

    MapBackedSysPropOrEnvVarResolver() {
        this(new LinkedHashMap<String, String>())
    }

    MapBackedSysPropOrEnvVarResolver(Map<String, String> map){
        this.map = new LinkedHashMap<>(map);
    }

    @Override
    public Optional<String> resolve(String propOrEnvVarName) {
        if(map.containsKey(propOrEnvVarName)){
            return Optional.of(map.get(propOrEnvVarName))
        } else {
            return Optional.empty()
        }
    }

    public void put(String key, String value){
        map.put(key, value);
    }
}
