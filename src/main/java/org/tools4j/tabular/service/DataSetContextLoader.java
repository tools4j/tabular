package org.tools4j.tabular.service;

import org.apache.log4j.Logger;
import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.service.commands.CommandMetadataFromXml;

import static org.tools4j.tabular.util.Constants.COMMAND_XML_FILE;

/**
 * User: ben
 * Date: 31/10/17
 * Time: 4:36 PM
 */
public class DataSetContextLoader {
    private final static Logger LOG = Logger.getLogger(DataSetContextLoader.class);
    private final DataSet<RowFromMap> dataSet;
    private final PropertiesRepo allProperties;
    private final PropertiesRepo environmentVariables;
    private final PropertiesRepo systemProperties;

    public DataSetContextLoader(
            DataSet<RowFromMap> dataSet,
            PropertiesRepo allProperties,
            PropertiesRepo environmentVariables,
            PropertiesRepo systemProperties) {

        this.dataSet = dataSet;
        this.allProperties = allProperties;
        this.environmentVariables = environmentVariables;
        this.systemProperties = systemProperties;
    }

    public DataSetContext load() {
        LOG.info("Loading column abbreviations");
        final PropertiesRepo columnAbbreviations = allProperties.getWithPrefix("app.column.abbreviations");

        final PropertiesRepo dataSetPropertiesWithAddedColumnAbbreviations = new PropertiesRepo();
        for (final String columnName : columnAbbreviations.keySet()) {
            final String abbreviation = columnAbbreviations.get(columnName);
            dataSetPropertiesWithAddedColumnAbbreviations.put(abbreviation, "${" + columnName + "}");
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
        DataSet<RowFromMap> returnDataSet = dataSet.resolveVariablesInCells(allProperties, resolvedProperties);

        LOG.info("==================== Loading Command Metadata ====================");
        CommandMetadatas commandMetadatas;
        String commandXml = resolvedProperties.get(COMMAND_XML_FILE, null);
        if (commandXml == null){
            LOG.info("Loading commandMetadata from properties.  (To load command metadata via new xml format, define a property '" + COMMAND_XML_FILE + "' with a valid path to the command xml file.)");
            CommandMetadataFromProperties commandMetadataFromProperties = new CommandMetadataFromProperties(resolvedProperties);
            commandMetadatas = commandMetadataFromProperties.load();
        } else {
            LOG.info("Loading commandMetadata from xml");
            CommandMetadataFromXml commandMetadataFromXml = new CommandMetadataFromXml(resolvedProperties);
            commandMetadatas = commandMetadataFromXml.load();
        }
        
        LOG.info("Resolving commands for dataset rows");
        long startTime = System.currentTimeMillis();
        DataSet<RowWithCommands> rowsWithCommands = returnDataSet.resolveCommands(commandMetadatas, resolvedProperties);

        long endTime = System.currentTimeMillis();
        LOG.info("Finished loading dataset, took " + (endTime - startTime) + "ms");
        return new DataSetContext(rowsWithCommands, commandMetadatas, resolvedProperties);
    }
}
