package org.tools4j.launcher.service;

import javafx.scene.input.KeyCode;
import org.junit.Test;
import org.tools4j.launcher.javafx.ExecutionService;

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
public class TestLauncherZeroCommands extends AbstractLauncherTest {

    @Override
    public ExecutionService getExecutionService() {
        return super.getExecutionServiceWithSucessfullyFinished();
    }

    @Override
    public String getWorkingDir() {
        return WORKING_DIR_CONTAINING_ZERO_COMMANDS;
    }

    @Test
    public void testLauncher() throws InterruptedException {
        verifyDataSearchMode(false);
        clickOn(Ids.dataSearchBox).write("Uat").type(KeyCode.ENTER, 2);
        verifyDataSearchMode(true);
        clickOn(Ids.dataSearchBox).type(KeyCode.ENTER, 2);
        verifyDataSearchMode(true);
        clickOn(Ids.dataSearchBox).type(KeyCode.ESCAPE, 1);
        Thread.sleep(1000);
    }
}
