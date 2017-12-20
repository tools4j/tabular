package org.tools4j.tabular.service;

/**
 * User: ben
 * Date: 6/11/17
 * Time: 6:52 AM
 */
public class PositionInRowCoordinate implements RowCoordinate{
    private final int rowIndex;
    private final int positionInRow;
    private final int length;

    public PositionInRowCoordinate(final int rowIndex, final int positionInRow, final int length) {
        this.rowIndex = rowIndex;
        this.positionInRow = positionInRow;
        this.length = length;
    }

    @Override
    public int getRow() {
        return rowIndex;
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
