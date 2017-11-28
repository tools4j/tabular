package org.tools4j.launcher.service;

import javafx.application.Platform;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.tools4j.launcher.javafx.Main;
import org.tools4j.launcher.util.PropertiesRepo;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.tools4j.launcher.service.LauncherUtils.verifyCommandSearchMode;
import static org.tools4j.launcher.service.LauncherUtils.verifyConsoleMode;
import static org.tools4j.launcher.service.LauncherUtils.verifyDataSearchMode;
import static org.tools4j.launcher.service.Utils.containsText;

/**
 * User: ben
 * Date: 24/11/17
 * Time: 7:02 AM
 */
public class TestLauncherHungProcess extends ApplicationTest {
    private final AtomicBoolean destroyCalled = new AtomicBoolean(false);
    private final AtomicReference<Stage> stage = new AtomicReference<>();

    @Override
    public void start(Stage stage) {
        try {
            while(this.stage.get() != null){
                System.out.println("Waiting for stage to be closed...");
                Thread.sleep(100);
            }
            System.setProperty("workingDir", "src/test/resources/test1");
            destroyCalled.set(false);
            //Will be busy for 200 seconds (effecitvely hung)
            final Main main = new Main(new PropertiesRepo(), new MockExecutionService(MockExecutionService.getBusyProcess(200, aVoid -> {
                destroyCalled.set(true);
                return null;
            })));
            main.start(stage);
            this.stage.set(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void tearDown() throws Exception {
        Platform.runLater(() -> {
            final Stage stage = this.stage.get();
            if(stage != null){
                System.out.println("Closing stage...");
                stage.close();
                this.stage.set(null);
            }
        });
    }

    @Test
    public void testHungProcess_clientForciblyStopsProcess() throws InterruptedException {
        verifyDataSearchMode(false);
        clickOn(Ids.dataSearchBox).write("Uat").type(KeyCode.ENTER, 2);
        verifyCommandSearchMode("hauu0001");
        clickOn(Ids.commandSearchBox).type(KeyCode.ENTER, 2);
        verifyConsoleMode();
        verifyThat(Ids.consoleLabel, containsText("Running"));

        //ESCAPE will halt the process
        clickOn(Ids.consoleOutput).type(KeyCode.ESCAPE);
        verifyConsoleMode();
        verifyThat(Ids.consoleLabel, containsText("Finished with error"));

        //Should be finished now
        clickOn(Ids.consoleOutput).type(KeyCode.ESCAPE);
        verifyCommandSearchMode("hauu0001");
        clickOn(Ids.commandSearchBox).type(KeyCode.ESCAPE);
        verifyDataSearchMode(true, "Uat");
        clickOn(Ids.dataSearchBox).type(KeyCode.ESCAPE);
        verifyDataSearchMode(false);
        verifyThat(Ids.dataSearchBox, hasText(""));
        assertTrue(destroyCalled.get());
    }

    @Test
    public void testHungProcess_clientLetsProcessCompleteInBackground() throws InterruptedException {
        verifyDataSearchMode(false);
        clickOn(Ids.dataSearchBox).write("Uat").type(KeyCode.ENTER, 2);
        verifyCommandSearchMode("hauu0001");
        clickOn(Ids.commandSearchBox).type(KeyCode.ENTER, 2);
        verifyConsoleMode();

        //ENTER will allow the process to complete in the background
        clickOn(Ids.consoleOutput).type(KeyCode.ENTER);
        verifyConsoleMode();
        verifyThat(Ids.consoleLabel, containsText("Letting process complete in the background"));

        //ESCAPE should let the user backtrack and run another command
        clickOn(Ids.consoleOutput).type(KeyCode.ESCAPE);
        verifyCommandSearchMode("hauu0001");
        assertFalse(destroyCalled.get());

        //Run another command
        clickOn(Ids.commandSearchBox).type(KeyCode.ENTER, 2);
        verifyConsoleMode();

        //ENTER will allow the process to complete in the background
        clickOn(Ids.consoleOutput).type(KeyCode.ENTER);
        verifyConsoleMode();
        verifyThat(Ids.consoleLabel, containsText("Letting process complete in the background"));

        //ESCAPE should let the user backtrack
        clickOn(Ids.consoleOutput).type(KeyCode.ESCAPE);
        verifyCommandSearchMode("hauu0001");
        clickOn(Ids.commandSearchBox).type(KeyCode.ESCAPE);
        verifyDataSearchMode(true, "Uat");
        clickOn(Ids.dataSearchBox).type(KeyCode.ESCAPE);
        verifyDataSearchMode(false);
        verifyThat(Ids.dataSearchBox, hasText(""));
        assertFalse(destroyCalled.get());
    }
}
