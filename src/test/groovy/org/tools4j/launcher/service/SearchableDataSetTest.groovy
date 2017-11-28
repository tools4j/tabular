package org.tools4j.launcher.service

import spock.lang.Specification

/**
 * User: ben
 * Date: 30/10/17
 * Time: 6:51 AM
 */
class SearchableDataSetTest extends Specification {
    private SearchableDataSet searchableDataSet;

    def setup(){
        final String csvFilePath = "src/test/resources/test4/test.csv"
        searchableDataSet = new SearchableDataSetFromDataSet(new DataSetFromCsvFile(new CsvFile(csvFilePath)).load()).load();
    }


    def "Search"() {
        when:
        final List<Map<String, String>> results = searchableDataSet.searchForExactCellMatches("haud0003");

        then:
        assert results.size() == 1
        println results
    }

    def "Search Capital Case"() {
        when:
        final List<Map<String, String>> results = searchableDataSet.searchForExactCellMatches("DMA");

        then:
        assert results.size() == 1
        println results
    }
}
