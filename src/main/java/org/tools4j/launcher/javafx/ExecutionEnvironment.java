package org.tools4j.launcher.javafx;

import javafx.scene.control.TextArea;
import org.tools4j.launcher.service.Command;
import org.tools4j.launcher.service.PostExecutionBehaviour;

/**
 * User: ben
 * Date: 27/11/17
 * Time: 6:02 PM
 */
public class ExecutionEnvironment {
    private final ExecutionService executionService;
    private final PostExecutionBehaviour postExecutionBehaviour;
    private final TextArea console;

    public ExecutionEnvironment(final ExecutionService executionService, final TextArea console, final PostExecutionBehaviour postExecutionBehaviour) {
        this.executionService = executionService;
        this.console = console;
        this.postExecutionBehaviour = postExecutionBehaviour;
    }

    public ExecutingCommand exec(final Command command){
        return executionService.exec(command, console, postExecutionBehaviour);
    }
}
