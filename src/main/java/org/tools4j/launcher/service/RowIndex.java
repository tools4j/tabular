package org.tools4j.launcher.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * User: ben
 * Date: 2/11/17
 * Time: 6:13 AM
 */
public class RowIndex<R extends Row> {
    private final List<String> rows;
    private final TableWithColumnHeadings<R> table;

    public RowIndex(final TableWithColumnHeadings<R> table) {
        this.table = table;
        rows = new ArrayList<>(table.size());
        final List<? extends Row> listOfRows = table.getRows();
        for(final Row row: listOfRows){
            rows.add(row.toString());
        }
    }

    public Results search(final String query){
        return search(new SearchQuery.QueryParser(query).parse());
    }

    private Results<R> search(final SearchQuery query) {
        final Set<Result<R>> resultList = new TreeSet<>();
        for(int i=0; i<rows.size(); i++){
            final String row = rows.get(i);
            int matchingScore = 0;
            final LinkedHashSet<Term> matchingTerms = new LinkedHashSet<>();
            for(final Word word: query.getWordsInQuery()){
                if(word.isContainedIn(row)){
                    matchingScore += 1;
                    matchingTerms.add(word);
                    resultList.add(new Result<>(new PositionInRowCoordinate(i, word.positionIn(row), word.length()), table.getRows().get(i), matchingTerms, matchingScore));
                }
            }
        }
        return new Results<>(query, resultList);
    }
}
