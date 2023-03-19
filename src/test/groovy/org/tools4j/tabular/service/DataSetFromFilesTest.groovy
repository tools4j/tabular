package org.tools4j.tabular.service

import org.tools4j.tabular.datasets.RowFromMap
import org.tools4j.tabular.datasets.DataSet
import org.tools4j.tabular.datasets.DataSetFromReader
import spock.lang.Specification

class DataSetFromFilesTest extends Specification {
    private static final String BASE_TEST_DIR = "src/test/resources/config-resolver"

    def "get dataset from file"() {
        expect:
        DataSet<RowFromMap> dataSet = new DataSetFromReader(
                new FileReader(new File("$BASE_TEST_DIR/1/table.csv")),
        ).load()

        assert dataSet.columnHeadings == ["columnA", "columnB"]
        assert dataSet.size() == 2

        assertColumns(dataSet, 0, "columnA-1"                    , "columnB-1")
        assertColumns(dataSet, 1, "another-columnA-1"            , "another-columnB-1")
    }


    void assertColumns(DataSet<RowFromMap> dataSet, int rowIndex, String expectedColA, String expectedColB) {
        assert dataSet.getRow(rowIndex).get("columnA") == expectedColA
        assert dataSet.getRow(rowIndex).get("columnB") == expectedColB
    }
}
