package org.tools4j.tabular.service

import org.tools4j.tabular.commands.Command
import org.tools4j.tabular.commands.CommandMetadata
import org.tools4j.tabular.commands.CommandMetadataFromProperties
import org.tools4j.tabular.commands.CommandMetadatas
import org.tools4j.tabular.datasets.Row
import org.tools4j.tabular.datasets.RowFromMap
import org.tools4j.tabular.properties.PropertiesFromString
import org.tools4j.tabular.properties.PropertiesRepo
import org.tools4j.tabular.service.datasets.ExpressionCompiler
import org.tools4j.tabular.service.datasets.FreemarkerCompiler
import spock.lang.Specification

/**
 * User: ben
 * Date: 8/11/17
 * Time: 5:28 PM
 */
class CommandMetadataTest extends Specification {
    private PropertiesRepo propertiesRepo
    private CommandMetadatas commandsMetadata
    private Row row;

    def setup(){
        propertiesRepo = new PropertiesRepo(new PropertiesFromString('''
            app.commmands.openHomeDir.name=Open Home Dir
            app.commmands.openHomeDir.predicate=true
            app.commmands.openHomeDir.command=ssh ${h} && cd ${d}
            
            app.commmands.startApplication.name=Start App
            app.commmands.startApplication.predicate='${e}' != 'prod'
            app.commmands.startApplication.command=ssh ${h} && cd ${d}/bin && ./start.sh
            
            app.commmands.tailAppLog.name=Tail App Log
            app.commmands.tailAppLog.predicate='${e}' == 'prod'
            app.commmands.tailAppLog.command=ssh ${h} && cd ${l} && tail -f ${.data_model["app.log.filename"]}
            
            log_filename=app.log
        ''').load());

        commandsMetadata = new CommandMetadataFromProperties(propertiesRepo).load()
        row = new RowFromMap([h: "myhostname", e: "prod", d: "~/", l: "~/logs/"]);
    }

    def "test command size"() {
        given:
        assert commandsMetadata.size() == 3
    }

    def "test get commands for row"() {
        given:
        final CommandMetadatas commandsForRow = commandsMetadata.getCommandsFor(row);
        assert commandsForRow.size() == 2
        assert commandsForRow.containsCommands("openHomeDir", "tailAppLog")
    }

    def "get command 1"() {
        given:
        final CommandMetadatas commandsForRow = commandsMetadata.getCommandsFor(row);
        final ExpressionCompiler expressionCompiler = new FreemarkerCompiler(propertiesRepo);
        commandsForRow.compile(expressionCompiler);
        final CommandMetadata commandMetadata = commandsForRow.get("openHomeDir");
        final Command command = commandMetadata.getCommandInstance(row);
        assert command.toString() == 'ssh myhostname && cd ~/'
    }

    def "get command 2"() {
        given:
        final CommandMetadatas commandsForRow = commandsMetadata.getCommandsFor(row);
        final ExpressionCompiler expressionCompiler = new FreemarkerCompiler(propertiesRepo);
        commandsForRow.compile(expressionCompiler);
        final CommandMetadata commandMetadata = commandsForRow.get("tailAppLog");
        final Command command = commandMetadata.getCommandInstance(row);
        assert command.toString() == 'ssh myhostname && cd ~/logs/ && tail -f app.log'
    }
}
