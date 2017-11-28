package org.tools4j.launcher.service;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.tools4j.launcher.javafx.Main;
import org.tools4j.launcher.util.PropertiesRepo;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.tools4j.launcher.service.LauncherUtils.verifyCommandSearchMode;
import static org.tools4j.launcher.service.LauncherUtils.verifyConsoleMode;
import static org.tools4j.launcher.service.LauncherUtils.verifyDataSearchMode;

/**
 * User: ben
 * Date: 24/11/17
 * Time: 7:02 AM
 */
public class TestLauncher extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        try {
            System.setProperty("workingDir", "src/test/resources/test1");
            final Main main = new Main(new PropertiesRepo(), new MockExecutionService(MockExecutionService.getFinishedProcess()));
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
        clickOn(Ids.consoleOutput).type(KeyCode.ESCAPE);
        verifyCommandSearchMode("hauu0001");
        clickOn(Ids.commandSearchBox).type(KeyCode.ESCAPE);
        verifyDataSearchMode(true);
        clickOn(Ids.dataSearchBox).type(KeyCode.ESCAPE);
        verifyDataSearchMode(false);
        verifyThat(Ids.dataSearchBox, hasText(""));
        clickOn(Ids.dataSearchBox).type(KeyCode.ESCAPE);
    }
}
