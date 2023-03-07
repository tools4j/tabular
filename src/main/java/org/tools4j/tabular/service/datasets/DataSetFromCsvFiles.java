package org.tools4j.tabular.service.datasets;

import org.tools4j.tabular.service.CsvFile;
import org.tools4j.tabular.service.RowFromMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * User: ben
 * Date: 25/10/17
 * Time: 6:37 AM
 */
public class DataSetFromCsvFiles implements DataSetLoader {
    private final List<CsvFile> csvFiles;

    public DataSetFromCsvFiles(CsvFile ... csvFiles) {
        this.csvFiles = Arrays.asList(csvFiles);
    }

    public DataSetFromCsvFiles(final List<CsvFile> csvFiles) {
        this.csvFiles = csvFiles;
    }

    @Override
    public DataSet<RowFromMap> load(){
        CsvFileData csvFileData = CsvFileData.EMPTY;
        for (CsvFile csvFile : csvFiles) {
            CsvFileData other = loadCsv(csvFile);
            csvFileData = csvFileData.add(other);
        }
        return new DataSet<>(csvFileData.columns, csvFileData.rows);
    }

    private CsvFileData loadCsv(CsvFile file){
        final List<String[]> csvData = file.getRows();
        final List<String> columns = new ArrayList<>();
        final List<RowFromMap> table = new ArrayList<>(csvData.size()); //could be off by one, but that's fine

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
            table.add(new RowFromMap(row));
        }
        return new CsvFileData(columns, table);
    }

    private static class CsvFileData {
        private final List<String> columns;
        private final List<RowFromMap> rows;
        private static final CsvFileData EMPTY = new CsvFileData(Collections.emptyList(), Collections.emptyList());

        private CsvFileData(List<String> columns, List<RowFromMap> rows) {
            this.columns = columns;
            this.rows = rows;
        }

        public CsvFileData add(CsvFileData other){
            if(!this.equals(EMPTY) && !columnsMatch(other)){
                throw new IllegalStateException("Columns do not match between multiple csv files " + this.columns + " other " + other.columns);
            }
            List<RowFromMap> union = new ArrayList<>(rows);
            union.addAll(other.rows);
            return new CsvFileData(other.columns, union);
        }

        public boolean columnsMatch(CsvFileData other){
            return this.columns.equals(other.columns);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CsvFileData that = (CsvFileData) o;
            return columns.equals(that.columns) &&
                    rows.equals(that.rows);
        }

        @Override
        public int hashCode() {
            return Objects.hash(columns, rows);
        }
    }
}
