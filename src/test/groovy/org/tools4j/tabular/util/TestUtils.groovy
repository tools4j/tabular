package org.tools4j.tabular.util

import org.tools4j.groovytables.Row
import org.tools4j.groovytables.Rows
import org.tools4j.tabular.service.DataSet
import org.tools4j.tabular.service.DataSetFromListOfMaps

class TestUtils {
    public static DataSet dataSetFromRows(Rows rows) {
        List<Map<String, String>> table = new ArrayList<>();
        Iterator<Row> rowsIterator = rows.iterator()
        while(rowsIterator.hasNext()){
            Iterator<Object> rowCells = rowsIterator.next().values.iterator()
            Iterator<String> columnHeadings = rows.columnHeadings.iterator();
            Map<String, String> row = new LinkedHashMap<>()
            while(rowCells.hasNext()){
                row.put(columnHeadings.next(), rowCells.next().toString())
            }
            table.add(row)
        }
        return new DataSetFromListOfMaps(table).asDataSet()
    }
}
