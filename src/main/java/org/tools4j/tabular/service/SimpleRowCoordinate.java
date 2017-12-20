package org.tools4j.tabular.service;

/**
 * User: ben
 * Date: 15/11/17
 * Time: 5:25 PM
 */
public class SimpleRowCoordinate implements RowCoordinate{
    private final int row;

    public SimpleRowCoordinate(final int row) {
        this.row = row;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public boolean isImmedicatelyFollowing(final RowCoordinate lastCoordinate) {
        return false;
    }

    @Override
    public boolean isInSameColumnAs(final RowCoordinate lastCoordinate) {
        return false;
    }
}
