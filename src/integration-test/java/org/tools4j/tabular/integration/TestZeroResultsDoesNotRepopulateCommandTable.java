package org.tools4j.tabular.integration;

import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import org.junit.Test;
import org.tools4j.tabular.javafx.ExecutionService;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextMatchers.hasText;
import static org.tools4j.tabular.integration.LauncherUtils.verifyCommandSearchMode;
import static org.tools4j.tabular.integration.LauncherUtils.verifyDataSearchMode;

/**
 * User: ben
 * Date: 24/11/17
 * Time: 7:02 AM
 */
public class TestZeroResultsDoesNotRepopulateCommandTable extends AbstractLauncherTest {

    @Override
    public ExecutionService getExecutionService() {
        return super.getExecutionServiceWithSucessfullyFinished();
    }

    @Override
    public String getWorkingDir() {
        return WORKING_DIR_CONTAINING_SEARCHABLE_COMMANDS;
    }

    @Test
    public void testZeroResultsDoesNotRepopulateCommandTable() throws InterruptedException {
        verifyDataSearchMode(false);
        sleep(500);
        clickOn(Ids.dataSearchBox).write("Uat").type(KeyCode.ENTER, 2);
        verifyCommandSearchMode("hauu0001");
        clickOn(Ids.commandSearchBox).write("Open");
        verifyThat(Ids.commandTableView, (TableView t) -> t.getItems().size() == 1);
        clickOn(Ids.commandSearchBox).write("blahblahblah");
        verifyThat(Ids.commandSearchBox, hasText("Openblahblahblah"));
        verifyThat(Ids.commandTableView, (TableView t) -> t.getItems().size() == 1);
    }
}
