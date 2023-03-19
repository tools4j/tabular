package org.tools4j.tabular.config;

import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.properties.PropertiesRepoWithAdditionalTweakedContantStyleProperties;
import org.tools4j.tabular.service.EnvironmentVariables;
import org.tools4j.tabular.service.SystemVariables;

public class ConfigResolverFromSysAndEnvVars {
    public PropertiesRepo resolve(){
        PropertiesRepo environmentVariables = new PropertiesRepoWithAdditionalTweakedContantStyleProperties(new EnvironmentVariables().load()).load();
        PropertiesRepo systemProperties = new PropertiesRepoWithAdditionalTweakedContantStyleProperties(new SystemVariables().load()).load();
        PropertiesRepo allProperties = new PropertiesRepo();
        allProperties.putAll(environmentVariables);
        allProperties.putAll(systemProperties);
        return allProperties;
    }
}
