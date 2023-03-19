package org.tools4j.tabular.datasets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ben
 * Date: 25/10/17
 * Time: 6:37 AM
 */
public class DataSetFromCsvFile implements DataSetLoader {
    private final CsvFile csvFile;

    public DataSetFromCsvFile(CsvFile csvFile) {
        this.csvFile = csvFile;
    }

    @Override
    public DataSet<? extends Row> load(){
        final List<String[]> csvData = csvFile.getRows();
        final List<RowFromMap> table = new ArrayList<>(csvData.size() - 1);

        final String[] firstRow = csvData.get(0);
        final List<String> columns = new ArrayList<>(Arrays.asList(firstRow));

        for(int i=1; i<csvData.size(); i++){
            final String[] line = csvData.get(i);
            final Map<String, String> row = new LinkedHashMap<>();
            for(int j=0; j<line.length && j<columns.size(); j++){
                final String columnName = columns.get(j);
                final String cellData = line[j];
                row.put(columnName, cellData);
            }
            table.add(new RowFromMap(row));
        }
        return new DataSet<>(columns, table);
    }
}
