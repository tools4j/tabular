package org.tools4j.tabular.service

import spock.lang.Specification

/**
 * User: ben
 * Date: 1/11/17
 * Time: 6:53 AM
 */
class SearchQueryTest extends Specification {
    def "test GetQuery"() {
        when:
        final SearchQuery query = new SearchQuery.QueryParser("Hi there big guy").parse()

        then:
        assert query.getQueryString() == 'Hi there big guy'
    }

    def "test 1"() {
        when:
        final SearchQuery query = new SearchQuery.QueryParser("can youFind thePartsOf myQuery evenWithNumbers009?").parse()

        then:
        assert query.getWordsInQuery() == new WordsAsStrings(["can", "youFind", "thePartsOf", "myQuery", "evenWithNumbers009?"]).getWords()
    }
}
