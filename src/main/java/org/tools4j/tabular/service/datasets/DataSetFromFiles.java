package org.tools4j.tabular.service.datasets;

import org.tools4j.tabular.service.CsvFile;
import org.tools4j.tabular.service.Row;

import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: ben
 * Date: 26/10/17
 * Time: 6:15 AM
 */
public class DataSetFromFiles implements DataSetLoader {
    private final List<Reader> files;

    public DataSetFromFiles(List<Reader> files) {
        this.files = files;
    }

    @Override
    public DataSet<? extends Row> load(){
        List<CsvFile> csvFiles = files.stream().map(f -> CsvFile.fromReader(f)).collect(Collectors.toList());
        DataSetFromCsvFiles dataSetFromCsvFiles = new DataSetFromCsvFiles(csvFiles);
        return dataSetFromCsvFiles.load();
    }
}
