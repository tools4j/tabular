package org.tools4j.tabular.service;

/**
 * User: ben
 * Date: 1/11/17
 * Time: 6:07 PM
 */
public class PartsToRowIndex {
    private final PartsSource parts;
    private final int rowIndex;

    public PartsToRowIndex(final PartsSource parts, final int rowIndex) {
        this.parts = parts;
        this.rowIndex = rowIndex;
    }
}
