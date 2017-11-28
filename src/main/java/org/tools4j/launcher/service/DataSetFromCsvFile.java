package org.tools4j.launcher.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ben
 * Date: 25/10/17
 * Time: 6:37 AM
 */
public class DataSetFromCsvFile {
    private final CsvFile csvFile;

    public DataSetFromCsvFile(final CsvFile csvFile) {
        this.csvFile = csvFile;
    }

    public DataSet load(){
        final List<String[]> csvData = csvFile.getData();
        final List<String> columns = new ArrayList<>();
        final List<RowWithCommands> table = new ArrayList<>(csvData.size()); //could be off by one, but that's fine

        final String[] firstRow = csvData.get(0);
        columns.addAll(Arrays.asList(firstRow));

        for(int i=1; i<csvData.size(); i++){
            final String[] line = csvData.get(i);
            final Map<String, String> row = new LinkedHashMap<>();
            for(int j=0; j<line.length && j<columns.size(); j++){
                final String columnName = columns.get(j);
                final String cellData = line[j];
                row.put(columnName, cellData);
            }
            table.add(new RowWithCommands(row));
        }
        return new DataSet(columns, table);
    }
}
