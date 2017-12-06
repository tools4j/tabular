package org.tools4j.launcher.service;

import org.tools4j.launcher.util.PropertiesRepo;

/**
 * User: ben
 * Date: 26/10/17
 * Time: 6:15 AM
 */
public class DataSetFromDir {
    private final String configDir;
    private final PropertiesRepo dataSetProperties;

    public DataSetFromDir(final String configDir, final PropertiesRepo dataSetProperties) {
        this.configDir = configDir;
        this.dataSetProperties = dataSetProperties;
    }

    public DataSet load(){
        final String propertiesPathAndFileNameWithoutExtension = configDir + "/config";
        try {
            final DataSetPropertiesFromPropertiesRepo dataSetPropertiesFromPropertiesRepo = new DataSetPropertiesFromPropertiesRepo(dataSetProperties);
            final DataSetProperties dataSetProperties = dataSetPropertiesFromPropertiesRepo.load();
            final CsvFile csvFile = new CsvFile(configDir + "/table.csv", dataSetProperties.getCsvDelimiter(), dataSetProperties.getCsvEscapedCharacterQuote());
            DataSetFromCsvFile dataSetFromCsvFile = new DataSetFromCsvFile(csvFile);
            return dataSetFromCsvFile.load();
        } catch (Exception e){
            throw new IllegalArgumentException("Error parsing properties file at " + propertiesPathAndFileNameWithoutExtension + ".properites", e);
        }
    }
}
