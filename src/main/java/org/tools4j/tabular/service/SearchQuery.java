package org.tools4j.tabular.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: ben
 * Date: 1/11/17
 * Time: 6:25 AM
 */
public class SearchQuery {
    private final String query;
    private final List<Word> wordsInQuery;

    public SearchQuery(final String query, final List<Word> words) {
        this.query = query;
        this.wordsInQuery = words;
    }

    public Collection<Part> getPartsInQuery() {
        return wordsInQuery.stream().flatMap((word) -> word.getParts().stream()).collect(Collectors.toList());
    }

    public static class QueryParser{
        private final String query;

        public QueryParser(final String query) {
            this.query = query;
        }

        public SearchQuery parse(){
            final String query = this.query.trim();
            return new SearchQuery(query, new WordsAsString(query).getWords());
        }
    }

    public Term getQuery() {
        return new Part(query);
    }

    public String getQueryString() {
        return query;
    }

    public List<Word> getWordsInQuery() {
        return wordsInQuery;
    }
}
