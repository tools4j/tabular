package org.tools4j.launcher.service;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.tools4j.launcher.javafx.Main;
import org.tools4j.launcher.util.PropertiesRepo;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
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
public class TestLauncherBusyProcess extends ApplicationTest {
    private final AtomicBoolean destroyCalled = new AtomicBoolean(false);

    @Override
    public void start(Stage stage) {
        try {
            System.setProperty("workingDir", "src/test/resources/test1");
            destroyCalled.set(false);
            //Will be busy for 2 seconds only
            final Main main = new Main(new PropertiesRepo(), new MockExecutionService(MockExecutionService.getBusyProcess(2, aVoid -> {
                destroyCalled.set(true);
                return null;
            })));
            main.start(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLauncher() throws InterruptedException {
        verifyDataSearchMode(false);
        clickOn(Ids.dataSearchBox).write("Uat").type(KeyCode.ENTER, 2);
        verifyCommandSearchMode("hauu0001");
        clickOn(Ids.commandSearchBox).type(KeyCode.ENTER, 2);
        verifyConsoleMode();

        //ENTER will do nothing as command is still running
        clickOn(Ids.consoleOutput).type(KeyCode.ENTER);
        verifyConsoleMode();
        verifyThat(Ids.consoleLabel, containsText("Running"));
        Thread.sleep(3000);

        //Should be finished now
        verifyThat(Ids.consoleLabel, containsText("Finished"));
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
