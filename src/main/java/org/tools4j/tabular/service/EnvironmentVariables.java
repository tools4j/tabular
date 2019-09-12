package org.tools4j.tabular.service;

import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.properties.PropertiesRepoLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ben
 * Date: 26/10/17
 * Time: 5:31 PM
 */
public class EnvironmentVariables implements PropertiesRepoLoader {
    @Override
    public PropertiesRepo load(){
        final Map<String, String> envVars = System.getenv();
        final Map<String, String> envVarsWithUnderscoresReplaced = new HashMap<>(envVars);
        for(final String key: envVars.keySet()){
            if(key.contains("_")){
                envVarsWithUnderscoresReplaced.put(key.replaceAll("_", "."), envVars.get(key));
            }
        }
        return new PropertiesRepo(envVarsWithUnderscoresReplaced);
    }
}
