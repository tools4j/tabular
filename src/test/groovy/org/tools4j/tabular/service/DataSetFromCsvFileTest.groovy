package org.tools4j.tabular.service

import org.tools4j.tabular.datasets.CsvFile
import org.tools4j.tabular.datasets.DataSet
import org.tools4j.tabular.datasets.DataSetFromColumnsAndListOfMaps
import org.tools4j.tabular.datasets.DataSetFromCsvFile
import spock.lang.Specification

/**
 * User: ben
 * Date: 25/10/17
 * Time: 6:51 AM
 */
class DataSetFromCsvFileTest extends Specification {
    def "GetData with column headings"() {
        given:
        DataSetFromCsvFile csvDataFile = new DataSetFromCsvFile(CsvFile.fromFileLocation("src/test/resources/csv-test/table.csv"));

        when:
        DataSet data = csvDataFile.load();

        then:
        final List<String> columns = ["a", "b", "c"];
        final List<Map<String, String>> table = [["a":"one", "b":"white", "c":"scooter"], ["a":"two", "b":"blue", "c":"trains"], ["a":"three", "b":"orange", "c":"trunks"], ["a":"four", "b":"purple", "c":"monsters"], ["a":"ninety-nine", "b":"red", "c":"baloons"]];
        final DataSet expected = new DataSetFromColumnsAndListOfMaps(columns, table).load();
        assert data == expected;
    }
}
