package org.tools4j.tabular.javafx;

/**
 * User: ben
 * Date: 21/11/17
 * Time: 6:43 AM
 */
public interface PropertyPersistenceService {
    void save(String key, String value);
    String getByKey(String String);
}
