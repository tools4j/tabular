package org.tools4j.tabular.datasets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.tabular.commands.CommandMetadataFromProperties;
import org.tools4j.tabular.commands.CommandMetadataFromXml;
import org.tools4j.tabular.commands.CommandMetadatas;
import org.tools4j.tabular.config.ConfigResolverFromConfigFiles;
import org.tools4j.tabular.config.ConfigResolverFromSysAndEnvVars;
import org.tools4j.tabular.config.DirResolver;
import org.tools4j.tabular.config.UserDirResolver;
import org.tools4j.tabular.config.WorkingDirResolver;
import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.util.FileResolver;

import static org.tools4j.tabular.util.Constants.COMMAND_XML_FILE;

/**
 * User: ben
 * Date: 31/10/17
 * Time: 4:36 PM
 */
public class DataSetContextLoader {
    private final static Logger LOG = LoggerFactory.getLogger(DataSetContextLoader.class);
    private final PropertiesRepo propertiesFromConstructor;

    public DataSetContextLoader() {
        this(PropertiesRepo.empty());
    }

    public DataSetContextLoader(PropertiesRepo propertiesFromConstructor) {
        this.propertiesFromConstructor = propertiesFromConstructor;
    }

    public DataSetContext load() {
        LOG.info("==================== Resolving properties ====================");
        PropertiesRepo allProperties = new PropertiesRepo();
        allProperties.putAll(new ConfigResolverFromSysAndEnvVars().resolve());
        allProperties.putAll(propertiesFromConstructor);
        DirResolver workingDirResolver = new WorkingDirResolver();
        DirResolver userDirResolver = new UserDirResolver();
        FileResolver configFileResolver = new FileResolver(allProperties, workingDirResolver, userDirResolver);
        PropertiesRepo propertiesFromConfigFiles = new ConfigResolverFromConfigFiles(configFileResolver).resolve();
        allProperties.putAll(propertiesFromConfigFiles);
        allProperties = allProperties.resolveVariablesWithinValues();
        
        LOG.info("==================== Resolving csv tables ====================");
        FileResolver csvFileResolver = new FileResolver(allProperties, workingDirResolver, userDirResolver);
        DataSetResolver dataSetResolver = new DataSetResolver(csvFileResolver);
        DataSet<? extends Row> dataset = dataSetResolver.resolve();
        DataSet<? extends Row> returnDataSet = dataset.resolveVariablesInCells(allProperties);

        LOG.info("==================== Loading commands ====================");
        CommandMetadatas commandMetadatas;
        String commandXml = allProperties.get(COMMAND_XML_FILE, null);
        if (commandXml == null){
            LOG.info("Loading commandMetadata from properties.  (To load command metadata via new xml format, define a property '" + COMMAND_XML_FILE + "' with a valid path to the command xml file.)");
            CommandMetadataFromProperties commandMetadataFromProperties = new CommandMetadataFromProperties(allProperties);
            commandMetadatas = commandMetadataFromProperties.load();
        } else {
            LOG.info("Loading commandMetadata from xml");
            CommandMetadataFromXml commandMetadataFromXml = new CommandMetadataFromXml(allProperties);
            commandMetadatas = commandMetadataFromXml.load();
        }

        LOG.info("==================== Matching commands with rows ====================");
        long startTime = System.currentTimeMillis();
        DataSet<RowWithCommands> rowsWithCommands = returnDataSet.resolveCommands(commandMetadatas, allProperties);

        long endTime = System.currentTimeMillis();
        LOG.info("Finished resolving commands, took " + (endTime - startTime) + "ms");
        final DataSetContext appContext = new DataSetContext(rowsWithCommands, commandMetadatas, allProperties);
        return appContext;
    }
}
