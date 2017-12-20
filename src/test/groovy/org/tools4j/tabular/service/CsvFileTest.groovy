package org.tools4j.tabular.service

import org.tools4j.groovytables.GroovyTables
import spock.lang.Specification

/**
 * User: ben
 * Date: 25/10/17
 * Time: 6:13 AM
 */
class CsvFileTest extends Specification {
    def "GetData"() {
        given:
        CsvFile csvDataFile = new CsvFile("src/test/resources/csv-test/table.csv", (char) ',');

        when:
        final List<String[]> data = csvDataFile.getData();

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
