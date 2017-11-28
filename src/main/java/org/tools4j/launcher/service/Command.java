package org.tools4j.launcher.service;

import com.sun.org.glassfish.gmbal.Description;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: ben
 * Date: 7/11/17
 * Time: 6:22 PM
 */
public class Command extends RowFromMap {
    private final String name;
    private final String commandLineString;

    public Command(final String name, final String description, final String commandLineString) {
        super(asMap(name, description, commandLineString));
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

    public String getCommandLineString() {
        return commandLineString;
    }
}
