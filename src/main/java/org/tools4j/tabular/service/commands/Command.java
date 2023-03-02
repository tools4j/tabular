package org.tools4j.tabular.service.commands;

import org.tools4j.tabular.service.RowFromMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * User: ben
 * Date: 7/11/17
 * Time: 6:22 PM
 */
public class Command extends RowFromMap {
    private final String id;
    private final String name;
    private final String commandLineString;

    public Command(String id, final String name, final String description, final String commandLineString) {
        super(asMap(name, description, commandLineString));
        this.id = id;
        this.name = name;
        this.commandLineString = commandLineString;
    }

    public enum ColumnHeadings{
        Name, Description, CommandLine;
    }

    private static Map<String, String> asMap(final String name, final String description, final String commandLineString) {
        final Map<String, String> map = new HashMap<>();
        map.put("Name", name);
        map.put("Description", description);
        map.put("CommandLine", commandLineString);
        return map;
    }

    public static List<String> getCommandTableColumnHeadings(){
        return Arrays.stream(ColumnHeadings.values()).map(h -> h.toString()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return commandLineString;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String resolveCommandLineString() {
        return new StringWithEmbeddedGroovy(commandLineString).resolve();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Command command = (Command) o;
        return Objects.equals(id, command.id) &&
                Objects.equals(name, command.name) &&
                Objects.equals(commandLineString, command.commandLineString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, commandLineString);
    }
}
