package org.tools4j.tabular.datasets;

public interface DataSetLoader {
    DataSet<? extends Row> load();
}
