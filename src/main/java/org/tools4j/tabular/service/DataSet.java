package org.tools4j.tabular.service;

import org.tools4j.tabular.properties.MapResolver;
import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.util.IndentableStringBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * User: ben
 * Date: 24/10/17
 * Time: 6:57 AM
 */
public class DataSet<T extends Row> implements TableWithColumnHeadings<T>, Pretty{
    private final List<String> columns;
    private final List<T> table;

    public DataSet(final List<T> table) {
        this(extractColumnNames(table), table);
    }

    public DataSet(final List<String> columns, final List<T> table) {
        this.columns = columns;
        this.table = table;
    }

    private static List<String> extractColumnNames(List<? extends Row> table) {
        List<String> firstRowColumnHeaders = null;
        for (Map<String, String> row : table) {
            ArrayList<String> currentRowColumnHeaders = new ArrayList<>(row.keySet());
            if(firstRowColumnHeaders == null){
                firstRowColumnHeaders = currentRowColumnHeaders;
            } else if(!currentRowColumnHeaders.equals(firstRowColumnHeaders)){
                throw new IllegalArgumentException("Column headers do not match between rows. firstRowColumnHeaders " + firstRowColumnHeaders + ", currentRowColumnHeaders " + currentRowColumnHeaders);
            }
        }
        return firstRowColumnHeaders;
    }

    @Override
    public String toString() {
        return "Data{" +
                "columns=" + columns +
                ", table=" + table +
                '}';
    }

    public DataSet<RowFromMap> resolveVariablesInCells(final PropertiesRepo... usingAdditionalProperties){
        final Map<String, String> additionalProperties = new HashMap<>();
        for(int i=0; i<usingAdditionalProperties.length; i++){
            additionalProperties.putAll(usingAdditionalProperties[i].asMap());
        }
        final List<RowFromMap> tableWithResolvedCells = new ArrayList<>();
        for(final T row: table){
            final RowFromMap rowFromMap = new RowFromMap(new MapResolver(additionalProperties).resolve(row));
            tableWithResolvedCells.add(rowFromMap);
        }
        return new DataSet<>(columns, tableWithResolvedCells);
    }

    public DataSet<RowWithCommands> resolveCommands(final CommandMetadatas commandMetadatas, final PropertiesRepo propertiesRepo){
        final List<RowWithCommands> rowWithCommands = new ArrayList<>(table.size());
        for(final T row: table){
            final CommandMetadatas commandMetadatasForRow = commandMetadatas.getCommandsFor(row);
            final List<Command> commandInstancesForRow = commandMetadatasForRow.getCommandInstances(row, propertiesRepo);
            final RowWithCommands rowWithResolvedCells = new RowWithCommands(row, commandInstancesForRow);
            rowWithCommands.add(rowWithResolvedCells);
        }
        return new DataSet(columns, rowWithCommands);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSet<?> dataSet = (DataSet<?>) o;
        return Objects.equals(columns, dataSet.columns) &&
                Objects.equals(table, dataSet.table);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columns, table);
    }

    @Override
    public List<String> getColumnHeadings() {
        return columns;
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public List<T> getRows() {
        return table;
    }

    @Override
    public String toPrettyString(final String indent) {
        final IndentableStringBuilder sb = new IndentableStringBuilder(indent);
        sb.append("dataSet{\n");
        sb.activateIndent();
        sb.append("columns=").append(columns).append("\n");
        sb.append("table=").append(table).append("\n");
        sb.decactivateIndent();
        sb.append("}");
        return sb.toString();
    }

    public String toCsv(){
        final StringBuilder sb = new StringBuilder();
        sb.append(join(columns)).append("\n");
        final Iterator<T> rows = table.iterator();
        while(rows.hasNext()){
            sb.append(join(rows.next().values()));
            if(rows.hasNext()){
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public String join(final Collection collection){
        return join(collection, ",");
    }

    public String join(final Collection collection, final String delimiter){
        final StringBuilder sb = new StringBuilder();
        final Iterator iterator = collection.iterator();
        while(iterator.hasNext()){
            sb.append(iterator.next().toString());
            if(iterator.hasNext()){
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public int rowCount() {
        return table.size();
    }

    public T getRow(final int i) {
        return table.get(i);
    }
}
