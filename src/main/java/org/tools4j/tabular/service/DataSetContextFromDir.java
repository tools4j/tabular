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
    private final PropertiesRepo propertyOverrides;

    public DataSetContextFromDir(final String configDir) {
        this(configDir, new PropertiesRepo());
    }

    public DataSetContextFromDir(final String configDir, final PropertiesRepo propertyOverrides) {
        this.configDir = configDir;
        this.propertyOverrides = propertyOverrides;
    }

    public DataSetContext load() {
        LOG.info("Loading DataSet properties");
        final PropertiesRepo configFileProperties = new PropertiesRepo(configDir + "/config");

        PropertiesRepo configLocalFileProperties = null;
        if(new File(configDir + "/config-local.properties").exists()){
            LOG.info("Detected a config-local.properties file.  Using values from this file to override values in config.properties");
            configLocalFileProperties = new PropertiesRepo(configDir + "/config-local");
        }

        LOG.info("Loading column abbreviations");
        final PropertiesRepo columnAbbreviations = configFileProperties.getWithPrefix("app.column.abbreviations");

        final PropertiesRepo dataSetPropertiesWithAddedColumnAbbreviations = new PropertiesRepo();
        for(final String columnName: columnAbbreviations.keySet()){
            final String abbreviation = columnAbbreviations.get(columnName);
            dataSetPropertiesWithAddedColumnAbbreviations.put(abbreviation, "${"+columnName+"}");
        }

        final PropertiesRepo systemProperties = new PropertiesRepoWithAdditionalTweakedContantStyleProperties(new SystemVariables().load()).load();
        final PropertiesRepo environmentVariables = new PropertiesRepoWithAdditionalTweakedContantStyleProperties(new EnvironmentVariables().load()).load();


        final PropertiesRepo allProperties = new PropertiesRepo();
        LOG.info("==================== config.properties file properties ====================");
        LOG.info(configFileProperties.toPrettyString());
        allProperties.putAll(configFileProperties);

        if(configLocalFileProperties != null) {
            LOG.info("==================== config-local.properties file properties ====================");
            LOG.info(configLocalFileProperties.toPrettyString());
            allProperties.putAll(configLocalFileProperties);
        }

        LOG.info("==================== Environment Variables ====================");
        LOG.info(environmentVariables.toPrettyString());
        allProperties.putAll(environmentVariables);

        LOG.info("==================== System Properties ====================");
        LOG.info(systemProperties.toPrettyString());
        allProperties.putAll(systemProperties);

        LOG.info("==================== Property Overrides ====================");
        LOG.info(propertyOverrides.toPrettyString());
        allProperties.putAll(propertyOverrides);

        LOG.info("==================== Resolving all variables in properties ====================");
        final PropertiesRepo resolvedProperties = allProperties.resolveVariablesWithinValues();

        LOG.info("==================== Configured abbreviations ====================");
        LOG.info(dataSetPropertiesWithAddedColumnAbbreviations.toPrettyString());
        resolvedProperties.putAll(dataSetPropertiesWithAddedColumnAbbreviations);

        LOG.info("==================== Final Resolved Properties ====================");
        LOG.info(resolvedProperties.toPrettyString());

        LOG.info("Loading DataSet CSV");
        final DataSetFromDir dataSetFromDir = new DataSetFromDir(configDir, propertyOverrides);
        DataSet dataSet = dataSetFromDir.load();

        LOG.info("Resolving variables in dataset table cells");
        dataSet = dataSet.resolveVariablesInCells(configFileProperties, resolvedProperties);

        LOG.info("Loading commandMetadata from properties");
        CommandMetadataFromProperties commandMetadataFromProperties = new CommandMetadataFromProperties(resolvedProperties);
        CommandMetadatas commandMetadatas = commandMetadataFromProperties.load();

        LOG.info("Resolving commands for dataset rows");
        dataSet = dataSet.resolveCommands(commandMetadatas, resolvedProperties);

        LOG.info("Finished loading dataset");
        return new DataSetContext(dataSet, commandMetadatas, resolvedProperties);
    }
}
