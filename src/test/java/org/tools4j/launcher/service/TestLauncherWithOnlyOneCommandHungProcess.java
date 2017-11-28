package org.tools4j.launcher.service;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.tools4j.launcher.javafx.Main;
import org.tools4j.launcher.util.PropertiesRepo;

import java.util.concurrent.atomic.AtomicBoolean;

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
public class TestLauncherWithOnlyOneCommandHungProcess extends ApplicationTest {
    private final AtomicBoolean destroyCalled = new AtomicBoolean(false);
    @Override
    public void start(Stage stage) {
        try {
            System.setProperty("workingDir", "src/test/resources/test5");
            destroyCalled.set(false);
            final Main main = new Main(new PropertiesRepo(), new MockExecutionService(MockExecutionService.getBusyProcess(100, aVoid -> {
                destroyCalled.set(true);
                return null;
            })));
            main.start(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testClientForciblyStopsCommand() throws InterruptedException {
        verifyDataSearchMode(false);
        clickOn(Ids.dataSearchBox).write("Uat").type(KeyCode.ENTER, 2);
        verifyThat(Ids.selectedDataLabel, hasText("hauu0001"));
        verifyConsoleMode();
        verifyThat(Ids.consoleLabel, containsText("Running"));

        //Escape will forcibly stop the command
        clickOn(Ids.consoleOutput).type(KeyCode.ESCAPE);
        verifyThat(Ids.consoleLabel, containsText("Finished with error"));
        assertTrue(destroyCalled.get());

        clickOn(Ids.consoleOutput).type(KeyCode.ESCAPE);
        verifyDataSearchMode(true, "Uat");
        clickOn(Ids.dataSearchBox).type(KeyCode.ESCAPE);
        verifyDataSearchMode(false);
        verifyThat(Ids.dataSearchBox, hasText(""));
    }


    @Test
    public void testHungProcess_clientLetsProcessComplete() throws InterruptedException {
        verifyDataSearchMode(false);
        clickOn(Ids.dataSearchBox).write("Uat").type(KeyCode.ENTER, 2);
        verifyConsoleMode();

        //ENTER will allow the process to complete in the background
        clickOn(Ids.consoleOutput).type(KeyCode.ENTER);
        verifyConsoleMode();
        verifyThat(Ids.consoleLabel, containsText("Letting process complete in the background"));

        //ESCAPE should let the user backtrack and run another command
        clickOn(Ids.consoleOutput).type(KeyCode.ESCAPE);
        verifyDataSearchMode(true,"Uat");
        assertFalse(destroyCalled.get());

        //Run another command
        clickOn(Ids.dataSearchBox).type(KeyCode.ENTER, 2);
        verifyConsoleMode();

        //ENTER will allow the process to complete in the background
        clickOn(Ids.consoleOutput).type(KeyCode.ENTER);
        verifyConsoleMode();
        verifyThat(Ids.consoleLabel, containsText("Letting process complete in the background"));

        //ESCAPE should let the user backtrack
        clickOn(Ids.consoleOutput).type(KeyCode.ESCAPE);
        verifyDataSearchMode(true, "Uat");
        clickOn(Ids.dataSearchBox).type(KeyCode.ESCAPE);
        verifyDataSearchMode(false);
        verifyThat(Ids.dataSearchBox, hasText(""));
        assertFalse(destroyCalled.get());
    }
}
