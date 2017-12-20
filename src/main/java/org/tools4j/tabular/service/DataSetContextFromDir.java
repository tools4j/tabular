package org.tools4j.tabular.service;

import org.apache.log4j.Logger;
import org.tools4j.tabular.util.PropertiesRepo;

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
        PropertiesRepo dataSetProperties = new PropertiesRepo(configDir + "/config");

        LOG.info("Loading column abbreviations");
        final PropertiesRepo columnAbbreviations = dataSetProperties.getWithPrefix("app.column.abbreviations");

        for(final String columnName: columnAbbreviations.keySet()){
            final String abbreviation = columnAbbreviations.get(columnName);
            dataSetProperties.put(abbreviation, "${"+columnName+"}");
        }

        LOG.info("Resolving variables in dataset properties");
        PropertiesRepo dataSetPropertiesWithSharedPropertiesAndOverrides = new PropertiesRepo();
        dataSetPropertiesWithSharedPropertiesAndOverrides.putAll(new EnvironmentVariables().load());
        dataSetPropertiesWithSharedPropertiesAndOverrides.putAll(new SystemVariables().load());
        dataSetPropertiesWithSharedPropertiesAndOverrides.putAll(dataSetProperties);
        dataSetPropertiesWithSharedPropertiesAndOverrides.putAll(propertyOverrides);
        dataSetPropertiesWithSharedPropertiesAndOverrides = dataSetPropertiesWithSharedPropertiesAndOverrides.resolveVariablesWithinValues();

        LOG.info("Loading DataSet CSV");
        final DataSetFromDir dataSetFromDir = new DataSetFromDir(configDir, propertyOverrides);
        DataSet dataSet = dataSetFromDir.load();

        LOG.info("Resolving variables in dataset table cells");
        dataSet = dataSet.resolveVariablesInCells(dataSetProperties, dataSetPropertiesWithSharedPropertiesAndOverrides);

        LOG.info("Loading commandMetadata from properties");
        CommandMetadataFromProperties commandMetadataFromProperties = new CommandMetadataFromProperties(dataSetPropertiesWithSharedPropertiesAndOverrides);
        CommandMetadatas commandMetadatas = commandMetadataFromProperties.load();

        LOG.info("Resolving commands for dataset rows");
        dataSet = dataSet.resolveCommands(commandMetadatas, dataSetPropertiesWithSharedPropertiesAndOverrides);

        LOG.info("Finished loading dataset");
        return new DataSetContext(dataSet, commandMetadatas, dataSetPropertiesWithSharedPropertiesAndOverrides);
    }
}
