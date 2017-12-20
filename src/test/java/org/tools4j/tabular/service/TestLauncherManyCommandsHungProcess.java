package org.tools4j.tabular.service;

import javafx.scene.input.KeyCode;
import org.junit.Test;
import org.tools4j.tabular.javafx.ExecutionService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.tools4j.tabular.service.LauncherUtils.verifyCommandSearchMode;
import static org.tools4j.tabular.service.LauncherUtils.verifyConsoleMode;
import static org.tools4j.tabular.service.LauncherUtils.verifyDataSearchMode;
import static org.tools4j.tabular.service.Utils.containsText;

/**
 * User: ben
 * Date: 24/11/17
 * Time: 7:02 AM
 */
public class TestLauncherManyCommandsHungProcess extends AbstractLauncherTest {

    @Override
    public ExecutionService getExecutionService() {
        //200 seconds is effectively hung
        return super.getExecutionServiceWithBusyProcess(200);
    }

    @Override
    public String getWorkingDir() {
        return WORKING_DIR_CONTAINING_SEARCHABLE_COMMANDS;
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
        Thread.sleep(100);
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
