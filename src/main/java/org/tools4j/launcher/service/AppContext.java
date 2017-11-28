package org.tools4j.launcher.service;

import java.util.Map;

/**
 * User: ben
 * Date: 30/10/17
 * Time: 5:30 PM
 */
public class AppContext {
    private final Map<String, DataSetContext> dataSetsByName;

    public AppContext(final Map<String, DataSetContext> dataSetsByName) {
        this.dataSetsByName = dataSetsByName;
    }

    public DataSetContext first(){
        return dataSetsByName.get(dataSetsByName.keySet().iterator().next());
    }
}
