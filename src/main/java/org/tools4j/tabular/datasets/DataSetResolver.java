package org.tools4j.tabular.datasets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.tabular.config.TabularConstants;
import org.tools4j.tabular.config.TabularProperties;
import org.tools4j.tabular.util.TabularDirAndFileResolver;

import java.io.Reader;
import java.util.Optional;

public class DataSetResolver {
    private final static Logger LOG = LoggerFactory.getLogger(DataSetResolver.class);
    private final TabularDirAndFileResolver tabularDirAndFileResolver;

    public DataSetResolver(TabularDirAndFileResolver tabularDirAndFileResolver) {
        this.tabularDirAndFileResolver = tabularDirAndFileResolver;
    }

    public DataSet<? extends Row> resolve(){
        LOG.info("Looking for table csv file");
        Optional<Reader> tableCsvFile = tabularDirAndFileResolver.resolveFile(TabularProperties.TABLE_CSV_URL, TabularProperties.TABLE_CSV_PATH, TabularConstants.TABULAR_TABLE_CSV_FILE_NAME_DEFAULT);
        if(!tableCsvFile.isPresent()) {
            throw new IllegalArgumentException("Could not resolve table csv file.");
        }
        return new DataSetFromReader(tableCsvFile.get()).load();
    }
}
