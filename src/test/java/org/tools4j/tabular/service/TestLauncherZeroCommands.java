package org.tools4j.tabular.service;

import javafx.scene.input.KeyCode;
import org.junit.Test;
import org.tools4j.tabular.javafx.ExecutionService;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.hasItems;
import static org.tools4j.tabular.service.LauncherUtils.verifyDataSearchMode;

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
    }

    @Test
    public void testLauncher_enterWithNoSearchTextShowsAllData() throws InterruptedException {
        verifyDataSearchMode(false);
        clickOn(Ids.dataSearchBox).type(KeyCode.ENTER, 1);
        verifyThat(Ids.dataTableView, hasItems(41));
        clickOn(Ids.dataSearchBox).write("Uat").type(KeyCode.ENTER, 1);
        verifyThat(Ids.dataTableView, hasItems(18));
        clickOn(Ids.dataSearchBox).type(KeyCode.ESCAPE, 1);
        verifyDataSearchMode(false);
        clickOn(Ids.dataSearchBox).type(KeyCode.ENTER, 1);
        verifyThat(Ids.dataTableView, hasItems(41));
    }

    @Test
    public void testLauncher_hideThenHotkeyLaunch() throws InterruptedException {
        verifyDataSearchMode(false);
        clickOn(Ids.dataSearchBox).type(KeyCode.ENTER, 1);
        verifyThat(Ids.dataTableView, hasItems(41));
        clickOn(Ids.dataSearchBox).type(KeyCode.ESCAPE, 2);
        push(KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.EQUALS);
        verifyDataSearchMode(false);
    }
}
