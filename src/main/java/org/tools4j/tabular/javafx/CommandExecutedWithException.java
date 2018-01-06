package org.tools4j.tabular.javafx;

import javafx.scene.control.TextArea;
import org.apache.log4j.Logger;
import org.tools4j.tabular.service.PostExecutionBehaviour;

/**
 * User: ben
 * Date: 29/12/17
 * Time: 7:04 AM
 */
public class CommandExecutedWithException implements ExecutingCommand {
    private final static Logger LOG = Logger.getLogger(CommandExecutedWithException.class);
    private final TextArea outputConsole;
    private final PostExecutionBehaviour postExecutionBehaviour;
    private final Throwable exception;

    public CommandExecutedWithException(final Throwable exception, final TextArea outputConsole, final PostExecutionBehaviour postExecutionBehaviour) {
        this.exception = exception;
        this.outputConsole = outputConsole;
        this.postExecutionBehaviour = postExecutionBehaviour;
    }

    @Override
    public void init(){
        outputConsole.appendText(exception.getMessage());
        postExecutionBehaviour.onFinishWithError.run();
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Process was never started due to an exception occuring when the command was first run.");
    }
}
