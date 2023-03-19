package org.tools4j.tabular.datasets;

import java.io.Reader;

/**
 * User: ben
 * Date: 26/10/17
 * Time: 6:15 AM
 */
public class DataSetFromReader implements DataSetLoader {
    private final Reader file;

    public DataSetFromReader(Reader file) {
        this.file = file;
    }

    @Override
    public DataSet<? extends Row> load(){
        CsvFile csvFile = CsvFile.fromReader(file);
        DataSetFromCsvFile dataSetFromCsvFile = new DataSetFromCsvFile(csvFile);
        return dataSetFromCsvFile.load();
    }
}
