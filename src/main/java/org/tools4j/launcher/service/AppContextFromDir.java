package org.tools4j.launcher.service;

import org.apache.log4j.Logger;
import org.tools4j.launcher.util.PropertiesRepo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ben
 * Date: 25/10/17
 * Time: 5:24 PM
 */
public class AppContextFromDir {
    private final static Logger LOG = Logger.getLogger(AppContextFromDir.class);
    private final String configDir;
    private final PropertiesRepo propertyOverrides;

    public AppContextFromDir(final String configDir) {
        this(configDir, null);
    }

    public AppContextFromDir(final String configDir, final PropertiesRepo propertyOverrides) {
        this.configDir = configDir;
        this.propertyOverrides = propertyOverrides;
    }

    public AppContext load(){
        LOG.info("Loading shared config...");
        final SharedConfig sharedConfig = new SharedConfigFromDir(configDir).load();
        final Map<String, DataSetContext> dataSetsByName = new LinkedHashMap<>();
        final List<String> dataSetNames = sharedConfig.getDataSetNames();
        final PropertiesRepo sharedProperties = sharedConfig.asPropertiesRepo();

        LOG.info("Looping through dataSet names...");
        for(final String dataSetName: dataSetNames){
            final DataSetContext dataSetContext = new DataSetContextFromDir(configDir, dataSetName, sharedProperties, propertyOverrides).load();
            dataSetsByName.put(dataSetName, dataSetContext);
        }
        return new AppContext(dataSetsByName);
    }
}
