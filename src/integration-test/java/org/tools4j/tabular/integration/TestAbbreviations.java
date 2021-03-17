package org.tools4j.tabular.integration;

import javafx.scene.input.KeyCode;
import org.junit.Ignore;
import org.junit.Test;
import org.tools4j.tabular.javafx.ExecutionService;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.tools4j.tabular.integration.LauncherUtils.verifyDataSearchMode;

/**
 * User: ben
 * Date: 24/11/17
 * Time: 7:02 AM
 */
@Ignore
public class TestAbbreviations extends AbstractLauncherTest {

    @Override
    public ExecutionService getExecutionService() {
        return super.getExecutionServiceWithSucessfullyFinished();
    }

    @Override
    public String getWorkingDir() {
        return WORKING_DIR_CONTAINING_ZERO_COMMANDS;
    }

    @Test
    public void testAbbreviations() {
        clickOn(Ids.dataSearchBox).write("serv").type(KeyCode.SPACE).write("a").type(KeyCode.SPACE);
        verifyThat(Ids.dataSearchBox, hasText("server Australia"));
        verifyDataSearchMode(true);
    }
}
