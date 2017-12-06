package org.tools4j.launcher.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: ben
 * Date: 6/12/17
 * Time: 6:25 AM
 */
public class DataSetFromStringMap {
    private final List<String> columns;
    private final List<Map<String, String>> table;

    public DataSetFromStringMap(final List<String> columns, final List<Map<String, String>> table) {
        this.columns = columns;
        this.table = table;
    }

    public DataSet asDataSet(){
        return new DataSet(columns, table.stream().map((row) -> new RowWithCommands(row)).collect(Collectors.toList()));
    }
}
