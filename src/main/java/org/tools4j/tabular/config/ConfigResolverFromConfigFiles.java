package org.tools4j.tabular.config;

import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.util.FileResolver;

import java.io.Reader;
import java.util.Optional;

public class ConfigResolverFromConfigFiles {
    private final FileResolver fileResolver;

    public ConfigResolverFromConfigFiles(FileResolver fileResolver) {
        this.fileResolver = fileResolver;
    }

    public PropertiesRepo resolve() {
        Optional<Reader> configFile = fileResolver.resolveFile(TabularProperties.TABULAR_CONFIG_FILE_URL_PROP, TabularProperties.TABULAR_CONFIG_FILE_PATH_PROP, TabularProperties.TABULAR_CONFIG_FILE_NAME_DEFAULT);
        PropertiesRepo allProperties = new PropertiesRepo();
        if (!configFile.isPresent()) {
            throw new IllegalArgumentException("Could not find config file.");
        }
        allProperties.putAll(new PropertiesRepo(configFile.get()));

        Optional<Reader> localConfigFile = fileResolver.resolveFile(TabularProperties.TABULAR_LOCAL_CONFIG_FILE_URL_PROP, TabularProperties.TABULAR_LOCAL_CONFIG_FILE_PATH_PROP, TabularProperties.TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT);
        if (localConfigFile.isPresent()){
            allProperties.putAll(new PropertiesRepo(localConfigFile.get()));
        }
        return allProperties;
    }
}
