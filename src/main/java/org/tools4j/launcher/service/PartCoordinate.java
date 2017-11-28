package org.tools4j.launcher.service;

/**
 * User: ben
 * Date: 6/11/17
 * Time: 6:24 AM
 */
public class PartCoordinate implements RowCoordinate {
    public final int row;
    public final int column;
    public final int part;

    public PartCoordinate(final int row, final int column, final int part) {
        this.row = row;
        this.column = column;
        this.part = part;
    }

    @Override
    public String toString() {
        return "[" + row + "," + column + "," + part + ']';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof PartCoordinate)) return false;

        final PartCoordinate that = (PartCoordinate) o;

        if (row != that.row) return false;
        if (column != that.column) return false;
        return part == that.part;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + column;
        result = 31 * result + part;
        return result;
    }

    public int compareTo(final PartCoordinate other) {
        int compare = Integer.compare(this.row, other.row);
        if(compare != 0){
            return compare;
        }
        compare = Integer.compare(this.column, other.column);
        if(compare != 0){
            return compare;
        }
        return Integer.compare(this.part, other.part);
    }

    @Override
    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getPart() {
        return part;
    }

    @Override
    public boolean isImmedicatelyFollowing(final RowCoordinate lastCoordinate) {
        return isInSameColumnAs(lastCoordinate)
                && ((PartCoordinate) lastCoordinate).part + 1 == part;
    }

    @Override
    public boolean isInSameColumnAs(final RowCoordinate lastCoordinate) {
        return ((PartCoordinate) lastCoordinate).row == row
                && ((PartCoordinate) lastCoordinate).column == column;
    }
}
