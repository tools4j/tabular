package org.tools4j.launcher.service;

import org.apache.log4j.Logger;
import org.tools4j.launcher.util.PropertiesRepo;

/**
 * User: ben
 * Date: 31/10/17
 * Time: 4:36 PM
 */
public class DataSetContextFromDir {
    private final static Logger LOG = Logger.getLogger(DataSetContextFromDir.class);

    private final String configDir;
    private final String dataSetName;
    private final PropertiesRepo sharedProperties;
    private final PropertiesRepo propertyOverrides;

    public DataSetContextFromDir(final String configDir, final String dataSetName, final PropertiesRepo sharedProperties) {
        this(configDir, dataSetName, sharedProperties, new PropertiesRepo());
    }

    public DataSetContextFromDir(final String configDir, final String dataSetName, final PropertiesRepo sharedProperties, final PropertiesRepo propertyOverrides) {
        this.configDir = configDir;
        this.dataSetName = dataSetName;
        this.sharedProperties = sharedProperties;
        this.propertyOverrides = propertyOverrides;
    }

    public DataSetContext load() {
        LOG.info("Loading DataSet: " + dataSetName);
        final DataSetFromDir dataSetFromDir = new DataSetFromDir(configDir, dataSetName, sharedProperties);
        DataSet dataSet = dataSetFromDir.load();

        LOG.info("Loading DataSet properties");
        PropertiesRepo dataSetProperties = new PropertiesRepo(configDir + "/" + dataSetName);

        LOG.info("Loading column abbreviations");
        final PropertiesRepo columnAbbreviations = dataSetProperties.getWithPrefix("app.column.abbreviations");

        for(final String columnName: columnAbbreviations.keySet()){
            final String abbreviation = columnAbbreviations.get(columnName);
            dataSetProperties.put(abbreviation, "${"+columnName+"}");
        }

        LOG.info("Resolving variables in dataset properties");
        dataSetProperties = dataSetProperties.resolveVariablesWithinValues(sharedProperties);

        LOG.info("Resolving variables in dataset table cells");
        dataSet = dataSet.resolveVariablesInCells(dataSetProperties, sharedProperties);
        PropertiesRepo dataSetPropertiesWithSharedPropertiesAndOverrides = new PropertiesRepo(sharedProperties);
        dataSetPropertiesWithSharedPropertiesAndOverrides.putAll(dataSetProperties);
        dataSetPropertiesWithSharedPropertiesAndOverrides.putAll(propertyOverrides);
        dataSetPropertiesWithSharedPropertiesAndOverrides = dataSetPropertiesWithSharedPropertiesAndOverrides.resolveVariablesWithinValues();

        LOG.info("Loading commandMetadata from properties");
        CommandMetadataFromProperties commandMetadataFromProperties = new CommandMetadataFromProperties(dataSetPropertiesWithSharedPropertiesAndOverrides);
        CommandMetadatas commandMetadatas = commandMetadataFromProperties.load();

        LOG.info("Resolving commands for dataset rows");
        dataSet = dataSet.resolveCommands(commandMetadatas, dataSetPropertiesWithSharedPropertiesAndOverrides);

        LOG.info("Finished loading dataset");
        return new DataSetContext(dataSetName, dataSet, dataSetPropertiesWithSharedPropertiesAndOverrides);
    }
}
