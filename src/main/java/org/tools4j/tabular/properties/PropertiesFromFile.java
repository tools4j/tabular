package org.tools4j.tabular.properties;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFromFile {
    private final File file;

    public PropertiesFromFile(File file) {
        this.file = file;
    }

    public Properties resolve(){
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(file));
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
