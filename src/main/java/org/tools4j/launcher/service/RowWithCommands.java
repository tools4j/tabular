package org.tools4j.launcher.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: ben
 * Date: 14/11/17
 * Time: 6:17 AM
 */
public class RowWithCommands extends RowDecorator {
    private final List<Command> commands;

    public RowWithCommands(final Row row, final List<Command> commands) {
        super(row);
        this.commands = commands;
    }

    public RowWithCommands(final Map<String, String> row) {
        super(new RowFromMap(row));
        this.commands = Collections.emptyList();
    }

    public List<Command> getCommands() {
        return commands;
    }

    public TableWithColumnHeadings<Command> getCommandsTable() {
        return new TableWithColumnHeadings<Command>() {
            @Override
            public List<Command> getRows() {
                return commands;
            }

            @Override
            public List<String> getColumnHeadings() {
                return Command.getCommandTableColumnHeadings();
            }

            @Override
            public int size() {
                return commands.size();
            }
        };
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof RowWithCommands)) return false;

        final RowWithCommands row = (RowWithCommands) o;

        return commands.equals(row.commands);
    }

    @Override
    public int hashCode() {
        return commands.hashCode();
    }
}
