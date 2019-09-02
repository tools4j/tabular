package org.tools4j.tabular.service;

import org.apache.log4j.Logger;
import org.tools4j.tabular.util.PropertiesRepo;
import org.tools4j.tabular.util.PropertiesRepoWithAdditionalTweakedContantStyleProperties;

import java.io.File;

/**
 * User: ben
 * Date: 31/10/17
 * Time: 4:36 PM
 */
public class DataSetContextFromDir {
    private final static Logger LOG = Logger.getLogger(DataSetContextFromDir.class);
    private final String configDir;

    public DataSetContextFromDir(final String configDir) {
        this.configDir = configDir;
    }

    public DataSetContext load() {
        LOG.info("Loading DataSet properties");
        final PropertiesRepo configFileProperties = new PropertiesRepo(configDir + "/config");

        PropertiesRepo configLocalFileProperties = null;
        if(new File(configDir + "/config-local.properties").exists()){
            LOG.info("Detected a config-local.properties file.  Using values from this file to override values in config.properties");
            configLocalFileProperties = new PropertiesRepo(configDir + "/config-local");
        }

        final PropertiesRepo allProperties = new PropertiesRepo();
        LOG.info("==================== config.properties file properties ====================");
        LOG.info(configFileProperties.toPrettyString());
        allProperties.putAll(configFileProperties);

        if(configLocalFileProperties != null) {
            LOG.info("==================== config-local.properties file properties ====================");
            LOG.info(configLocalFileProperties.toPrettyString());
            allProperties.putAll(configLocalFileProperties);
        }

        return load(configFileProperties);
    }

    private DataSetContext load(PropertiesRepo properties) {
        LOG.info("Loading DataSet CSV");
        final DataSetFromDir dataSetFromDir = new DataSetFromDir(configDir);
        DataSet dataSet = dataSetFromDir.get();
        final PropertiesRepo environmentVariables = new PropertiesRepoWithAdditionalTweakedContantStyleProperties(new EnvironmentVariables().load()).load();
        final PropertiesRepo systemProperties = new PropertiesRepoWithAdditionalTweakedContantStyleProperties(new SystemVariables().load()).load();
        return new DataSetContextLoader(dataSet, properties, environmentVariables, systemProperties).load();
    }
}
