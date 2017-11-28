package org.tools4j.launcher.service;

/**
 * User: ben
 * Date: 26/10/17
 * Time: 6:38 AM
 */
public class ColumnMetaData {
    private final char columnAbbreviation;
    private final String columnLongName;

    public ColumnMetaData(final char columnAbbreviation, final String columnLongName) {
        this.columnAbbreviation = columnAbbreviation;
        this.columnLongName = columnLongName;
    }
}
