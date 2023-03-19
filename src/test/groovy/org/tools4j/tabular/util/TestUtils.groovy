package org.tools4j.tabular.util

import org.tools4j.groovytables.Row
import org.tools4j.groovytables.Rows
import org.tools4j.tabular.datasets.DataSet
import org.tools4j.tabular.datasets.DataSetFromListOfMaps

class TestUtils {
    public static DataSet<org.tools4j.tabular.datasets.Row> dataSetFromRows(Rows rows) {
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
        return new DataSetFromListOfMaps(table).load()
    }
    
    public static boolean assertHasSameColumnsAndRows(DataSet expected, DataSet actual){
        assert actual.getColumnHeadings() == expected.getColumnHeadings()
        assert actual.getRows().size() == expected.getRows().size()
        
        for(int i=0; i<actual.getRows().size(); i++){
            Map<String, String> actualRowAsMap = new LinkedHashMap<>();
            actualRowAsMap.putAll(actual.getRow(i));
            
            Map<String, String> expectedRowAsMap = new LinkedHashMap<>();
            expectedRowAsMap.putAll(expected.getRow(i));
            
            assert actualRowAsMap == expectedRowAsMap
        }
        return true;
    }
}
