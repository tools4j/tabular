package org.tools4j.tabular.config;

import static org.tools4j.tabular.config.TabularConstants.TABULAR_CONFIG_FILE_NAME_DEFAULT;
import static org.tools4j.tabular.config.TabularProperties.CONFIG_FILE_PATH;
import static org.tools4j.tabular.config.TabularProperties.CONFIG_FILE_URL;
import static org.tools4j.tabular.config.TabularConstants.TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT;
import static org.tools4j.tabular.config.TabularProperties.LOCAL_CONFIG_FILE_PATH;
import static org.tools4j.tabular.config.TabularProperties.LOCAL_CONFIG_FILE_URL;

import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.util.FileResolver;

import java.io.Reader;
import java.util.Optional;

public class ConfigResolverFromConfigFiles {
    private final FileResolver fileResolver;

    public ConfigResolverFromConfigFiles(FileResolver fileResolver) {
        this.fileResolver = fileResolver;
    }

    /**
     * This method looks config files at different locations.  It will look for a config file, and a local-config file.
     * Usually, in a corporate environment, the config file will be managed by a central team, and the local-config file
     * will be managed by the individual user.  The local-config file will override the config file.
     * returned.  
     * <br/>
     * config.properties is looked for in this order:
     * <ol>
     *     <li>At the URL specified by the property {@value TabularProperties#CONFIG_FILE_URL}.</li>
     *     <li>At the location specified by property {@value TabularProperties#CONFIG_FILE_PATH}.</li>
     *     <li>In the folder specified by property {@value TabularProperties#CONFIG_DIR}, named {@value TabularConstants#TABULAR_CONFIG_FILE_NAME_DEFAULT}</li>
     *     <li>In the users home folder under a subfolder named 'tabular'. (i.e. ./tabular/), named {@value TabularConstants#TABULAR_CONFIG_FILE_NAME_DEFAULT}</li>
     *     <li>In the current working directory named {@value TabularConstants#TABULAR_CONFIG_FILE_NAME_DEFAULT}</li>
     *     <li>In the folder specified by property {@value TabularProperties#TABULAR_HOME}, named {@value TabularConstants#TABULAR_CONFIG_FILE_NAME_DEFAULT}</li>
     * </ol>
     * <br/>
     * config-local.properties is looked for in this order:
     * <ol>
     *     <li>At the URL specified by the property {@value TabularProperties#LOCAL_CONFIG_FILE_URL}.</li>
     *     <li>At the location specified by property {@value TabularProperties#LOCAL_CONFIG_FILE_PATH}.</li>
     *     <li>In the folder specified by property {@value TabularProperties#CONFIG_DIR}, named {@value TabularConstants#TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT}</li>
     *     <li>In the users home folder under a subfolder named 'tabular'. (i.e. ./tabular/), named {@value TabularConstants#TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT}</li>
     *     <li>In the current working directory named {@value TabularConstants#TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT}</li>
     *     <li>In the folder specified by property {@value TabularProperties#TABULAR_HOME}, named {@value TabularConstants#TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT}</li>
     * </ol>
     */
    
    public PropertiesRepo resolve() {
        Optional<Reader> configFile = fileResolver.resolveFile(CONFIG_FILE_URL, CONFIG_FILE_PATH, TABULAR_CONFIG_FILE_NAME_DEFAULT);
        PropertiesRepo allProperties = new PropertiesRepo();
        if (!configFile.isPresent()) {
            throw new IllegalArgumentException("Could not find config file.");
        }
        allProperties.putAll(new PropertiesRepo(configFile.get()));

        Optional<Reader> localConfigFile = fileResolver.resolveFile(LOCAL_CONFIG_FILE_URL, LOCAL_CONFIG_FILE_PATH, TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT);
        if (localConfigFile.isPresent()){
            allProperties.putAll(new PropertiesRepo(localConfigFile.get()));
        }
        return allProperties;
    }
}
