package org.tools4j.tabular.integration;

import javafx.scene.input.KeyCode;
import org.junit.Test;
import org.tools4j.tabular.javafx.ExecutionService;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.tools4j.tabular.integration.LauncherUtils.verifyCommandSearchMode;
import static org.tools4j.tabular.integration.LauncherUtils.verifyDataSearchMode;

/**
 * User: ben
 * Date: 24/11/17
 * Time: 7:02 AM
 */
public class TestLauncherBackgroundLabels extends AbstractLauncherTest {

    public static final String DATA_SEARCH = "Data Search";
    public static final String COMMAND_SEARCH = "Command Search";
    public static final String EMPTY = "";

    @Override
    public ExecutionService getExecutionService() {
        return super.getExecutionServiceWithSucessfullyFinished();
    }

    @Override
    public String getWorkingDir() {
        return WORKING_DIR_CONTAINING_SEARCHABLE_COMMANDS;
    }

    @Test
    public void testDataSearchBackgroundLabel() throws InterruptedException {
        verifyDataSearchMode(false);
        verifyThat(Ids.dataSearchBoxBackgroundLabel, hasText(DATA_SEARCH));

        clickOn(Ids.dataSearchBox).write("Uat");
        verifyThat(Ids.dataSearchBox, hasText("Uat"));
        verifyThat(Ids.dataSearchBoxBackgroundLabel, hasText(EMPTY));
        preventTextBoxFromThinkingItsGettingDoubleClick();

        clickOn(Ids.dataSearchBox).push(KeyCode.BACK_SPACE);
        verifyThat(Ids.dataSearchBox, hasText("Ua"));
        verifyThat(Ids.dataSearchBoxBackgroundLabel, hasText(EMPTY));
        preventTextBoxFromThinkingItsGettingDoubleClick();

        clickOn(Ids.dataSearchBox).push(KeyCode.BACK_SPACE);
        verifyThat(Ids.dataSearchBox, hasText("U"));
        verifyThat(Ids.dataSearchBoxBackgroundLabel, hasText(EMPTY));
        preventTextBoxFromThinkingItsGettingDoubleClick();

        clickOn(Ids.dataSearchBox).push(KeyCode.BACK_SPACE);
        verifyThat(Ids.dataSearchBox, hasText(""));
        verifyThat(Ids.dataSearchBoxBackgroundLabel, hasText(DATA_SEARCH));
        preventTextBoxFromThinkingItsGettingDoubleClick();

        //Write in text again, then press esc to clear
        clickOn(Ids.dataSearchBox).write("Uat");
        verifyThat(Ids.dataSearchBox, hasText("Uat"));
        verifyThat(Ids.dataSearchBoxBackgroundLabel, hasText(EMPTY));

        clickOn(Ids.dataSearchBox).type(KeyCode.ESCAPE);
        verifyThat(Ids.dataSearchBox, hasText(""));
        verifyThat(Ids.dataSearchBoxBackgroundLabel, hasText(DATA_SEARCH));
    }

    @Test
    public void testCommandSearchBackgroundLabel() throws InterruptedException {
        verifyDataSearchMode(false);
        verifyThat(Ids.dataSearchBoxBackgroundLabel, hasText(DATA_SEARCH));

        clickOn(Ids.dataSearchBox).write("Uat").type(KeyCode.ENTER, 2);
        verifyCommandSearchMode("hauu0001");
        verifyThat(Ids.commandSearchBoxBackgroundLabel, hasText(COMMAND_SEARCH));
        preventTextBoxFromThinkingItsGettingDoubleClick();

        clickOn(Ids.commandSearchBox).write("Bla");
        verifyThat(Ids.commandSearchBox, hasText("Bla"));
        verifyThat(Ids.commandSearchBoxBackgroundLabel, hasText(EMPTY));
        preventTextBoxFromThinkingItsGettingDoubleClick();

        clickOn(Ids.commandSearchBox).type(KeyCode.BACK_SPACE);
        verifyThat(Ids.commandSearchBox, hasText("Bl"));
        verifyThat(Ids.commandSearchBoxBackgroundLabel, hasText(EMPTY));
        preventTextBoxFromThinkingItsGettingDoubleClick();

        clickOn(Ids.commandSearchBox).type(KeyCode.BACK_SPACE);
        verifyThat(Ids.commandSearchBox, hasText("B"));
        verifyThat(Ids.commandSearchBoxBackgroundLabel, hasText(EMPTY));
        preventTextBoxFromThinkingItsGettingDoubleClick();

        clickOn(Ids.commandSearchBox).type(KeyCode.BACK_SPACE);
        verifyThat(Ids.commandSearchBox, hasText(""));
        verifyThat(Ids.commandSearchBoxBackgroundLabel, hasText(COMMAND_SEARCH));
        preventTextBoxFromThinkingItsGettingDoubleClick();

        //Write in text again, then press esc to clear
        clickOn(Ids.commandSearchBox).write("Bla");
        verifyThat(Ids.commandSearchBox, hasText("Bla"));
        verifyThat(Ids.commandSearchBoxBackgroundLabel, hasText(EMPTY));
        preventTextBoxFromThinkingItsGettingDoubleClick();

        //Select a command
        clickOn(Ids.commandSearchBox).type(KeyCode.DOWN);
        verifyThat(Ids.commandSearchBox, hasText("Open Home Dir"));
        verifyThat(Ids.commandSearchBoxBackgroundLabel, hasText(EMPTY));

        push(KeyCode.ESCAPE);
        verifyThat(Ids.commandSearchBox, hasText("Open Home Dir"));
        verifyThat(Ids.commandSearchBoxBackgroundLabel, hasText(EMPTY));
        preventTextBoxFromThinkingItsGettingDoubleClick();

        clickOn(Ids.commandSearchBox).type(KeyCode.ESCAPE);
    }

    private void preventTextBoxFromThinkingItsGettingDoubleClick() {
        clickOn(Ids.labelLogo);
    }
}
