package org.tools4j.launcher.javafx;

import javafx.scene.control.TextArea;
import org.apache.log4j.Logger;
import org.tools4j.launcher.service.Command;
import org.tools4j.launcher.service.PostExecutionBehaviour;

/**
 * User: ben
 * Date: 21/11/17
 * Time: 5:52 PM
 */
public class ExecutionServiceImpl implements ExecutionService {
    private final static Logger LOG = Logger.getLogger(LauncherPresenter.class);

    public ExecutionServiceImpl() {}

    @Override
    public ExecutingCommand exec(final Command command, final TextArea outputConsole, final PostExecutionBehaviour postExecutionBehaviour){
        Runtime rt = Runtime.getRuntime();
        try {
            LOG.info("Executing: " + command.getCommandLineString() + " in workingDir:" + System.getProperty("user.dir"));
            outputConsole.appendText("$ " + command.getCommandLineString() + "\n");
            final Process pr = rt.exec(command.getCommandLineString());
            postExecutionBehaviour.onRunning.apply(null);
            final ExecutingCommand executingCommand = new ExecutingCommand(pr, postExecutionBehaviour.onFinish, postExecutionBehaviour.onFinishWithError, outputConsole);
            executingCommand.init();
            return executingCommand;

        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }
}
