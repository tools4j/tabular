package org.tools4j.tabular.service;

import com.google.common.collect.ArrayListMultimap;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User: ben
 * Date: 30/10/17
 * Time: 6:16 AM
 */
public class SearchableDataSet {
    private final DataSet dataSet;
    private final ArrayListMultimap<String, Integer> cellCompleteValueToRowIndex;

    public SearchableDataSet(final DataSet dataSet, final ArrayListMultimap<String, Integer> cellCompleteValueToRowIndex) {
        this.dataSet = dataSet;
        this.cellCompleteValueToRowIndex = cellCompleteValueToRowIndex;
    }

    public List<Map<String,String>> searchForExactCellMatches(final String text){
        final List<Map<String,String>> results = new LinkedList<>();
        for(final String value: cellCompleteValueToRowIndex.keySet()){
            if(value.equalsIgnoreCase(text)){
                final Collection<Integer> rowIndexes = cellCompleteValueToRowIndex.get(value);
                for(int rowIndex: rowIndexes){
                    results.add(dataSet.getRows().get(rowIndex));
                }
            }
        }
        return results;
    }

    public List<Map<String,String>> searchForCapitalCaseMatches(final String text){
        final List<Map<String,String>> results = new LinkedList<>();
        for(final String value: cellCompleteValueToRowIndex.keySet()){
            if(value.equalsIgnoreCase(text)){
                final Collection<Integer> rowIndexes = cellCompleteValueToRowIndex.get(value);
                for(int rowIndex: rowIndexes){
                    results.add(dataSet.getRows().get(rowIndex));
                }
            }
        }
        return results;
    }
}
