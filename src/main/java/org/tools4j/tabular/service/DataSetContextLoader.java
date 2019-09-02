package org.tools4j.tabular.service;

import org.apache.log4j.Logger;
import org.tools4j.tabular.util.PropertiesRepo;

/**
 * User: ben
 * Date: 31/10/17
 * Time: 4:36 PM
 */
public class DataSetContextLoader {
    private final static Logger LOG = Logger.getLogger(DataSetContextLoader.class);
    private final DataSet dataSet;
    private final PropertiesRepo allProperties;
    private final PropertiesRepo environmentVariables;
    private final PropertiesRepo systemProperties;

    public DataSetContextLoader(DataSet dataSet, PropertiesRepo allProperties, PropertiesRepo environmentVariables, PropertiesRepo systemProperties) {
        this.dataSet = dataSet;
        this.allProperties = allProperties;
        this.environmentVariables = environmentVariables;
        this.systemProperties = systemProperties;
    }

    public DataSetContext load() {
        LOG.info("Loading column abbreviations");
        final PropertiesRepo columnAbbreviations = allProperties.getWithPrefix("app.column.abbreviations");

        final PropertiesRepo dataSetPropertiesWithAddedColumnAbbreviations = new PropertiesRepo();
        for(final String columnName: columnAbbreviations.keySet()){
            final String abbreviation = columnAbbreviations.get(columnName);
            dataSetPropertiesWithAddedColumnAbbreviations.put(abbreviation, "${"+columnName+"}");
        }

        LOG.info("==================== Environment Variables ====================");
        LOG.info(environmentVariables.toPrettyString());
        allProperties.putAll(environmentVariables);

        LOG.info("==================== System Properties ====================");
        LOG.info(systemProperties.toPrettyString());
        allProperties.putAll(systemProperties);

        LOG.info("==================== Resolving all variables in properties ====================");
        final PropertiesRepo resolvedProperties = allProperties.resolveVariablesWithinValues();

        LOG.info("==================== Configured abbreviations ====================");
        LOG.info(dataSetPropertiesWithAddedColumnAbbreviations.toPrettyString());
        resolvedProperties.putAll(dataSetPropertiesWithAddedColumnAbbreviations);

        LOG.info("==================== Final Resolved Properties ====================");
        LOG.info(resolvedProperties.toPrettyString());

        LOG.info("Resolving variables in dataset table cells");
        DataSet<?> returnDataSet = dataSet.resolveVariablesInCells(allProperties, resolvedProperties);

        LOG.info("Loading commandMetadata from properties");
        CommandMetadataFromProperties commandMetadataFromProperties = new CommandMetadataFromProperties(resolvedProperties);
        CommandMetadatas commandMetadatas = commandMetadataFromProperties.load();

        LOG.info("Resolving commands for dataset rows");
        returnDataSet = returnDataSet.resolveCommands(commandMetadatas, resolvedProperties);

        LOG.info("Finished loading dataset");
        return new DataSetContext(returnDataSet, commandMetadatas, resolvedProperties);
    }
}
