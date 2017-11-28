package org.tools4j.launcher.service;

import org.tools4j.launcher.util.PropertiesRepo;

/**
 * User: ben
 * Date: 31/10/17
 * Time: 4:20 PM
 */
public class SharedConfigFromDir {
    private final String configDir;

    public SharedConfigFromDir(final String configDir) {
        this.configDir = configDir;
    }

    public SharedConfig load(){
        PropertiesRepo configProperties = new PropertiesRepo(configDir + "/config");
        PropertiesRepo envVars = new EnvironmentVariables().load();
        envVars.putAll(new SystemVariables().load());
        envVars = envVars.resolveVariablesWithinValues();
        configProperties = configProperties.resolveVariablesWithinValues(envVars);
        return new SharedConfig(envVars, configProperties);
    }
}
