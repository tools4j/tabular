package org.tools4j.tabular.datasets;

import java.io.Reader;
import java.util.List;

public class DataSetReaderImpl implements DataSetReader {
    private final List<Reader> tableCsvFiles;

    public DataSetReaderImpl(List<Reader> tableCsvFiles) {
        this.tableCsvFiles = tableCsvFiles;
    }

    @Override
    public List<Reader> getTableCsvFiles() {
        return tableCsvFiles;
    }

    @Override
    public void close() throws Exception {
        for (Reader tableCsvFile : tableCsvFiles) {
            tableCsvFile.close();
        }
    }
}
