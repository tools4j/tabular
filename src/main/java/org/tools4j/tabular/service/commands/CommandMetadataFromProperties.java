package org.tools4j.tabular.service.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.tabular.properties.PropertiesRepo;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ben
 * Date: 7/11/17
 * Time: 6:15 PM
 */
public class CommandMetadataFromProperties {
    private final static Logger LOG = LoggerFactory.getLogger(CommandMetadata.class);
    private final PropertiesRepo propertiesRepo;

    public CommandMetadataFromProperties(final PropertiesRepo propertiesRepo) {
        this.propertiesRepo = propertiesRepo;
    }

    public CommandMetadatas load(){
        final List<CommandMetadata> commandMetadata = new ArrayList<>();
        PropertiesRepo commandsProperties = propertiesRepo.getWithPrefix("app.commands");
        if(commandsProperties.isEmpty()){
            PropertiesRepo withLegacyPrefix = propertiesRepo.getWithPrefix("app.commmands");
            if(!withLegacyPrefix.isEmpty()){
                LOG.warn("It appears that your commands are defined in your property file with prefix 'app.commmands' (three m's). This may not continue to be supported.  Please update to be prefixed with 'app.commands' (two m's)");
                commandsProperties = withLegacyPrefix;
            }
        }
        for(final String commandKey: commandsProperties.getNextUniqueKeyParts()){
            final PropertiesRepo commandProperties = commandsProperties.getWithPrefix(commandKey);
            final String id = commandKey;
            final String name = commandProperties.getMandatory("name", "name property must be specified for command " + commandKey);
            final String predicateGroovyStr = commandProperties.getMandatory("predicate", "predicate property must be specified for command " + commandKey);
            final String command = commandProperties.getMandatory("command", "command property must be specified for command " + commandKey);
            final String description = commandProperties.get("description", "");

            //Instantiate an expression instance now for fail-fast-ness
            commandMetadata.add(new CommandMetadata(id, name, new GroovyExpressionPredicate(predicateGroovyStr, propertiesRepo), command, description));
        }
        return new CommandMetadatas(commandMetadata);
    }
}
