package org.tools4j.tabular.service;

import org.tools4j.tabular.util.IndentableStringBuilder;
import org.tools4j.tabular.util.PropertiesRepo;
import org.tools4j.tabular.util.ResolvedMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: ben
 * Date: 24/10/17
 * Time: 6:57 AM
 */
public class DataSet implements TableWithColumnHeadings<RowWithCommands>, Pretty{
    private final List<String> columns;
    private final List<RowWithCommands> table;

    public DataSet(final List<String> columns, final List<RowWithCommands> table) {
        this.columns = columns;
        this.table = table;
    }

    @Override
    public String toString() {
        return "Data{" +
                "columns=" + columns +
                ", table=" + table +
                '}';
    }

    public DataSet resolveVariablesInCells(final PropertiesRepo... usingAdditionalProperties){
        final Map<String, String> additionalProperties = new HashMap<>();
        for(int i=0; i<usingAdditionalProperties.length; i++){
            additionalProperties.putAll(usingAdditionalProperties[i].asMap());
        }
        final List<RowWithCommands> tableWithResolvedCells = new ArrayList<>();
        for(final RowWithCommands row: table){
            final RowWithCommands rowWithResolvedCells = new RowWithCommands(new RowFromMap(new ResolvedMap(row, additionalProperties).resolve()), row.getCommands());
            tableWithResolvedCells.add(rowWithResolvedCells);
        }
        return new DataSet(columns, tableWithResolvedCells);
    }

    public DataSet resolveCommands(final CommandMetadatas commandMetadatas, final PropertiesRepo propertiesRepo){
        final List<RowWithCommands> rowWithCommands = new ArrayList<>(table.size());
        for(final RowWithCommands row: table){
            final CommandMetadatas commandMetadatasForRow = commandMetadatas.getCommandsFor(row);
            final List<Command> commandInstancesForRow = commandMetadatasForRow.getCommandInstances(row, propertiesRepo);
            final RowWithCommands rowWithResolvedCells = new RowWithCommands(row, commandInstancesForRow);
            rowWithCommands.add(rowWithResolvedCells);
        }
        return new DataSet(columns, rowWithCommands);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSet)) return false;

        final DataSet dataSet = (DataSet) o;

        if (!columns.equals(dataSet.columns)) return false;
        return table.equals(dataSet.table);
    }

    @Override
    public int hashCode() {
        int result = columns.hashCode();
        result = 31 * result + table.hashCode();
        return result;
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
    public List<RowWithCommands> getRows() {
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
        final Iterator<RowWithCommands> rows = table.iterator();
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

    public RowWithCommands getRow(final int i) {
        return table.get(i);
    }
}
