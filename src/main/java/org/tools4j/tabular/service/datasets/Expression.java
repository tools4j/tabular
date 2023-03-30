package org.tools4j.tabular.service.datasets;

import org.tools4j.tabular.service.Row;

public interface Expression {
    String resolve(Row row);
}
