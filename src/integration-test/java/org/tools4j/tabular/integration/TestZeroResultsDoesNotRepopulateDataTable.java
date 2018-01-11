package org.tools4j.tabular.integration;

import javafx.scene.control.TableView;
import org.junit.Test;
import org.tools4j.tabular.javafx.ExecutionService;


import static org.testfx.api.FxAssert.verifyThat;
import static org.tools4j.tabular.integration.LauncherUtils.verifyDataSearchMode;

/**
 * User: ben
 * Date: 24/11/17
 * Time: 7:02 AM
 */
public class TestZeroResultsDoesNotRepopulateDataTable extends AbstractLauncherTest {

    @Override
    public ExecutionService getExecutionService() {
        return super.getExecutionServiceWithSucessfullyFinished();
    }

    @Override
    public String getWorkingDir() {
        return WORKING_DIR_CONTAINING_SEARCHABLE_COMMANDS;
    }

    @Test
    public void testZeroResultsDoesNotRepopulateDataTable() throws InterruptedException {
        verifyDataSearchMode(false);
        clickOn(Ids.dataSearchBox).write("Uat");
        verifyThat(Ids.dataTableView, (TableView t) -> t.getItems().size() > 10);
        clickOn(Ids.dataSearchBox).write("asdfasdffdasfasd");
        verifyThat(Ids.dataTableView, (TableView t) -> t.getItems().size() > 10);
    }
}
