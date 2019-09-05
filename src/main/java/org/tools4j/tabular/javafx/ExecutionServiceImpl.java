package org.tools4j.tabular.javafx;

import javafx.scene.control.TextArea;
import org.apache.log4j.Logger;
import org.tools4j.tabular.service.Command;
import org.tools4j.tabular.service.PostExecutionBehaviour;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ben
 * Date: 21/11/17
 * Time: 5:52 PM
 */
public class ExecutionServiceImpl implements ExecutionService {
    private final static Logger LOG = Logger.getLogger(LauncherPresenter.class);
    private final List<ExecutingCommand> executedCommands;

    public ExecutionServiceImpl() {
        executedCommands = new ArrayList<>();
    }

    @Override
    public ExecutingCommand exec(final Command command, final TextArea outputConsole, final PostExecutionBehaviour postExecutionBehaviour){
        Runtime rt = Runtime.getRuntime();
        final Process pr;
        try {
            LOG.info("Executing: " + command.resolveCommandLineString() + " in workingDir:" + System.getProperty("user.dir"));
            outputConsole.appendText("$ " + command.resolveCommandLineString() + "\n");

            ExecutingCommand executingCommand;
            try {
                postExecutionBehaviour.onRunning.run();
                pr = rt.exec(command.resolveCommandLineString());
                executingCommand = new DefaultExecutingCommand(pr, postExecutionBehaviour.onFinish, postExecutionBehaviour.onFinishWithError, outputConsole);
            } catch (Exception e){
                LOG.error("Error running command '" + command + "' :" + e.getMessage());
                executingCommand = new CommandExecutedWithException(e, outputConsole, postExecutionBehaviour);
            }
            executingCommand.init();
            executedCommands.add(executingCommand);
            return executingCommand;

        } catch (Exception e) {
            LOG.error(e);
            throw e;
        }
    }

    @Override
    public void destroy() {
        LOG.info("Shutting down any previously run command which are still executing.");
        for(final ExecutingCommand command: executedCommands){
            if(!command.isFinished()){
                LOG.warn("Shutting command which was still executing...");
                command.stop();
            }
        }

    }
}
