package org.tools4j.tabular.properties;

import java.io.StringReader;
import java.util.Properties;

/**
 * User: ben
 * Date: 8/11/17
 * Time: 5:32 PM
 */
public class PropertiesFromString {
    private final String str;

    public PropertiesFromString(final String str) {
        this.str = str;
    }

    public Properties load() {
        try {
            Properties properties = new java.util.Properties();
            properties.load(new StringReader(str));
            return properties;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
