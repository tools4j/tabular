package org.tools4j.tabular.config;

import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.util.FileResolver;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public class ConfigResolverFromConfigFiles {
    //File urls
    public static final String TABULAR_CONFIG_FILE_URL_PROP = "tabular_config_file_url";
    public static final String TABULAR_LOCAL_CONFIG_FILE_URL_PROP = "tabular_local_config_file_url";

    //File paths
    public static final String TABULAR_CONFIG_FILE_PATH_PROP = "tabular_config_file_path";
    public static final String TABULAR_LOCAL_CONFIG_FILE_PATH_PROP = "tabular_local_config_file_path";

    //Defaults
    public static final String TABULAR_CONFIG_FILE_NAME_DEFAULT = "config.properties";
    public static final String TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT = "config-local.properties";

    private final FileResolver fileResolver;

    public ConfigResolverFromConfigFiles(FileResolver fileResolver) {
        this.fileResolver = fileResolver;
    }

    public PropertiesRepo resolve() {
        Optional<Reader> configFile = fileResolver.resolveFile(TABULAR_CONFIG_FILE_URL_PROP, TABULAR_CONFIG_FILE_PATH_PROP, TABULAR_CONFIG_FILE_NAME_DEFAULT);
        PropertiesRepo allProperties = new PropertiesRepo();
        if (!configFile.isPresent()) {
            throw new IllegalArgumentException("Could not find config file.");
        }
        allProperties.putAll(new PropertiesRepo(configFile.get()));

        Optional<Reader> localConfigFile = fileResolver.resolveFile(TABULAR_LOCAL_CONFIG_FILE_URL_PROP, TABULAR_LOCAL_CONFIG_FILE_PATH_PROP, TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT);
        if (localConfigFile.isPresent()){
            allProperties.putAll(new PropertiesRepo(localConfigFile.get()));
        }
        return allProperties;
    }
}
