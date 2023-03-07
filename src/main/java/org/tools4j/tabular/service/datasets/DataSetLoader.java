package org.tools4j.tabular.service.datasets;

import org.tools4j.tabular.service.Row;

public interface DataSetLoader {
    DataSet<? extends Row> load();
}
