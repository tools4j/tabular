package org.tools4j.tabular.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: ben
 * Date: 6/12/17
 * Time: 6:25 AM
 */
public class DataSetFromListOfMaps {
    private final List<Map<String, String>> listOfMaps;

    public DataSetFromListOfMaps(final List<Map<String, String>> listOfMaps) {
        this.listOfMaps = listOfMaps;
    }

    public DataSet asDataSet(){
        return new DataSet(listOfMaps.stream().map(row -> new RowWithCommands(row)).collect(Collectors.toList()));
    }
}
