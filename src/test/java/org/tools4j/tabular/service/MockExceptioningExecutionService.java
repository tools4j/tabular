package org.tools4j.tabular.service;

import javafx.scene.control.TextArea;
import org.apache.log4j.Logger;
import org.tools4j.tabular.javafx.CommandExecutedWithException;
import org.tools4j.tabular.javafx.DefaultExecutingCommand;
import org.tools4j.tabular.javafx.ExecutingCommand;
import org.tools4j.tabular.javafx.ExecutionService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * User: ben
 * Date: 28/11/17
 * Time: 6:16 AM
 */
public class MockExceptioningExecutionService implements ExecutionService {
    private final static Logger LOG = Logger.getLogger(MockExceptioningExecutionService.class);
    private List<ExecutingCommand> executedCommands;

    public MockExceptioningExecutionService() {
        this.executedCommands = new ArrayList<>();
    }

    @Override
    public ExecutingCommand exec(final Command command, final TextArea outputConsole, final PostExecutionBehaviour postExecutionBehaviour) {
        outputConsole.appendText("Mocked Executor!\n");
        outputConsole.appendText("Was instructed to execute this command: " + command.getCommandLineString() + "\n");
        postExecutionBehaviour.onRunning.run();
        final ExecutingCommand executingCommand = new CommandExecutedWithException(new RuntimeException("Mock Exception"), outputConsole, postExecutionBehaviour);
        executingCommand.init();
        executedCommands.add(executingCommand);
        return executingCommand;
    }

    @Override
    public void destroy() {
        LOG.info("Shutting down any previously run command which are still executing.");
        for(final ExecutingCommand command: executedCommands){
            if(!command.isFinished()){
                LOG.info("Shutting command which was still executing...");
                command.stop();
            }
        }
    }
}
