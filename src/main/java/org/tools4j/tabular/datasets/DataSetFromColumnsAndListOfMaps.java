package org.tools4j.tabular.datasets;

import org.tools4j.tabular.datasets.RowFromMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: ben
 * Date: 6/12/17
 * Time: 6:25 AM
 */
public class DataSetFromColumnsAndListOfMaps implements DataSetLoader {
    private final List<String> columns;
    private final List<Map<String, String>> table;

    public DataSetFromColumnsAndListOfMaps(final List<String> columns, final List<Map<String, String>> table) {
        this.columns = columns;
        this.table = table;
    }

    @Override
    public DataSet<Row> load(){
        return new DataSet<>(columns, table.stream().map((row) -> (Row) new RowFromMap(row)).collect(Collectors.toList()));
    }
}
