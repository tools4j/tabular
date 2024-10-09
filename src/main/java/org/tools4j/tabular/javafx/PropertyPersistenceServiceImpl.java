package org.tools4j.tabular.javafx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * User: ben
 * Date: 21/11/17
 * Time: 6:43 AM
 */
public class PropertyPersistenceServiceImpl implements PropertyPersistenceService {
    private final static Logger LOG = LoggerFactory.getLogger(PropertyPersistenceServiceImpl.class);
    private final String propertiesFileNameWithoutExtension;
    private final Properties properties;
    
    public PropertyPersistenceServiceImpl(final String propertiesFileNameWithoutExtension) {
        this.propertiesFileNameWithoutExtension = propertiesFileNameWithoutExtension;
        createPropertiesFileInWorkingDirectoryIfRequired();
        this.properties = getPropertiesFromWorkingDirectory();
    }

    @Override
    public void save(final String key, final String value) {
        properties.setProperty(key, value);
        savePropertiesToWorkingDirectory();
    }

    @Override
    public String getByKey(final String key) {
        return properties.getProperty(key);
    }

    private void createPropertiesFileInWorkingDirectoryIfRequired() {
        try {
            String path = System.getProperty("user.dir");
            final String filePath = path + "/" + propertiesFileNameWithoutExtension + ".properties";
            final File file = new File(filePath);
            if(!file.exists()){
                LOG.info("Creating new file to save table settings at: " + filePath);
                file.createNewFile();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private Properties getPropertiesFromWorkingDirectory() {
        try {
            Properties config = new java.util.Properties();
            String path = System.getProperty("user.dir");
            FileInputStream fis = new FileInputStream(path + "/" + propertiesFileNameWithoutExtension + ".properties");
            config.load(fis);
            fis.close();
            return config;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void savePropertiesToWorkingDirectory() {
        try {
            String path = System.getProperty("user.dir");
            FileOutputStream fos = new FileOutputStream(path + "/" + propertiesFileNameWithoutExtension + ".properties");
            properties.store(fos, "Please do not modify this file manually, is created and updated automatically");
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
