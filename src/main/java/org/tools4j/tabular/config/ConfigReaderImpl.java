package org.tools4j.tabular.config;

import java.io.Reader;
import java.util.List;

public class ConfigReaderImpl implements ConfigReader {
    private final List<Reader> tableCsvFiles;
    private final List<Reader> configPropertiesFiles;
    private final List<Reader> localConfigPropertiesFiles;

    public ConfigReaderImpl(List<Reader> tableCsvFiles, List<Reader> configPropertiesFiles, List<Reader> localConfigPropertiesFiles) {
        this.tableCsvFiles = tableCsvFiles;
        this.configPropertiesFiles = configPropertiesFiles;
        this.localConfigPropertiesFiles = localConfigPropertiesFiles;
    }

    @Override
    public List<Reader> getTableCsvFiles() {
        return tableCsvFiles;
    }

    @Override
    public List<Reader> getConfigPropertiesFiles() {
        return configPropertiesFiles;
    }

    @Override
    public List<Reader> getLocalConfigPropertiesFiles() {
        return localConfigPropertiesFiles;
    }

    @Override
    public void close() throws Exception {
        for (Reader tableCsvFile : tableCsvFiles) {
            tableCsvFile.close();
        }
        for (Reader configPropertiesFile : configPropertiesFiles) {
            configPropertiesFile.close();
        }
        for (Reader localConfigPropertiesFile : localConfigPropertiesFiles) {
            localConfigPropertiesFile.close();
        }
    }
}
