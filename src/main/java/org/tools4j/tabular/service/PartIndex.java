package org.tools4j.tabular.service;

import com.google.common.collect.ArrayListMultimap;
import org.tools4j.tabular.util.IndentableStringBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * User: ben
 * Date: 3/11/17
 * Time: 6:39 AM
 */
public class PartIndex<R extends Row> implements Pretty {
    private final ArrayListMultimap<Part, PartCoordinate> partsToRowIndexes;
    private final TableWithColumnHeadings<R> table;

    public PartIndex(final TableWithColumnHeadings<R> tableWithColumnHeadings) {
        this.table = tableWithColumnHeadings;
        this.partsToRowIndexes = ArrayListMultimap.create();

        final List<R> rows = table.getRows();
        for(int r=0; r<rows.size(); r++){
            final Map<String, String> row = rows.get(r);
            final Iterator<String> columnNames = row.keySet().iterator();
            for(int c=0; columnNames.hasNext(); c++){
                final String columnName = columnNames.next();
                final String cellValue = row.get(columnName);
                final Iterator<Part> parts = new WordsAsString(cellValue).getAllParts().iterator();
                for(int p=0; parts.hasNext(); p++){
                    final Part part = parts.next();
                    for(final Part combination: part.getCombinations() ){
                        partsToRowIndexes.put(combination.toLowerCase(), new PartCoordinate(r, c, p));
                    }
                }
            }
        }
    }

    public Results<R> search(final String query){
        return search(new SearchQuery.QueryParser(query).parse());
    }

    public Results<R> returnAll(){
        final Set<Result<R>> set = new HashSet<>();
        for(int i=0; i<table.getRows().size(); i++){
            final R row = table.getRows().get(i);
            set.add(new Result<R>(new SimpleRowCoordinate(i), row, new LinkedHashSet<>(), 0));
        }
        return new Results<R>(SearchQuery.EMPTY, set);
    }

    private Results<R> search(final SearchQuery query) {
        final Results<R> results = new Results<>(query);
        for(final Part part: query.getPartsInQuery()){
            for(PartCoordinate matchCoordinate: partsToRowIndexes.get(part.toLowerCase())) {
                final Result<R> result = new Result<>(matchCoordinate,
                        table.getRows().get(matchCoordinate.row),
                        singletonLinkedHashSet(part),
                        part.length());
                results.add(result, new BiFunction<Result<R>, Result<R>, Integer>() {
                    @Override
                    public Integer apply(final Result<R> previousResult, final Result<R> thisResult) {
                        if(previousResult.getRowIndex() != thisResult.getRowIndex()){
                            throw new IllegalStateException("Something has gone very wrong, two results should not be added together which correspond to different rows.");
                        }
                        final RowCoordinate lastCoordinate = previousResult.getMatchCoordinates().getLast();
                        if(matchCoordinate.isImmedicatelyFollowing(lastCoordinate)){
                            return previousResult.getMatchingScore() + result.getMatchingScore() * 4;
                        } else if(matchCoordinate.isInSameColumnAs(lastCoordinate)){
                            return previousResult.getMatchingScore() + result.getMatchingScore() * 2;
                        } else {
                            return previousResult.getMatchingScore() + result.getMatchingScore();
                        }
                    }
                });
            }
        }
        return results;
    }

    private <T> LinkedHashSet<T> singletonLinkedHashSet(final T item) {
        final LinkedHashSet<T> set = new LinkedHashSet<>();
        set.add(item);
        return set;
    }

    @Override
    public String toPrettyString(final String indent) {
        final IndentableStringBuilder sb = new IndentableStringBuilder(indent);
        sb.append("PartIndex{\n");
        sb.activateIndent();
        List<Part> keys = new ArrayList<>(partsToRowIndexes.keySet());
        Collections.sort(keys);

        for(final Part key: keys){
            sb.append(key).append(":").append(partsToRowIndexes.get(key)).append("\n");
        }
        sb.decactivateIndent();
        sb.append("}");
        return sb.toString();
    }
}
