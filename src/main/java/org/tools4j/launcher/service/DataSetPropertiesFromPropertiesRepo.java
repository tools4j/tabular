package org.tools4j.launcher.service;

import org.tools4j.launcher.util.PropertiesRepo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: ben
 * Date: 26/10/17
 * Time: 6:40 AM
 */
public class DataSetPropertiesFromPropertiesRepo {
    private final PropertiesRepo properties;

    public DataSetPropertiesFromPropertiesRepo(final PropertiesRepo properties) {
        this.properties = properties;
    }

    public DataSetProperties load(){
        //csv delimiter
        final String delimiterStr = properties.get("app.csv.delimiter", ",");
        if (delimiterStr.length() != 1) {
            throw new IllegalArgumentException("Delimiter specified in app.csv.delimiter must be just one character long.  Delimiter found: '" + delimiterStr + "'");
        }
        final Character delimiter = delimiterStr.charAt(0);

        //escaped character quote
        final String escapedCharacterQuoteStr = properties.get("app.csv.escapedCharacterQuote");
        final Character escapedCharacterQuote;
        if (escapedCharacterQuoteStr == null) {
            escapedCharacterQuote = null;
        } else if (escapedCharacterQuoteStr.length() != 1) {
            throw new IllegalArgumentException("Value specified by app.csv.escapedCharacterQuote must be just one character long.  Value found: '" + escapedCharacterQuoteStr + "'");
        } else {
            escapedCharacterQuote = escapedCharacterQuoteStr.charAt(0);
        }

        //column metadata
        final Map<Character, ColumnMetaData> columnMetaDataMap = new LinkedHashMap<>();
        final PropertiesRepo columnsData = properties.getWithPrefix("columns");
        final Set<String> columnAbbreviations = columnsData.getNextUniqueKeyParts();
        for(final String columnAbbreviationStr: columnAbbreviations){
            if(columnAbbreviationStr.length() != 1){
                throw new IllegalArgumentException("Columns in csv file must be denoted by their single character abbreviation. Column found columns." + columnAbbreviationStr);
            }
            final PropertiesRepo columnData = columnsData.getWithPrefix(columnAbbreviationStr);
            final String columnName = columnData.get("name");
            final ColumnMetaData columnMetaData = new ColumnMetaData(columnAbbreviationStr.charAt(0), columnName);
            columnMetaDataMap.put(columnAbbreviationStr.charAt(0), columnMetaData);
        }

        //environment variables
        final PropertiesRepo envVariables = properties.getWithPrefix("env");

        //Construct DataSetProperties
        return new DataSetProperties(delimiter, escapedCharacterQuote, columnMetaDataMap, envVariables);
    }
}
