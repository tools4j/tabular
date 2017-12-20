package org.tools4j.tabular.service

import spock.lang.Specification

/**
 * User: ben
 * Date: 30/10/17
 * Time: 7:04 AM
 */
class ResolvedDataSetTest extends Specification {
    def "test resolved dataset"(){
        when:
        final String configDir = "src/test/resources/simple-table"
        final DataSetContext dataSetContext = new DataSetContextFromDir(configDir).load();

        then:
        final List<String> columns = ["number", "descriptionAndDomain", "description", "numberAtDomain"];
        final List<Map<String, String>> table = [["number":"one", "descriptionAndDomain":"scooter:tools4j.com", "description":"scooter", "numberAtDomain":"one@tools4j.com"], ["number":"two", "descriptionAndDomain":"trains:tools4j.com", "description":"trains", "numberAtDomain":"two@tools4j.com"], ["number":"three", "descriptionAndDomain":"trunks:tools4j.com", "description":"trunks", "numberAtDomain":"three@tools4j.com"], ["number":"four", "descriptionAndDomain":"escaped dollar four:tools4j.com", "description":"escaped dollar four", "numberAtDomain":"four@tools4j.com"], ["number":"ninety-nine", "descriptionAndDomain":"baloons-hi there!:tools4j.com", "description":"baloons-hi there!", "numberAtDomain":"ninety-nine@tools4j.com"]]
        final DataSet expected = new DataSetFromStringMap(columns, table).asDataSet();
        assert dataSetContext.dataSet == expected;
    }
}
