package org.tools4j.launcher.service;

import org.tools4j.launcher.util.PropertiesRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ben
 * Date: 31/10/17
 * Time: 4:20 PM
 */
public class SharedConfig {
    private final PropertiesRepo configProperties;
    private final PropertiesRepo envVars;

    public SharedConfig(final PropertiesRepo envVars, final PropertiesRepo configProperties) {
        this.envVars = envVars;
        this.configProperties = configProperties;
    }

    public List<String> getDataSetNames() {
        final List<String> dataSetNames = new ArrayList<>();
        final PropertiesRepo dataSets = configProperties.getWithPrefix("app.dataset");
        for(final String key: dataSets.keySet()){
            dataSetNames.add(dataSets.get(key));
        }
        return dataSetNames;
    }

    public Map<String, String> asMap(){
        final Map<String, String> map = new HashMap<>();
        map.putAll(envVars.asMap());
        map.putAll(configProperties.asMap());
        return map;
    }

    public PropertiesRepo asPropertiesRepo(){
        final PropertiesRepo returnRepo = new PropertiesRepo(envVars);
        returnRepo.putAll(configProperties);
        return returnRepo;
    }

}
