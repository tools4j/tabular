package org.tools4j.tabular.service;

import javafx.scene.input.KeyCode;
import org.junit.Test;
import org.tools4j.tabular.javafx.ExecutionService;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.tools4j.tabular.service.LauncherUtils.verifyConsoleMode;
import static org.tools4j.tabular.service.LauncherUtils.verifyDataSearchMode;
import static org.tools4j.tabular.service.Utils.containsText;

/**
 * User: ben
 * Date: 24/11/17
 * Time: 7:02 AM
 */
public class TestLauncherWithOnlyOneCommand extends AbstractLauncherTest {

    @Override
    public ExecutionService getExecutionService() {
        return super.getExecutionServiceWithSucessfullyFinished();
    }

    @Override
    public String getWorkingDir() {
        return WORKING_DIR_CONTAINING_JUST_ONE_COMMAND;
    }

    @Test
    public void testLauncherWithOnlyOneCommand() throws InterruptedException {
        verifyDataSearchMode(false);
        clickOn(Ids.dataSearchBox).write("Uat").type(KeyCode.ENTER, 2);
        verifyThat(Ids.selectedDataLabel, hasText("hauu0001"));
        verifyConsoleMode();
        verifyThat(Ids.consoleLabel, containsText("Finished"));
        clickOn(Ids.consoleOutput).type(KeyCode.ESCAPE);
        verifyDataSearchMode(true, "Uat");
        clickOn(Ids.dataSearchBox).type(KeyCode.ESCAPE);
        verifyDataSearchMode(false);
        verifyThat(Ids.dataSearchBox, hasText(""));
    }
}
