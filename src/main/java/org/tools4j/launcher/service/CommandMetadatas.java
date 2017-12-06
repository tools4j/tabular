package org.tools4j.launcher.service;

import org.tools4j.launcher.util.IndentableStringBuilder;
import org.tools4j.launcher.util.PropertiesRepo;

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
    private final Map<String, CommandMetadata> commandMetadataMap;

    public CommandMetadatas(final List<CommandMetadata> commands) {
        this.commandMetadataMap = new LinkedHashMap<>();
        commands.forEach((commandMetadata -> commandMetadataMap.put(commandMetadata.getName(), commandMetadata)));
    }

    public CommandMetadatas getCommandsFor(final Row row){
        return new CommandMetadatas(commandMetadataMap.values().stream().filter((commandMetadata -> commandMetadata.test(row))).collect(Collectors.toList()));
    }

    public int size() {
        return commandMetadataMap.size();
    }

    public boolean containsCommands(final String ... commandNames) {
        return Stream.of(commandNames).allMatch((command) -> commandMetadataMap.containsKey(command));
    }

    public CommandMetadata get(final String commandName) {
        return commandMetadataMap.get(commandName);
    }

    public List<Command> getCommandInstances(final Row row, final PropertiesRepo properties) {
        return getCommandsFor(row).commandMetadataMap.values().stream().map((commandMetadata -> commandMetadata.getCommandInstance(row, properties))).collect(Collectors.toList());
    }

    @Override
    public String toPrettyString(final String indent) {
        final IndentableStringBuilder sb = new IndentableStringBuilder(indent);
        sb.append("commandMetadatas{\n");
        sb.activateIndent();
        for(final CommandMetadata commandMetadata: commandMetadataMap.values()){
            sb.append(commandMetadata.toPrettyString(indent));
        }
        sb.decactivateIndent();
        sb.append("}");
        return sb.toString();
    }

    public boolean isEmpty() {
        return commandMetadataMap.isEmpty();
    }
}
