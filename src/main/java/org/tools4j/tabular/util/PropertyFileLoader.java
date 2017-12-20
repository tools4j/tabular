package org.tools4j.tabular.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileLoader {

    public Properties getProperties(final String propertiesFileNameWithoutExtension) {
        Properties config = getPropertiesFromClasspath(propertiesFileNameWithoutExtension);
        if (config == null) {
            config = getPropertiesFromWorkingDirectory(propertiesFileNameWithoutExtension);
        }
        return config;
    }

    private Properties getPropertiesFromWorkingDirectory(final String propertiesFileNameWithoutExtension) {
        try {
            Properties config = new java.util.Properties();
            String path = System.getProperty("user.dir");
            java.io.FileInputStream fis = new java.io.FileInputStream(new java.io.File(path + "/" + propertiesFileNameWithoutExtension + ".properties"));
            config.load(fis);
            fis.close();
            return config;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Properties getPropertiesFromClasspath(final String propertiesFileNameWithoutExtension) {
        Properties config = new Properties();
        InputStream in = this.getClass().getResourceAsStream("/" + propertiesFileNameWithoutExtension + ".properties");
        if (in == null) {
            return null;
        }
        try {
            config.load(in);
            in.close();
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
        return config;
    }
}
