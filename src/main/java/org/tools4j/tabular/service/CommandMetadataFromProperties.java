package org.tools4j.tabular.service;

import org.tools4j.tabular.properties.PropertiesRepo;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ben
 * Date: 7/11/17
 * Time: 6:15 PM
 */
public class CommandMetadataFromProperties {
    private final PropertiesRepo propertiesRepo;

    public CommandMetadataFromProperties(final PropertiesRepo propertiesRepo) {
        this.propertiesRepo = propertiesRepo;
    }

    public CommandMetadatas load(){
        final List<CommandMetadata> commandMetadata = new ArrayList<>();
        final PropertiesRepo commandsProperties = propertiesRepo.getWithPrefix("app.commmands");
        for(final String commandKey: commandsProperties.getNextUniqueKeyParts()){
            final PropertiesRepo commandProperties = commandsProperties.getWithPrefix(commandKey);
            final String name = commandProperties.getMandatory("name", "name property must be specified for command " + commandKey);
            final String predicateGroovyStr = commandProperties.getMandatory("predicate", "predicate property must be specified for command " + commandKey);
            final String command = commandProperties.getMandatory("command", "command property must be specified for command " + commandKey);
            final String description = commandProperties.get("description", "");

            //Instantiate an expression instance now for fail-fast-ness
            commandMetadata.add(new CommandMetadata(name, new GroovyExpressionPredicate(predicateGroovyStr, propertiesRepo), command, description));
        }
        return new CommandMetadatas(commandMetadata);
    }
}
