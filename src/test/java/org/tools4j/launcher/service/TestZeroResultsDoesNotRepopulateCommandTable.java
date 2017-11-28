package org.tools4j.launcher.service;

import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.tools4j.launcher.javafx.Main;
import org.tools4j.launcher.util.PropertiesRepo;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;
import static org.tools4j.launcher.service.LauncherUtils.verifyCommandSearchMode;
import static org.tools4j.launcher.service.LauncherUtils.verifyDataSearchMode;

/**
 * User: ben
 * Date: 24/11/17
 * Time: 7:02 AM
 */
public class TestZeroResultsDoesNotRepopulateCommandTable extends ApplicationTest {

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

    @After
    public void tearDown() throws Exception {
        super.stop();
    }

    @Test
    public void testZeroResultsDoesNotRepopulateCommandTable() throws InterruptedException {
        verifyDataSearchMode(false);
        clickOn(Ids.dataSearchBox).write("Uat").type(KeyCode.ENTER, 2);
        verifyCommandSearchMode("hauu0001");
        clickOn(Ids.commandSearchBox).write("Open");
        verifyThat(Ids.commandTableView, (TableView t) -> t.getItems().size() == 1);
        clickOn(Ids.commandSearchBox).write("blahblahblah");
        verifyThat(Ids.commandSearchBox, hasText("Openblahblahblah"));
        verifyThat(Ids.commandTableView, (TableView t) -> t.getItems().size() == 1);
    }
}
