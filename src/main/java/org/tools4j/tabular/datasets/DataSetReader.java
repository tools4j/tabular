package org.tools4j.tabular.datasets;

import java.io.Reader;
import java.util.List;

public interface DataSetReader extends AutoCloseable {
    List<Reader> getTableCsvFiles();
}
