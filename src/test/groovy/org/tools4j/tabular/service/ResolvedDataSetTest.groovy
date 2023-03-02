package org.tools4j.tabular.service

import org.tools4j.groovytables.GroovyTables
import org.tools4j.groovytables.Rows
import org.tools4j.tabular.config.ConfigReader
import org.tools4j.tabular.config.ConfigResolver
import org.tools4j.tabular.config.DummyDirResolver
import org.tools4j.tabular.service.datasets.DataSet
import org.tools4j.tabular.util.TestUtils
import spock.lang.Specification

import static org.tools4j.tabular.util.TestUtils.dataSetFromRows

/**
 * User: ben
 * Date: 30/10/17
 * Time: 7:04 AM
 */
class ResolvedDataSetTest extends Specification {
    private final static String FILE_WHICH_DOES_NOT_EXIST = "src/test/resources/blah-blah-blah";

    def setup() {
        assert !(new File(FILE_WHICH_DOES_NOT_EXIST)).exists()
    }

    def "test resolved dataset"(){
        given:
        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();

        when:
        final ConfigReader config = new ConfigResolver(
                propOrEnvVarResolver,
                new DummyDirResolver("src/test/resources/table_with_substitutions"),
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST)
        ).resolve()
        final DataSetContext dataSetContext = new DataSetContextFromConfig(config).load();

        then:
        final Rows expectedData = GroovyTables.createRows {
            number        | descriptionAtDomain                    | description                | numberAtDomain
            'one'         | 'scooter@tools4j.com'                  | 'scooter'                  | 'one@tools4j.com'
            'two'         | 'trains@tools4j.com'                   | 'trains'                   | 'two@tools4j.com'
            'three'       | 'trunks@tools4j.com'                   | 'trunks'                   | 'three@tools4j.com'
            'four'        | 'escaped dollar ${number}@tools4j.com' | 'escaped dollar ${number}' | 'four@tools4j.com'
            'ninety-nine' | 'baloons-hi there!@tools4j.com'        | 'baloons-hi there!'        | 'ninety-nine@tools4j.com'}
        final DataSet<Row> expected = dataSetFromRows(expectedData);
        assert TestUtils.assertHasSameColumnsAndRows(expected, dataSetContext.dataSet)
    }

    def "test resolved dataset - when sysProperty exists matching column name"(){
        given:
        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();

        when:
        System.setProperty("number", "blah!")
        final ConfigReader config = new ConfigResolver(
                propOrEnvVarResolver,
                new DummyDirResolver("src/test/resources/table_with_substitutions"),
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST)
        ).resolve()
        final DataSetContext dataSetContext = new DataSetContextFromConfig(config).load();

        then:
        final Rows expectedData = GroovyTables.createRows {
            number        | descriptionAtDomain                    | description                | numberAtDomain
            'one'         | 'scooter@tools4j.com'                  | 'scooter'                  | 'one@tools4j.com'
            'two'         | 'trains@tools4j.com'                   | 'trains'                   | 'two@tools4j.com'
            'three'       | 'trunks@tools4j.com'                   | 'trunks'                   | 'three@tools4j.com'
            'four'        | 'escaped dollar ${number}@tools4j.com' | 'escaped dollar ${number}' | 'four@tools4j.com'
            'ninety-nine' | 'baloons-hi there!@tools4j.com'        | 'baloons-hi there!'        | 'ninety-nine@tools4j.com'}
        
        final DataSet<Row> expected = dataSetFromRows(expectedData);
        assert TestUtils.assertHasSameColumnsAndRows(expected, dataSetContext.dataSet)
    }
}
