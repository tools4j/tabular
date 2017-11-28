package org.tools4j.launcher.service;

import org.tools4j.launcher.util.IndentableStringBuilder;
import org.tools4j.launcher.util.PropertiesRepo;

import java.util.Arrays;
import java.util.List;

/**
 * User: ben
 * Date: 25/10/17
 * Time: 5:25 PM
 */
public class DataSetContext {
    public static final String APP_DATA_COLUMN_TO_DISPLAY_WHEN_SELECTED = "app.data.column.to.display.when.selected";
    public static final String APP_COMMAND_COLUMN_TO_DISPLAY_WHEN_SELECTED = "app.command.column.to.display.when.selected";
    public static final String APP_COLUMNS_TO_DISPLAY_IN_COMMAND_TABLE = "app.columns.to.display.in.command.table";
    public static final String APP_COLUMNS_TO_DISPLAY_IN_DATA_TABLE = "app.columns.to.display.in.data.table";
    private final String dataSetName;
    private final DataSet dataSet;
    private final PropertiesRepo properties;

    public DataSetContext(final String dataSetName, final DataSet dataSet, final PropertiesRepo properties) {
        this.dataSetName = dataSetName;
        this.dataSet = dataSet;
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "DataSetContext{" +
                "dataSetName='" + dataSetName + '\'' +
                ", dataSet=" + dataSet +
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
        sb.append("dataSetName=").append(dataSetName).append("\n");
        sb.append("dataSet=").append(dataSet.toPrettyString(indent)).append("\n");
        sb.append("properties=").append(properties.toPrettyString(indent)).append("\n");
        sb.decactivateIndent();
        sb.append("}");
        return sb.toString();
    }

    public String dataSetAsCsv() {
        return dataSet.toCsv();
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public PropertiesRepo getProperties() {
        return properties;
    }

    public boolean skipCommandSearch() {
        return (isOnlySingleCommandConfiguredPerRow() && properties.getAsBoolean("app.skip.command.browse.if.only.one.command.configured", false));
    }

    private boolean isOnlySingleCommandConfiguredPerRow() {
        for(final RowWithCommands row: dataSet.getRows()){
            if(row.getCommands().size() > 1){
                return false;
            }
        }
        return true;
    }

    public List<String> getDataColumnsToDisplay() {
        final String configuredColumnsToDisplayInTable = properties.get(APP_COLUMNS_TO_DISPLAY_IN_DATA_TABLE);
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
}
