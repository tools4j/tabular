package org.tools4j.tabular.properties;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class PropertiesFromReader {
    private final Reader reader;

    public PropertiesFromReader(Reader reader) {
        this.reader = reader;
    }

    public Properties resolve(){
        Properties properties = new Properties();
        try {
            properties.load(reader);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return properties;
    }
}
