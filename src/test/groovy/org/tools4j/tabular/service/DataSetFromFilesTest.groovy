package org.tools4j.tabular.service


import spock.lang.Specification

class DataSetFromFilesTest extends Specification {
    private static final String BASE_TEST_DIR = "src/test/resources/config-resolver"

    def "get dataset from single file"() {
        expect:
        DataSet<RowFromMap> dataSet = new DataSetFromFiles([
                new FileReader(new File("$BASE_TEST_DIR/1/table.csv")),
        ]).get()

        assert dataSet.columnHeadings == ["columnA", "columnB"]
        assert dataSet.size() == 2

        assertColumns(dataSet, 0, "columnA-1"                    , "columnB-1")
        assertColumns(dataSet, 1, "another-columnA-1"            , "another-columnB-1")
    }

    def "get dataset from multiple files - rows from all csv files are returned"() {
        expect:
        DataSet<RowFromMap> dataSet = new DataSetFromFiles([
                new FileReader(new File("$BASE_TEST_DIR/1/table.csv")),
                new FileReader(new File("$BASE_TEST_DIR/2/table.csv")),
                new FileReader(new File("$BASE_TEST_DIR/2/non-default-named-table.csv"))
        ]).get()

        assert dataSet.columnHeadings == ["columnA", "columnB"]
        assert dataSet.size() == 6
        
        assertColumns(dataSet, 0, "columnA-1","columnB-1")
        assertColumns(dataSet, 1, "another-columnA-1","another-columnB-1")
        assertColumns(dataSet, 2, "columnA-2","columnB-2")
        assertColumns(dataSet, 3, "another-columnA-2","another-columnB-2")
        assertColumns(dataSet, 4, "non-default-columnA-2","non-default-columnB-2")
        assertColumns(dataSet, 5, "non-default-another-columnA-2","non-default-another-columnB-2")
    }

    def "get dataset from multiple files - including file which has mismatched columns - throws exception"() {
        when:
        DataSet<RowFromMap> dataSet = new DataSetFromFiles([
                new FileReader(new File("$BASE_TEST_DIR/1/table.csv")),
                new FileReader(new File("$BASE_TEST_DIR/2/table-with-different-column-headings.csv")),
                new FileReader(new File("$BASE_TEST_DIR/2/non-default-named-table.csv"))
        ]).get()

        then:
        Exception e = thrown()
        assert e.message == "Columns do not match between multiple csv files [columnA, columnB] other [columnY, columnZ]"
    }

    void assertColumns(DataSet<RowFromMap> dataSet, int rowIndex, String expectedColA, String expectedColB) {
        assert dataSet.getRow(rowIndex).get("columnA") == expectedColA
        assert dataSet.getRow(rowIndex).get("columnB") == expectedColB
    }
}
