package org.tools4j.tabular.service;

import org.tools4j.tabular.util.PropertiesRepo;

import java.util.Map;

/**
 * User: ben
 * Date: 26/10/17
 * Time: 6:37 AM
 */
public class DataSetProperties {
    private final char csvDelimiter;
    private final Character csvEscapedCharacterQuote;

    public DataSetProperties(final char csvDelimiter, final Character csvEscapedCharacterQuote, final Map<Character, ColumnMetaData> columnMetaData, final PropertiesRepo envVariables) {
        this.csvDelimiter = csvDelimiter;
        this.csvEscapedCharacterQuote = csvEscapedCharacterQuote;
    }

    public char getCsvDelimiter() {
        return csvDelimiter;
    }

    public Character getCsvEscapedCharacterQuote() {
        return csvEscapedCharacterQuote;
    }
}
