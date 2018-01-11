package org.tools4j.tabular.integration;

import javafx.scene.input.KeyCode;
import org.junit.Test;
import org.tools4j.tabular.javafx.ExecutionService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.tools4j.tabular.integration.LauncherUtils.verifyConsoleMode;
import static org.tools4j.tabular.integration.LauncherUtils.verifyDataSearchMode;
import static org.tools4j.tabular.integration.Utils.containsText;

/**
 * User: ben
 * Date: 24/11/17
 * Time: 7:02 AM
 */
public class TestLauncherWithOnlyOneCommandHungProcess extends AbstractLauncherTest {

    @Override
    public ExecutionService getExecutionService() {
        //200 seconds == effectively hung
        return super.getExecutionServiceWithBusyProcess(200);
    }

    @Override
    public String getWorkingDir() {
        return WORKING_DIR_CONTAINING_JUST_ONE_COMMAND;
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
