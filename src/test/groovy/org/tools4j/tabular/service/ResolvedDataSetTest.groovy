package org.tools4j.tabular.service

import org.tools4j.groovytables.GroovyTables
import org.tools4j.groovytables.Rows
import spock.lang.Specification

import static org.tools4j.tabular.util.TestUtils.dataSetFromRows

/**
 * User: ben
 * Date: 30/10/17
 * Time: 7:04 AM
 */
class ResolvedDataSetTest extends Specification {
    def "test resolved dataset"(){
        when:
        final String configDir = "src/test/resources/table_with_substitutions"
        final DataSetContext dataSetContext = new DataSetContextFromDir(configDir).load();

        then:
        final Rows expectedData = GroovyTables.createRows {
            number        | descriptionAndDomain              | description           | numberAtDomain
            'one'         | 'scooter:tools4j.com'             | 'scooter'             | 'one@tools4j.com'
            'two'         | 'trains:tools4j.com'              | 'trains'              | 'two@tools4j.com'
            'three'       | 'trunks:tools4j.com'              | 'trunks'              | 'three@tools4j.com'
            'four'        | 'escaped dollar ${n}:tools4j.com' | 'escaped dollar ${n}' | 'four@tools4j.com'
            'ninety-nine' | 'baloons-hi there!:tools4j.com'   | 'baloons-hi there!'   | 'ninety-nine@tools4j.com'}
        final DataSet expected = dataSetFromRows(expectedData);
        assert dataSetContext.dataSet == expected;
    }

    def "test resolved dataset - when sysProperty exists matching column name"(){
        when:
        final String configDir = "src/test/resources/table_with_substitutions"
        System.setProperty("number", "blah!")
        final DataSetContext dataSetContext = new DataSetContextFromDir(configDir).load();

        then:
        final Rows expectedData = GroovyTables.createRows {
            number        | descriptionAndDomain              | description           | numberAtDomain
            'one'         | 'scooter:tools4j.com'             | 'scooter'             | 'one@tools4j.com'
            'two'         | 'trains:tools4j.com'              | 'trains'              | 'two@tools4j.com'
            'three'       | 'trunks:tools4j.com'              | 'trunks'              | 'three@tools4j.com'
            'four'        | 'escaped dollar ${n}:tools4j.com' | 'escaped dollar ${n}' | 'four@tools4j.com'
            'ninety-nine' | 'baloons-hi there!:tools4j.com'   | 'baloons-hi there!'   | 'ninety-nine@tools4j.com'}
        final DataSet expected = dataSetFromRows(expectedData);
        assert dataSetContext.dataSet == expected;
    }
}
