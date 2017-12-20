package org.tools4j.tabular.service

import spock.lang.Specification

/**
 * User: ben
 * Date: 25/10/17
 * Time: 6:51 AM
 */
class DataSetFromCsvFileTest extends Specification {
    def "GetData with column headings"() {
        given:
        DataSetFromCsvFile csvDataFile = new DataSetFromCsvFile(new CsvFile("src/test/resources/csv-test/table.csv", (char) ','));

        when:
        DataSet data = csvDataFile.load();

        then:
        final List<String> columns = ["a", "b", "c"];
        final List<Map<String, String>> table = [["a":"one", "b":"white", "c":"scooter"], ["a":"two", "b":"blue", "c":"trains"], ["a":"three", "b":"orange", "c":"trunks"], ["a":"four", "b":"purple", "c":"monsters"], ["a":"ninety-nine", "b":"red", "c":"baloons"]];
        final DataSet expected = new DataSetFromStringMap(columns, table).asDataSet();
        assert data == expected;
    }
}
