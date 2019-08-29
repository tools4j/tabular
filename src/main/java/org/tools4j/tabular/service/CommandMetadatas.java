package org.tools4j.tabular.service;

import org.tools4j.tabular.util.IndentableStringBuilder;
import org.tools4j.tabular.util.PropertiesRepo;

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
    private final Map<String, CommandMetadata> commandMetadataByCommandName;

    public CommandMetadatas(final List<CommandMetadata> commands) {
        this.commandMetadataByCommandName = new LinkedHashMap<>();
        commands.forEach((commandMetadata -> commandMetadataByCommandName.put(commandMetadata.getName(), commandMetadata)));
    }

    public CommandMetadatas getCommandsFor(final Row row){
        return new CommandMetadatas(commandMetadataByCommandName.values().stream().filter((commandMetadata -> commandMetadata.test(row))).collect(Collectors.toList()));
    }

    public int size() {
        return commandMetadataByCommandName.size();
    }

    public boolean containsCommands(final String ... commandNames) {
        return Stream.of(commandNames).allMatch((command) -> commandMetadataByCommandName.containsKey(command));
    }

    public CommandMetadata get(final String commandName) {
        return commandMetadataByCommandName.get(commandName);
    }

    public List<Command> getCommandInstances(final Row row, final PropertiesRepo properties) {
        return getCommandsFor(row).commandMetadataByCommandName.values().stream().map((commandMetadata -> commandMetadata.getCommandInstance(row, properties))).collect(Collectors.toList());
    }

    @Override
    public String toPrettyString(final String indent) {
        final IndentableStringBuilder sb = new IndentableStringBuilder(indent);
        sb.append("commandMetadatas{\n");
        sb.activateIndent();
        for(final CommandMetadata commandMetadata: commandMetadataByCommandName.values()){
            sb.append(commandMetadata.toPrettyString(indent));
        }
        sb.decactivateIndent();
        sb.append("}");
        return sb.toString();
    }

    public boolean isEmpty() {
        return commandMetadataByCommandName.isEmpty();
    }
}
