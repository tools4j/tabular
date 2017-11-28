package org.tools4j.launcher.service;

import javafx.scene.control.TextArea;
import org.tools4j.launcher.javafx.ExecutingCommand;
import org.tools4j.launcher.javafx.ExecutionService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

/**
 * User: ben
 * Date: 28/11/17
 * Time: 6:16 AM
 */
public class MockExecutionService implements ExecutionService {
    private final Process process;

    public MockExecutionService(final Process process) {
        this.process = process;
    }

    @Override
    public ExecutingCommand exec(final Command command, final TextArea outputConsole, final PostExecutionBehaviour postExecutionBehaviour) {
        outputConsole.appendText("Mocked Executor!\n");
        outputConsole.appendText("Was instructed to execute this command: " + command.getCommandLineString() + "\n");
        postExecutionBehaviour.onRunning.apply(null);
        final ExecutingCommand executingCommand = new ExecutingCommand(process, postExecutionBehaviour.onFinish, postExecutionBehaviour.onFinishWithError, outputConsole);
        executingCommand.init();
        return executingCommand;
    }


    public static Process getFinishedProcess(){
        return new Process() {
            @Override
            public OutputStream getOutputStream() {
                return new ByteArrayOutputStream(0);
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream("".getBytes());
            }

            @Override
            public InputStream getErrorStream() {
                return new ByteArrayInputStream("".getBytes());
            }

            @Override
            public int waitFor() throws InterruptedException {
                return 0;
            }

            @Override
            public int exitValue() {
                return 0;
            }

            @Override
            public void destroy() {
            }
        };
    }

    public static Process getFinishedWithErrorProcess(){
        return new Process() {
            public boolean destroyed = false;

            @Override
            public OutputStream getOutputStream() {
                return new ByteArrayOutputStream(0);
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream("".getBytes());
            }

            @Override
            public InputStream getErrorStream() {
                return new ByteArrayInputStream("".getBytes());
            }

            @Override
            public int waitFor() throws InterruptedException {
                return 0;
            }

            @Override
            public int exitValue() {
                return 1;
            }

            @Override
            public void destroy() {
            }
        };
    }

    public static Process getBusyProcess(final int hangForSeconds, final Function<Void, Void> onDestroyedCallback){
        return new Process() {
            @Override
            public OutputStream getOutputStream() {
                return new ByteArrayOutputStream(0);
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream("".getBytes());
            }

            @Override
            public InputStream getErrorStream() {
                return new ByteArrayInputStream("".getBytes());
            }

            @Override
            public int waitFor() throws InterruptedException {
               Thread.sleep(hangForSeconds*1000);
               return 0;
            }

            @Override
            public int exitValue() {
                return 1;
            }

            @Override
            public void destroy() {
                onDestroyedCallback.apply(null);
            }
        };
    }
}
