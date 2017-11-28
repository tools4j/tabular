package org.tools4j.launcher.service;

import org.tools4j.launcher.util.PropertiesFile;
import org.tools4j.launcher.util.PropertiesRepo;

/**
 * User: ben
 * Date: 26/10/17
 * Time: 6:15 AM
 */
public class DataSetFromDir {
    private final String dataSetName;
    private final String configDir;

    public DataSetFromDir(final String configDir, final String dataSetName, final PropertiesRepo environmentVariables) {
        this.configDir = configDir;
        this.dataSetName = dataSetName;
    }

    public DataSet load(){
        final String propertiesPathAndFileNameWithoutExtension = configDir + "/" + dataSetName;
        try {
            final PropertiesRepo dataSetPropertiesRepo = new PropertiesFile(propertiesPathAndFileNameWithoutExtension).getProperties();
            final DataSetPropertiesFromPropertiesRepo dataSetPropertiesFromPropertiesRepo = new DataSetPropertiesFromPropertiesRepo(dataSetPropertiesRepo);
            final DataSetProperties dataSetProperties = dataSetPropertiesFromPropertiesRepo.load();

            final CsvFile csvFile = new CsvFile(configDir + "/" + dataSetName + ".csv", dataSetProperties.getCsvDelimiter(), dataSetProperties.getCsvEscapedCharacterQuote());
            DataSetFromCsvFile dataSetFromCsvFile = new DataSetFromCsvFile(csvFile);
            return dataSetFromCsvFile.load();
        } catch (Exception e){
            throw new IllegalArgumentException("Error parsing properties file at " + propertiesPathAndFileNameWithoutExtension + ".properites", e);
        }
    }
}
