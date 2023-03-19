package org.tools4j.tabular.commands;

import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.service.Pretty;
import org.tools4j.tabular.datasets.Row;
import org.tools4j.tabular.util.IndentableStringBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: ben
 * Date: 8/11/17
 * Time: 5:45 PM
 */
public class CommandMetadatas implements Pretty {
    private final Map<String, CommandMetadata> commandMetadataById;

    public CommandMetadatas(final List<CommandMetadata> commands) {
        this.commandMetadataById = new LinkedHashMap<>();
        commands.forEach((commandMetadata -> commandMetadataById.put(commandMetadata.getId(), commandMetadata)));
    }

    public CommandMetadatas getCommandsFor(final Row row){
        return new CommandMetadatas(commandMetadataById.values().stream().filter((commandMetadata -> commandMetadata.test(row))).collect(Collectors.toList()));
    }

    public int size() {
        return commandMetadataById.size();
    }

    public boolean containsCommands(final String ... commandIds) {
        return Stream.of(commandIds).allMatch((id) -> commandMetadataById.containsKey(id));
    }

    public CommandMetadata get(final String id) {
        return commandMetadataById.get(id);
    }

    public List<Command> getCommandInstances(final Row row, final PropertiesRepo properties) {
        return getCommandsFor(row).commandMetadataById.values().stream().map((commandMetadata -> commandMetadata.getCommandInstance(row, properties))).collect(Collectors.toList());
    }

    @Override
    public String toPrettyString(final String indent) {
        final IndentableStringBuilder sb = new IndentableStringBuilder(indent);
        sb.append("commandMetadatas{\n");
        sb.activateIndent();
        for(final CommandMetadata commandMetadata: commandMetadataById.values()){
            sb.append(commandMetadata.toPrettyString(indent));
            sb.append("\n");
        }
        sb.decactivateIndent();
        sb.append("}");
        return sb.toString();
    }

    public boolean isEmpty() {
        return commandMetadataById.isEmpty();
    }

    public List<CommandMetadata> getCommandMetadatas() {
        return new ArrayList<>(this.commandMetadataById.values());
    }
}
