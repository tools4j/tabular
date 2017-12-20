package org.tools4j.tabular.service;

import org.apache.log4j.Logger;
import org.tools4j.tabular.util.PropertiesRepo;
import org.tools4j.tabular.util.ResolvedString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * User: ben
 * Date: 7/11/17
 * Time: 6:15 PM
 */
public class CommandMetadata extends RowFromMap implements Predicate<Row>, Pretty {
    private final static Logger LOG = Logger.getLogger(CommandMetadata.class);
    private final String name;
    private final Predicate<Row> predicate;
    private final String command;
    private final String description;

    public CommandMetadata(final String name, final Predicate<Row> predicate, final String command, final String description) {
        super(asMap(name, command, description));
        this.name = name;
        this.predicate = predicate;
        this.command = command;
        this.description = description;
    }

    public Command getCommandInstance(final Row result, final PropertiesRepo properties){
        return new Command(name, description, new ResolvedString(command, properties.asMap(), result).resolve());
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean test(final Row result) {
        return predicate.test(result);
    }

    public static Map<String, String> asMap(final String name, final String command, final String description) {
        final Map<String, String> map = new HashMap<>();
        map.put("Name", name);
        map.put("Command", command);
        map.put("Description", description);
        return map;
    }

    public static List<String> columnNames(){
        return Arrays.asList("Name", "Description");
    }

    public String toPrettyString(final String indent) {
        return toString();
    }

    @Override
    public String toString() {
        return "CommandMetadata{" +
                "name='" + name + '\'' +
                ", predicate=" + predicate +
                ", command='" + command + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
