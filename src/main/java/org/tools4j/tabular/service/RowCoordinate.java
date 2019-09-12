package org.tools4j.tabular.service;

/**
 * User: ben
 * Date: 6/11/17
 * Time: 6:51 AM
 */
public interface RowCoordinate {
    int getRow();
    boolean isImmedicatelyFollowing(final RowCoordinate lastCoordinate);
    boolean isInSameColumnAs(final RowCoordinate lastCoordinate);
}
