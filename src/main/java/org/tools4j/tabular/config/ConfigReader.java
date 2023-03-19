package org.tools4j.tabular.config;

import java.io.Reader;
import java.util.List;

public interface ConfigReader extends AutoCloseable {
    List<Reader> getConfigPropertiesFiles();
    List<Reader> getLocalConfigPropertiesFiles();
}
