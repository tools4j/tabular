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

        final PropertiesRepo dataSetPropertiesWithAddedColumnAbbreviations = new PropertiesRepo();
        for(final String columnName: columnAbbreviations.keySet()){
            final String abbreviation = columnAbbreviations.get(columnName);
            dataSetPropertiesWithAddedColumnAbbreviations.put(abbreviation, "${"+columnName+"}");
        }

        final PropertiesRepo systemProperties = new PropertiesRepoWithAdditionalTweakedContantStyleProperties(new SystemVariables().load()).load();
        final PropertiesRepo environmentVariables = new PropertiesRepoWithAdditionalTweakedContantStyleProperties(new EnvironmentVariables().load()).load();


        final PropertiesRepo allProperties = new PropertiesRepo();
        LOG.info("==================== Config file properties ====================");
        LOG.info(dataSetProperties.toPrettyString());
        allProperties.putAll(dataSetProperties);

        LOG.info("==================== Configured abbreviations ====================");
        LOG.info(dataSetPropertiesWithAddedColumnAbbreviations.toPrettyString());
        allProperties.putAll(dataSetPropertiesWithAddedColumnAbbreviations);

        LOG.info("==================== Environment Variables ====================");
        LOG.info(environmentVariables.toPrettyString());
        allProperties.putAll(environmentVariables);

        LOG.info("==================== System Properties ====================");
        LOG.info(systemProperties.toPrettyString());
        allProperties.putAll(systemProperties);

        LOG.info("==================== Property Overrides ====================");
        LOG.info(propertyOverrides.toPrettyString());
        allProperties.putAll(propertyOverrides);

        final PropertiesRepo resolvedProperties = allProperties.resolveVariablesWithinValues();
        LOG.info("==================== Final Resolved Properties ====================");
        LOG.info(resolvedProperties.toPrettyString());

        LOG.info("Loading DataSet CSV");
        final DataSetFromDir dataSetFromDir = new DataSetFromDir(configDir, propertyOverrides);
        DataSet dataSet = dataSetFromDir.load();

        LOG.info("Resolving variables in dataset table cells");
        dataSet = dataSet.resolveVariablesInCells(dataSetProperties, resolvedProperties);

        LOG.info("Loading commandMetadata from properties");
        CommandMetadataFromProperties commandMetadataFromProperties = new CommandMetadataFromProperties(resolvedProperties);
        CommandMetadatas commandMetadatas = commandMetadataFromProperties.load();

        LOG.info("Resolving commands for dataset rows");
        dataSet = dataSet.resolveCommands(commandMetadatas, resolvedProperties);

        LOG.info("Finished loading dataset");
        return new DataSetContext(dataSet, commandMetadatas, resolvedProperties);
    }
}
