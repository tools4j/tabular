package org.tools4j.tabular.service;

import org.tools4j.tabular.util.IndentableStringBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * User: ben
 * Date: 1/11/17
 * Time: 6:20 AM
 */
public class Results<R extends Row> implements Pretty, Iterable<Result<R>>{
    private final TreeSet<Result<R>> results;
    private final SearchQuery searchQuery;

    public Results(final String searchQuery){
        this(new SearchQuery.QueryParser(searchQuery).parse(), new HashSet<>());
    }

    public Results(final SearchQuery searchQuery){
        this(searchQuery, new HashSet<>());
    }

    public Results(final SearchQuery searchQuery, final Set<Result<R>> results) {
        this.searchQuery = searchQuery;
        this.results = new TreeSet<>();
        this.results.addAll(results);
    }

    //Creates an empty set of results
    public Results() {
        this.searchQuery = new SearchQuery("", Collections.emptyList());
        this.results = new TreeSet<>();
    }

    public void add(final Result<R> result, final BiFunction<Result<R>, Result<R>, Integer> matchingScoreCalculator){
        for(final Result<R> existingResult: results){
            if(existingResult.getRowIndex() == result.getRowIndex()){
                results.add(existingResult.add(result, matchingScoreCalculator));
                return;
            }
        }
        results.add(result);
    }

    public Results<R> withAllWordsMatching(){
        return new Results<R>(searchQuery, results.stream().filter((result) -> result.allPartsMatch(searchQuery)).collect(Collectors.toSet()));
    }

    @Override
    public String toPrettyString(final String indent) {
        final IndentableStringBuilder sb = new IndentableStringBuilder(indent);
        sb.append("Results{\n");
        sb.activateIndent();
        for(final Result<R> result: results){
            sb.append(result.toPrettyString(indent)).append("\n");
        }
        sb.decactivateIndent();
        sb.append("}");
        return sb.toString();
    }

    public int size() {
        return results.size();
    }

    public List<Integer> getIndexes() {
        return results.stream().map((result) -> result.getRowIndex()).collect(Collectors.toList());
    }

    @Override
    public Iterator<Result<R>> iterator() {
        return results.iterator();
    }
}
