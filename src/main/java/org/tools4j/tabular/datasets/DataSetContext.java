package org.tools4j.tabular.datasets;

import org.tools4j.tabular.commands.Command;
import org.tools4j.tabular.commands.CommandMetadata;
import org.tools4j.tabular.commands.CommandMetadatas;
import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.service.datasets.Expression;
import org.tools4j.tabular.service.datasets.FreemarkerCompiler;
import org.tools4j.tabular.util.IndentableStringBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.tools4j.tabular.config.TabularProperties.COLUMNS_TO_DISPLAY_IN_COMMAND_TABLE;
import static org.tools4j.tabular.config.TabularProperties.COLUMNS_TO_DISPLAY_IN_DATA_TABLE;
import static org.tools4j.tabular.config.TabularProperties.COLUMNS_TO_INDEX_IN_COMMAND_TABLE;
import static org.tools4j.tabular.config.TabularProperties.COLUMNS_TO_INDEX_IN_DATA_TABLE;
import static org.tools4j.tabular.config.TabularProperties.COMMAND_EXPRESSION_TO_DISPLAY_WHEN_SELECTED;
import static org.tools4j.tabular.config.TabularProperties.DATA_EXPRESSION_TO_DISPLAY_WHEN_SELECTED;
import static org.tools4j.tabular.config.TabularProperties.SKIP_COMMAND_BROWSE_IF_ONLY_ONE_COMMAND_CONFIGURED;

/**
 * User: ben
 * Date: 25/10/17
 * Time: 5:25 PM
 */
public class DataSetContext {
    private final DataSet<RowWithCommands> dataSet;
    private final PropertiesRepo properties;
    private final CommandMetadatas commandMetadatas;
    private final Expression dataExpressionToDisplayWhenSelected;
    private final Expression commandExpressionToDisplayWhenSelected;
    
    public DataSetContext(final DataSet<RowWithCommands> dataSet, final CommandMetadatas commandMetadatas, final PropertiesRepo properties) {
        this.dataSet = dataSet;
        this.properties = properties;
        this.commandMetadatas = commandMetadatas;
        String dataExpressionToDisplayWhenSelected = properties.get(DATA_EXPRESSION_TO_DISPLAY_WHEN_SELECTED);
        String commandExpressionToDisplayWhenSelected = properties.get(COMMAND_EXPRESSION_TO_DISPLAY_WHEN_SELECTED);
        
        if(dataExpressionToDisplayWhenSelected != null || commandExpressionToDisplayWhenSelected != null){
            FreemarkerCompiler freemarkerCompiler = new FreemarkerCompiler(properties);
            if(dataExpressionToDisplayWhenSelected != null){
                this.dataExpressionToDisplayWhenSelected = freemarkerCompiler.compile(dataExpressionToDisplayWhenSelected);
            } else {
                this.dataExpressionToDisplayWhenSelected = null;
            }
            if(commandExpressionToDisplayWhenSelected != null){
                this.commandExpressionToDisplayWhenSelected = freemarkerCompiler.compile(commandExpressionToDisplayWhenSelected);
            } else {
                this.commandExpressionToDisplayWhenSelected = null;
            }
        } else {
            this.commandExpressionToDisplayWhenSelected = null;
            this.dataExpressionToDisplayWhenSelected = null;
        }
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
        return (commandMetadatas.size() == 1 && properties.getAsBoolean(SKIP_COMMAND_BROWSE_IF_ONLY_ONE_COMMAND_CONFIGURED, false));
    }

    public boolean zeroCommandsConfigured() {
        return commandMetadatas.isEmpty();
    }

    public List<String> getDataColumnsToDisplay() {
        final String configuredColumnsToDisplayInTable = properties.get(COLUMNS_TO_DISPLAY_IN_DATA_TABLE);
        if(configuredColumnsToDisplayInTable != null){
            return Arrays.asList(configuredColumnsToDisplayInTable.split(","));
        } else {
            return dataSet.getColumnHeadings();
        }
    }

    public List<String> getDataColumnsToIndex() {
        final String configuredColumnsToDisplayInTable = properties.get(COLUMNS_TO_INDEX_IN_DATA_TABLE);
        if(configuredColumnsToDisplayInTable != null){
            return Arrays.asList(configuredColumnsToDisplayInTable.split(","));
        } else {
            return dataSet.getColumnHeadings();
        }
    }

    public List<String> getCommandColumnsToDisplay() {
        final String configuredColumnsToDisplayInTable = properties.get(COLUMNS_TO_DISPLAY_IN_COMMAND_TABLE);
        if(configuredColumnsToDisplayInTable != null){
            return Arrays.asList(configuredColumnsToDisplayInTable.split(","));
        } else {
            return Command.getCommandTableColumnHeadings();
        }
    }

    public List<String> getCommandColumnsToIndex() {
        final String configuredColumnsToDisplayInTable = properties.get(COLUMNS_TO_INDEX_IN_COMMAND_TABLE);
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
        if(dataExpressionToDisplayWhenSelected != null){
            return dataExpressionToDisplayWhenSelected.resolve(selectedRow);
        } else {
            return query;
        }
    }

    public String getValueToDisplayWhenCommandRowSelected(final Row selectedRow, final String query) {
        if(commandExpressionToDisplayWhenSelected != null){
            return commandExpressionToDisplayWhenSelected.resolve(selectedRow);
        } else if(query != null && !query.isEmpty()){
            return query;
        } else {
            return selectedRow.get(Command.ColumnHeadings.Name.toString());
        }
    }

    public String getValueToDisplayWhenCommandRowSelected(final Row selectedRow) {
        return getValueToDisplayWhenCommandRowSelected(selectedRow, null);
    }

    public List<CommandMetadata> getCommandMetadatas() {
        return commandMetadatas.getCommandMetadatas();
    }
}
