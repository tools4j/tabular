package org.tools4j.tabular.datasets;

import org.tools4j.tabular.config.TabularConstants;
import org.tools4j.tabular.config.TabularProperties;
import org.tools4j.tabular.util.FileResolver;

import java.io.Reader;
import java.util.Optional;

public class DataSetResolver {

    private final FileResolver fileResolver;

    public DataSetResolver(FileResolver fileResolver) {
        this.fileResolver = fileResolver;
    }

    public DataSet<? extends Row> resolve(){
        Optional<Reader> tableCsvFile = fileResolver.resolveFile(TabularProperties.TABLE_CSV_URL, TabularProperties.TABLE_CSV_PATH, TabularConstants.TABULAR_TABLE_CSV_FILE_NAME_DEFAULT);
        if(!tableCsvFile.isPresent()) {
            throw new IllegalArgumentException("Could not resolve table csv file.");
        }
        return new DataSetFromReader(tableCsvFile.get()).load();
    }
}
