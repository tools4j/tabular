package org.tools4j.tabular.integration;

import javafx.scene.input.KeyCode;
import org.junit.Test;
import org.tools4j.tabular.javafx.ExecutionService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.tools4j.tabular.integration.LauncherUtils.verifyCommandSearchMode;
import static org.tools4j.tabular.integration.LauncherUtils.verifyConsoleMode;
import static org.tools4j.tabular.integration.LauncherUtils.verifyDataSearchMode;

/**
 * User: ben
 * Date: 24/11/17
 * Time: 7:02 AM
 */
public class TestLauncherManyCommands extends AbstractLauncherTest {

    @Override
    public ExecutionService getExecutionService() {
        return super.getExecutionServiceWithSucessfullyFinished();
    }

    @Override
    public String getWorkingDir() {
        return WORKING_DIR_CONTAINING_SEARCHABLE_COMMANDS;
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
