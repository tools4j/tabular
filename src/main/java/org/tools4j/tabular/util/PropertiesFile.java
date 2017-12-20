package org.tools4j.tabular.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: ben
 * Date: 24/10/17
 * Time: 6:20 AM
 */
public class PropertiesFile {
    public final String propertiesPathAndFileNameWithoutExtension;

    public PropertiesFile(final String propertiesPathAndFileNameWithoutExtension) {
        this.propertiesPathAndFileNameWithoutExtension = propertiesPathAndFileNameWithoutExtension;
    }

    public PropertiesRepo getProperties() {
        final Properties config = getPropertiesFromFileSystemOrClasspath();
        return new PropertiesRepo(config);
    }

    public Map<String, String> asMap() {
        final Properties properties = getPropertiesFromFileSystemOrClasspath();
        final Map<String, String> map = new HashMap<>();
        for(final Object key: properties.keySet()){
            map.put((String) key, (String) properties.get(key));
        }
        return map;
    }

    private Properties getPropertiesFromFileSystemOrClasspath() {
        Properties config = getPropertiesFromClasspath();
        if (config == null) {
            config = getPropertiesFromWorkingDirectory();
        }
        return config;
    }

    private java.util.Properties getPropertiesFromWorkingDirectory() {
        try {
            java.util.Properties config = new java.util.Properties();
            String path = System.getProperty("user.dir");
            java.io.FileInputStream fis = new java.io.FileInputStream(new java.io.File(path + "/" + propertiesPathAndFileNameWithoutExtension + ".properties"));
            config.load(fis);
            fis.close();
            return config;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private java.util.Properties getPropertiesFromClasspath() {
        java.util.Properties config = new java.util.Properties();
        InputStream in = this.getClass().getResourceAsStream("/" + propertiesPathAndFileNameWithoutExtension + ".properties");
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
