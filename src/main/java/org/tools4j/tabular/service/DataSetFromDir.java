package org.tools4j.tabular.service;

import org.tools4j.tabular.util.PropertiesRepo;

import java.util.function.Supplier;

/**
 * User: ben
 * Date: 26/10/17
 * Time: 6:15 AM
 */
public class DataSetFromDir implements Supplier<DataSet<?>> {
    private final String configDir;

    public DataSetFromDir(final String configDir) {
        this.configDir = configDir;
    }

    @Override
    public DataSet<?> get(){
        final String propertiesPathAndFileNameWithoutExtension = configDir + "/config";
        try {
            final CsvFile csvFile = new CsvFile(configDir + "/table.csv", ',', '"');
            DataSetFromCsvFile dataSetFromCsvFile = new DataSetFromCsvFile(csvFile);
            return dataSetFromCsvFile.load();
        } catch (Exception e){
            throw new IllegalArgumentException("Error parsing properties file at " + propertiesPathAndFileNameWithoutExtension + ".properites", e);
        }
    }
}
