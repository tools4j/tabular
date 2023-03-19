package org.tools4j.tabular.datasets;

import org.tools4j.tabular.util.FileResolver;

import java.io.Reader;
import java.util.Optional;

public class DataSetResolver {
    public static final String TABULAR_TABLE_CSV_URL_PROP = "tabular_csv_url";
    public static final String TABULAR_TABLE_CSV_PATH_PROP = "tabular_csv_path";
    public static final String TABULAR_TABLE_CSV_FILE_NAME_DEFAULT = "table.csv";

    private final FileResolver fileResolver;

    public DataSetResolver(FileResolver fileResolver) {
        this.fileResolver = fileResolver;
    }

    public DataSet<? extends Row> resolve(){
        Optional<Reader> tableCsvFile = fileResolver.resolveFile(TABULAR_TABLE_CSV_URL_PROP, TABULAR_TABLE_CSV_PATH_PROP, TABULAR_TABLE_CSV_FILE_NAME_DEFAULT);
        if(!tableCsvFile.isPresent()) {
            throw new IllegalArgumentException("Could not resolve table csv file.");
        }
        return new DataSetFromReader(tableCsvFile.get()).load();
    }
}
