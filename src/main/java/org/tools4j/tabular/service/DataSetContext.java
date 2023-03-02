package org.tools4j.tabular.service;

import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.service.commands.Command;
import org.tools4j.tabular.service.commands.CommandMetadata;
import org.tools4j.tabular.service.commands.CommandMetadatas;
import org.tools4j.tabular.service.datasets.DataSet;
import org.tools4j.tabular.util.IndentableStringBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * User: ben
 * Date: 25/10/17
 * Time: 5:25 PM
 */
public class DataSetContext {
    public static final String APP_DATA_COLUMN_TO_DISPLAY_WHEN_SELECTED = "app.data.column.to.display.when.selected";
    public static final String APP_COMMAND_COLUMN_TO_DISPLAY_WHEN_SELECTED = "app.command.column.to.display.when.selected";
    public static final String APP_COLUMNS_TO_DISPLAY_IN_COMMAND_TABLE = "app.columns.to.display.in.command.table";
    public static final String APP_COLUMNS_TO_INDEX_IN_COMMAND_TABLE = "app.columns.to.index.in.command.table";
    public static final String APP_COLUMNS_TO_DISPLAY_IN_DATA_TABLE = "app.columns.to.display.in.data.table";
    public static final String APP_COLUMNS_TO_INDEX_IN_DATA_TABLE = "app.columns.to.index.in.data.table";
    public static final String APP_DATA_SEARCH_ABBREVIATION_PREFIX = "app.data.search.abbreviation.";
    public static final String APP_COMMAND_SEARCH_ABBREVIATION_PREFIX = "app.command.search.abbreviation.";
    private final DataSet<RowWithCommands> dataSet;
    private final PropertiesRepo properties;
    private final CommandMetadatas commandMetadatas;

    public DataSetContext(final DataSet<RowWithCommands> dataSet, final CommandMetadatas commandMetadatas, final PropertiesRepo properties) {
        this.dataSet = dataSet;
        this.properties = properties;
        this.commandMetadatas = commandMetadatas;
    }

    @Override
    public String toString() {
        return "DataSetContext{" +
                "dataSet=" + dataSet +
                ", properties=" + properties +
                '}';
    }

    public String toPrettyString() {
        return toPrettyString("    ");
    }


    public String toPrettyString(final String indent) {
        final IndentableStringBuilder sb = new IndentableStringBuilder(indent);
        sb.append("DataSetContext{\n");
        sb.activateIndent();
        sb.append("dataSet=").append(dataSet.toPrettyString(indent)).append("\n");
        sb.append("commandMetadatas=").append(commandMetadatas.toPrettyString(indent)).append("\n");
        sb.append("properties=").append(properties.toPrettyString(indent)).append("\n");
        sb.decactivateIndent();
        sb.append("}");
        return sb.toString();
    }

    public DataSet<RowWithCommands> getDataSet() {
        return dataSet;
    }

    public PropertiesRepo getProperties() {
        return properties;
    }

    public boolean skipCommandSearch() {
        return (commandMetadatas.size() == 1 && properties.getAsBoolean("app.skip.command.browse.if.only.one.command.configured", false));
    }

    public boolean zeroCommandsConfigured() {
        return commandMetadatas.isEmpty();
    }

    public List<String> getDataColumnsToDisplay() {
        final String configuredColumnsToDisplayInTable = properties.get(APP_COLUMNS_TO_DISPLAY_IN_DATA_TABLE);
        if(configuredColumnsToDisplayInTable != null){
            return Arrays.asList(configuredColumnsToDisplayInTable.split(","));
        } else {
            return dataSet.getColumnHeadings();
        }
    }

    public String getDataSearchAbbreviation(String text){
        return properties.get(APP_DATA_SEARCH_ABBREVIATION_PREFIX + text);
    }

    public String getCommandSearchAbbreviation(String text){
        return properties.get(APP_COMMAND_SEARCH_ABBREVIATION_PREFIX + text);
    }

    public List<String> getDataColumnsToIndex() {
        final String configuredColumnsToDisplayInTable = properties.get(APP_COLUMNS_TO_INDEX_IN_DATA_TABLE);
        if(configuredColumnsToDisplayInTable != null){
            return Arrays.asList(configuredColumnsToDisplayInTable.split(","));
        } else {
            return dataSet.getColumnHeadings();
        }
    }

    public List<String> getCommandColumnsToDisplay() {
        final String configuredColumnsToDisplayInTable = properties.get(APP_COLUMNS_TO_DISPLAY_IN_COMMAND_TABLE);
        if(configuredColumnsToDisplayInTable != null){
            return Arrays.asList(configuredColumnsToDisplayInTable.split(","));
        } else {
            return Command.getCommandTableColumnHeadings();
        }
    }

    public List<String> getCommandColumnsToIndex() {
        final String configuredColumnsToDisplayInTable = properties.get(APP_COLUMNS_TO_INDEX_IN_COMMAND_TABLE);
        if(configuredColumnsToDisplayInTable != null){
            return Arrays.asList(configuredColumnsToDisplayInTable.split(","));
        } else {
            return Command.getCommandTableColumnHeadings();
        }
    }

    public Predicate<String> getDataColumnToIndexPredicate(){
        return columnName -> getDataColumnsToIndex().contains(columnName);
    }

    public Predicate<String> getCommandColumnToIndexPredicate(){
        return columnName -> getCommandColumnsToIndex().contains(columnName);
    }

    public String getValueToDisplayWhenDataRowSelected(final Row selectedRow, final String query) {
        final String configuredColumnToDisplayInTable = properties.get(APP_DATA_COLUMN_TO_DISPLAY_WHEN_SELECTED);
        if(configuredColumnToDisplayInTable != null){
            if(!dataSet.getColumnHeadings().contains(configuredColumnToDisplayInTable)){
                throw new IllegalArgumentException("The column specified in the property " + APP_DATA_COLUMN_TO_DISPLAY_WHEN_SELECTED
                                                    + "=" + configuredColumnToDisplayInTable + " must be a valid data column.  e.g. "
                                                    + "one of: " + dataSet.getColumnHeadings());
            } else {
                return selectedRow.get(configuredColumnToDisplayInTable);
            }
        } else {
            return query;
        }
    }

    public String getValueToDisplayWhenCommandRowSelected(final Row selectedRow, final String query) {
        final String configuredColumnToDisplayInTable = properties.get(APP_COMMAND_COLUMN_TO_DISPLAY_WHEN_SELECTED);
        if(configuredColumnToDisplayInTable != null){
            if(!Command.getCommandTableColumnHeadings().contains(configuredColumnToDisplayInTable)){
                throw new IllegalArgumentException("The column specified in the property " + APP_COMMAND_COLUMN_TO_DISPLAY_WHEN_SELECTED
                        + "=" + configuredColumnToDisplayInTable + " must be a valid command table column.  e.g. "
                        + "one of: " + Command.getCommandTableColumnHeadings());
            } else {
                return selectedRow.get(configuredColumnToDisplayInTable);
            }
        } else if(query != null && !query.isEmpty()){
            return query;
        } else {
            return selectedRow.get(Command.ColumnHeadings.Description.toString());
        }
    }

    public String getValueToDisplayWhenCommandRowSelected(final Row selectedRow) {
        return getValueToDisplayWhenCommandRowSelected(selectedRow, null);
    }

    public List<CommandMetadata> getCommandMetadatas() {
        return commandMetadatas.getCommandMetadatas();
    }
}
