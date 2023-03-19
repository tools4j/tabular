package org.tools4j.tabular.javafx;

import javafx.scene.control.TextArea;
import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.service.PostExecutionBehaviour;
import org.tools4j.tabular.commands.Command;

/**
 * User: ben
 * Date: 27/11/17
 * Time: 6:02 PM
 */
public class ExecutionEnvironment {
    private final ExecutionService executionService;
    private final PostExecutionBehaviour postExecutionBehaviour;
    private final TextArea console;
    private final PropertiesRepo properties;

    public ExecutionEnvironment(final ExecutionService executionService, final TextArea console, final PropertiesRepo properties, final PostExecutionBehaviour postExecutionBehaviour) {
        this.executionService = executionService;
        this.properties = properties;
        this.console = console;
        this.postExecutionBehaviour = postExecutionBehaviour;
    }

    public ExecutingCommand exec(final Command command){
        return executionService.exec(command, console, postExecutionBehaviour);
    }
}
