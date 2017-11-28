package org.tools4j.launcher.service;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * User: ben
 * Date: 2/11/17
 * Time: 6:21 AM
 */
public class Result<R extends Row> implements Comparable<Result<R>>, Pretty, Map<String, String>, Row{
    private final int rowIndex;
    private final R row;
    private final LinkedList<RowCoordinate> matchCoordinates;
    private final LinkedHashSet<Term> matchingTerms;
    private final int matchingScore;

    public Result(final RowCoordinate matchCoordinate, final R row, final LinkedHashSet<Term> matchingTerms, final int matchingScore) {
        this(singletonLinkedList(matchCoordinate), row, matchingTerms, matchingScore);
    }

    public Result(final LinkedList<RowCoordinate> matchCoordinates, final R row, final LinkedHashSet<Term> matchingTerms, final int matchingScore) {
        this.rowIndex = matchCoordinates.get(0).getRow();
        this.matchCoordinates = matchCoordinates;
        this.row = row;
        this.matchingTerms = matchingTerms;
        this.matchingScore = matchingScore;
    }

    public boolean allPartsMatch(final SearchQuery searchQuery){
        if(matchingTerms.size() == 1
                && searchQuery.getQuery().equals(matchingTerms.iterator().next())){
            return true;
        }
        for(final Part queryPart: searchQuery.getPartsInQuery()){
            if(!matchingTerms.contains(queryPart)){
                return false;
            }
        }
        return true;
    }

    public Result<R> add(final Result<R> other){
        return add(other, (previousResult, newResult) -> previousResult.matchingScore + newResult.matchingScore);
    }

    public Result<R> add(final Result<R> other, final BiFunction<Result<R>, Result<R>, Integer> matchingScoreCalculator){
        if(this.rowIndex != other.rowIndex){
            throw new IllegalArgumentException("Cannot add to a Result from a different row.  This: " + toString() + " other: " + other.toString());
        }
        final LinkedHashSet<Term> matchingTerms = new LinkedHashSet<>(this.matchingTerms);
        matchingTerms.addAll(other.matchingTerms);

        final LinkedList<RowCoordinate> matchCoordinates = new LinkedList<>(this.matchCoordinates);
        matchCoordinates.addAll(other.matchCoordinates);

        return new Result<R>(matchCoordinates, row, matchingTerms, matchingScoreCalculator.apply(this, other));
    }

    @Override
    public int compareTo(final Result<R> o) {
        int comparison = -1 * Integer.compare(this.matchingScore, o.matchingScore);
        if(comparison != 0) {
            return comparison;
        } else {
            return Integer.compare(rowIndex, o.rowIndex);
        }
    }

    @Override
    public String toPrettyString(final String indent){
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("%1$-5s", matchingScore));
        sb.append(String.format("%1$-5s", rowIndex+":"+matchCoordinates));
        sb.append(" matchingTerms=").append(matchingTerms);
        sb.append(" row=").append(row);
        return sb.toString();
    }


    public LinkedList<RowCoordinate> getMatchCoordinates() {
        return matchCoordinates;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public static <T> LinkedList<T> singletonLinkedList(final T item){
        final LinkedList<T> list = new LinkedList<>();
        list.add(item);
        return list;
    }

    public int getMatchingScore() {
        return matchingScore;
    }

    @Override
    public int size() {
        return row.size();
    }

    @Override
    public boolean isEmpty() {
        return row.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return row.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return row.containsValue(value);
    }

    @Override
    public String get(final String key) {
        return row.get(key);
    }

    @Override
    public String get(final Object key) {
        return row.get(key);
    }

    @Override
    public String put(final String key, final String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String remove(final Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(final Map<? extends String, ? extends String> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return row.keySet();
    }

    @Override
    public Collection<String> values() {
        return row.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return row.entrySet();
    }

    public R getRow(){
        return row;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Result)) return false;

        final Result<?> result = (Result<?>) o;

        return rowIndex == result.rowIndex;
    }

    @Override
    public int hashCode() {
        return rowIndex;
    }
}
