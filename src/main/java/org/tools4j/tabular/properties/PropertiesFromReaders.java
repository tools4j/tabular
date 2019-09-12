package org.tools4j.tabular.properties;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesFromReaders {
    private final List<Reader> readers;

    public PropertiesFromReaders(List<Reader> readers) {
        this.readers = readers;
    }

    public Properties resolve(){
        Properties properties = new Properties();
        for (Reader reader : readers) {
            try {
                properties.load(reader);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return properties;
    }
}
