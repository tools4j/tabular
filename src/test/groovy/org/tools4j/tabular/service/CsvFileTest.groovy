package org.tools4j.tabular.service

import org.tools4j.groovytables.GroovyTables
import org.tools4j.tabular.datasets.CsvFile
import spock.lang.Specification

/**
 * User: ben
 * Date: 25/10/17
 * Time: 6:13 AM
 */
class CsvFileTest extends Specification {
    def "GetData"() {
        given:
        CsvFile csvDataFile = CsvFile.fromFileLocation("src/test/resources/csv-test/table.csv");

        when:
        final List<String[]> data = csvDataFile.getRows();

        then:
        final List<String[]> expected = GroovyTables.createListOfArrays {
            "a"|"b"|"c"
            "one"|"white"|"scooter"
            "two"|"blue"|"trains"
            "three"|"orange"|"trunks"
            "four"|"purple"|"monsters"
            "ninety-nine"|"red"|"baloons"
        }
        assert data == expected
    }
}
