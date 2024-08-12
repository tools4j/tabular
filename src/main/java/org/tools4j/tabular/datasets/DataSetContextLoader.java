package org.tools4j.tabular.datasets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.tabular.commands.CommandMetadataFromXml;
import org.tools4j.tabular.commands.CommandMetadatas;
import org.tools4j.tabular.config.ConfigResolverFromConfigFiles;
import org.tools4j.tabular.config.ConfigResolverFromSysAndEnvVars;
import org.tools4j.tabular.config.DirResolver;
import org.tools4j.tabular.config.ConfigDirResolver;
import org.tools4j.tabular.config.UserDirResolver;
import org.tools4j.tabular.config.WorkingDirResolver;
import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.service.datasets.ExpressionCompiler;
import org.tools4j.tabular.service.datasets.FreemarkerCompiler;
import org.tools4j.tabular.util.TabularDirAndFileResolver;

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
        DirResolver configDirResolver = new ConfigDirResolver(allProperties);
        TabularDirAndFileResolver tabularDirAndFileResolver = new TabularDirAndFileResolver(allProperties, workingDirResolver, userDirResolver, configDirResolver);
        PropertiesRepo propertiesFromConfigFiles = new ConfigResolverFromConfigFiles(tabularDirAndFileResolver).resolve();
        allProperties.putAll(propertiesFromConfigFiles);
        allProperties.resolveVariablesWithinValues();
        
        LOG.info("==================== Resolving csv tables ====================");
        DataSetResolver dataSetResolver = new DataSetResolver(tabularDirAndFileResolver);
        DataSet<? extends Row> dataset = dataSetResolver.resolve();
        DataSet<? extends Row> returnDataSet = dataset.resolveVariablesInCells(allProperties);

        LOG.info("==================== Loading commands ====================");
        CommandMetadatas commandMetadatas;
        LOG.info("Loading commandMetadata from xml");
        CommandMetadataFromXml commandMetadataFromXml = new CommandMetadataFromXml(allProperties, tabularDirAndFileResolver);
        commandMetadatas = commandMetadataFromXml.load();

        LOG.info("==================== Compiling commands ====================");
        ExpressionCompiler expressionCompiler = new FreemarkerCompiler(allProperties);
        commandMetadatas.compile(expressionCompiler);
        
        LOG.info("==================== Resolving commands for dataset rows ====================");
        long startTime = System.currentTimeMillis();
        DataSet<RowWithCommands> rowsWithCommands = returnDataSet.resolveCommands(commandMetadatas, allProperties);

        long endTime = System.currentTimeMillis();
        LOG.info("Finished resolving commands, took " + (endTime - startTime) + "ms");
        return new DataSetContext(rowsWithCommands, commandMetadatas, allProperties);
    }
}
