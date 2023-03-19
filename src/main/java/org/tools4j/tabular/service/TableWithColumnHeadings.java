package org.tools4j.tabular.service;

import org.tools4j.tabular.datasets.Row;

import java.util.List;

/**
 * User: ben
 * Date: 15/11/17
 * Time: 6:41 AM
 */
public interface TableWithColumnHeadings<R extends Row> {
    List<R> getRows();
    List<String> getColumnHeadings();
    int size();
}
