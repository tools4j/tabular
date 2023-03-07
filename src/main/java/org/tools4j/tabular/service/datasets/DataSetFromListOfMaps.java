package org.tools4j.tabular.service.datasets;

import org.tools4j.tabular.service.RowFromMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: ben
 * Date: 6/12/17
 * Time: 6:25 AM
 */
public class DataSetFromListOfMaps implements DataSetLoader {
    private final List<Map<String, String>> listOfMaps;

    public DataSetFromListOfMaps(final List<Map<String, String>> listOfMaps) {
        this.listOfMaps = listOfMaps;
    }

    @Override
    public DataSet<RowFromMap> load() {
        return new DataSet<>(listOfMaps.stream().map(RowFromMap::new).collect(Collectors.toList()));
    }
}
