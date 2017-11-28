package org.tools4j.launcher.service;

import org.tools4j.launcher.util.PropertiesRepo;

import java.util.Map;

/**
 * User: ben
 * Date: 26/10/17
 * Time: 6:37 AM
 */
public class DataSetProperties {
    private final char csvDelimiter;
    private final Character csvEscapedCharacterQuote;
    private final Map<Character, ColumnMetaData> columnMetaData;
    private final PropertiesRepo envVariables;

    public DataSetProperties(final char csvDelimiter, final Character csvEscapedCharacterQuote, final Map<Character, ColumnMetaData> columnMetaData, final PropertiesRepo envVariables) {
        this.csvDelimiter = csvDelimiter;
        this.csvEscapedCharacterQuote = csvEscapedCharacterQuote;
        this.columnMetaData = columnMetaData;
        this.envVariables = envVariables;
    }

    public char getCsvDelimiter() {
        return csvDelimiter;
    }

    public Character getCsvEscapedCharacterQuote() {
        return csvEscapedCharacterQuote;
    }

    public PropertiesRepo getEnvVariables() {
        return envVariables;
    }

    public Map<Character, ColumnMetaData> getColumnMetaData() {
        return columnMetaData;
    }
}
