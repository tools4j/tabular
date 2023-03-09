package org.tools4j.tabular.service.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.properties.StringResolver;
import org.tools4j.tabular.service.Pretty;
import org.tools4j.tabular.service.Row;
import org.tools4j.tabular.service.RowFromMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * User: ben
 * Date: 7/11/17
 * Time: 6:15 PM
 */
public class CommandMetadata extends RowFromMap implements Predicate<Row>, Pretty {
    private final static Logger LOG = LoggerFactory.getLogger(CommandMetadata.class);
    private final String id;
    private final String name;
    private final Predicate<Row> predicate;
    private final String command;
    private final String description;

    public CommandMetadata(String id, final String name, final Predicate<Row> predicate, final String command, final String description) {
        super(asMap(name, command, description));
        this.id = id;
        this.name = name;
        this.predicate = predicate;
        this.command = command;
        this.description = description;
    }

    public Command getCommandInstance(final Row row, final PropertiesRepo properties){
        return new Command(id, name, description, new StringResolver(properties.asMap(), row).resolve(command));
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CommandMetadata that = (CommandMetadata) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    @Override
    public String toString() {
        return "CommandMetadata{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", predicate=" + predicate +
                ", command='" + command + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public Predicate<Row> getPredicate() {
        return predicate;
    }

    public static class Builder {
        private String id;
        private String name;
        private Predicate<Row> predicate;
        private String command;
        private String description;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withPredicate(Predicate<Row> predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder withCommand(String command) {
            this.command = command;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }
        
        public CommandMetadata build(){
            if(id == null) throw new IllegalStateException("id must not be null");
            if(name == null) throw new IllegalStateException("id must not be null");
            if(predicate == null) throw new IllegalStateException("id must not be null");
            if(command == null) throw new IllegalStateException("id must not be null");
            if(description == null) throw new IllegalStateException("id must not be null");
            return new CommandMetadata(id, name, predicate, command, description);
        }
    }
}
