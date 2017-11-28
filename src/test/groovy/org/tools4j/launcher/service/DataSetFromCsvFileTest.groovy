package org.tools4j.launcher.service

import spock.lang.Specification

/**
 * User: ben
 * Date: 25/10/17
 * Time: 6:51 AM
 */
class DataSetFromCsvFileTest extends Specification {
    def "GetData with column headings"() {
        given:
        DataSetFromCsvFile csvDataFile = new DataSetFromCsvFile(new CsvFile("src/test/resources/test2/easy.csv", (char) ','));

        when:
        DataSet data = csvDataFile.load();

        then:
        final List<String> columns = ["a", "b", "c"];
        final List<Map<String, String>> table = [["a":"one", "b":"white", "c":"scooter"], ["a":"two", "b":"blue", "c":"trains"], ["a":"three", "b":"orange", "c":"trunks"], ["a":"four", "b":"purple", "c":"monsters"], ["a":"ninety-nine", "b":"red", "c":"baloons"]];
        final DataSet expected = new DataSet(columns, table);
        assert data == expected;
    }

    def "GetData with NO column headings"() {
        given:
        DataSetFromCsvFile csvDataFile = new DataSetFromCsvFile(new CsvFile("src/test/resources/test2/easy-no-column-headings.csv", (char) ','));

        when:
        DataSet data = csvDataFile.load();

        then:
        final List<String> columns = ["0", "1", "2"];
        final List<Map<String, String>> table = [["0":"one", "1":"white", "2":"scooter"], ["0":"two", "1":"blue", "2":"trains"], ["0":"three", "1":"orange", "2":"trunks"], ["0":"four", "1":"purple", "2":"monsters"], ["0":"ninety-nine", "1":"red", "2":"baloons"]];
        final DataSet expected = new DataSet(columns, table);
        assert data == expected;
    }
}
