package org.tools4j.tabular.service;

import javafx.scene.control.TextArea;
import org.apache.log4j.Logger;
import org.tools4j.tabular.javafx.DefaultExecutingCommand;
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
public class MockExecutionService implements ExecutionService {
    private final static Logger LOG = Logger.getLogger(MockExecutionService.class);
    private List<DefaultExecutingCommand> executedCommands;
    private final Process process;

    public MockExecutionService(final Process process) {
        this.process = process;
        this.executedCommands = new ArrayList<>();
    }

    @Override
    public DefaultExecutingCommand exec(final Command command, final TextArea outputConsole, final PostExecutionBehaviour postExecutionBehaviour) {
        outputConsole.appendText("Mocked Executor!\n");
        outputConsole.appendText("Was instructed to execute this command: " + command.getCommandLineString() + "\n");
        postExecutionBehaviour.onRunning.run();
        final DefaultExecutingCommand executingCommand = new DefaultExecutingCommand(process, postExecutionBehaviour.onFinish, postExecutionBehaviour.onFinishWithError, outputConsole);
        executingCommand.init();
        executedCommands.add(executingCommand);
        return executingCommand;
    }

    @Override
    public void destroy() {
        LOG.info("Shutting down any previously run command which are still executing.");
        for(final DefaultExecutingCommand command: executedCommands){
            if(!command.isFinished()){
                LOG.info("Shutting command which was still executing...");
                command.stop();
            }
        }
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
            private AtomicReference<Thread> sleepingThread = new AtomicReference<>();

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
                /*
                Not thread safe.  Potential for a Check-then-act race condition.
                But there shouldn't be multiple threads in this space anyway, so not concerned.
                 */
                sleepingThread.set(Thread.currentThread());
                try {
                    Thread.sleep(hangForSeconds * 1000);
                } finally {
                    sleepingThread.set(null);
                }
               return 0;
            }

            @Override
            public int exitValue() {
                return 1;
            }

            @Override
            public void destroy() {
                try {
                    if(sleepingThread.get() != null){
                        LOG.info("Shutting down sleeping thread...");
                        sleepingThread.get().interrupt();
                    }
                } finally {
                    onDestroyedCallback.apply(null);
                }
            }
        };
    }
}
