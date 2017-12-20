package org.tools4j.tabular.javafx;

import javafx.scene.control.TextArea;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * User: ben
 * Date: 23/11/17
 * Time: 6:35 AM
 */
public class ExecutingCommand {
    private final static Logger LOG = Logger.getLogger(ExecutingCommand.class);

    private final Process process;
    private final Function<Void, Void> onFinish;
    private final Function<Void, Void> onFinishWithError;
    private final TextArea outputConsole;
    private final AtomicBoolean finished = new AtomicBoolean(false);
    private final AtomicBoolean errorOccurred = new AtomicBoolean(false);


    public ExecutingCommand(final Process process, final Function<Void, Void> onFinish, final Function<Void, Void> onFinishWithError, final TextArea outputConsole) {
        this.process = process;
        this.onFinish = onFinish;
        this.onFinishWithError = onFinishWithError;
        this.outputConsole = outputConsole;
    }

    public void init(){
        try {
            final InputStream errorStream = process.getErrorStream();
            final InputStream inputStream = process.getInputStream();
            final Executor executor = Executors.newFixedThreadPool(3);

            executor.execute(() -> {
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    errorOccurred.set(true);
                    e.printStackTrace();
                } finally {
                    finished.set(true);
                    if(errorOccurred.get()){
                        onFinishWithError.apply(null);
                    } else {
                        onFinish.apply(null);
                    }
                }
            });

            executor.execute(() -> {
                while(!finished.get()){
                    try {
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while((line = reader.readLine()) != null){
                            outputConsole.appendText(line + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            executor.execute(() -> {
                while(!finished.get()){
                    try {
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                        String line;
                        while((line = reader.readLine()) != null){
                            outputConsole.appendText("ERROR: " + line + "\n");
                            errorOccurred.set(true);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            LOG.error(e);
        }
    }

    public boolean isFinished(){
        return finished.get();
    }

    public void stop(){
        if(finished.get()){
            return;
        } else {
            try {
                process.destroyForcibly();
            } finally {
                onFinishWithError.apply(null);
            }
        }
    }
}
