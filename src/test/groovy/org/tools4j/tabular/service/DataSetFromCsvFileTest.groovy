package org.tools4j.tabular.service

import org.tools4j.tabular.service.datasets.DataSet
import org.tools4j.tabular.service.datasets.DataSetFromColumnsAndListOfMaps
import org.tools4j.tabular.service.datasets.DataSetFromCsvFiles
import spock.lang.Specification

/**
 * User: ben
 * Date: 25/10/17
 * Time: 6:51 AM
 */
class DataSetFromCsvFileTest extends Specification {
    def "GetData with column headings"() {
        given:
        DataSetFromCsvFiles csvDataFile = new DataSetFromCsvFiles(CsvFile.fromFileLocation("src/test/resources/csv-test/table.csv"));

        when:
        DataSet data = csvDataFile.load();

        then:
        final List<String> columns = ["a", "b", "c"];
        final List<Map<String, String>> table = [["a":"one", "b":"white", "c":"scooter"], ["a":"two", "b":"blue", "c":"trains"], ["a":"three", "b":"orange", "c":"trunks"], ["a":"four", "b":"purple", "c":"monsters"], ["a":"ninety-nine", "b":"red", "c":"baloons"]];
        final DataSet expected = new DataSetFromColumnsAndListOfMaps(columns, table).load();
        assert data == expected;
    }
}
