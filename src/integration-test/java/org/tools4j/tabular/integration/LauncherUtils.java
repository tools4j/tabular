package org.tools4j.tabular.integration;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.NodeQueryUtils.isVisible;
import static org.tools4j.tabular.integration.Utils.isEditable;
import static org.tools4j.tabular.integration.Utils.not;

/**
 * User: ben
 * Date: 27/11/17
 * Time: 6:46 AM
 */
public class LauncherUtils {

    public static void verifyDataSearchMode(final boolean expanded, final String expectedDataSearchBoxText) {
        verifyDataSearchMode(expanded);
        verifyThat(Ids.dataSearchBox, hasText(expectedDataSearchBoxText));
    }

    public static void verifyDataSearchMode(final boolean expanded) {
        verifyThat(Ids.commandSearchBox, not(isVisible()));
        verifyThat(Ids.commandTableContentPane, not(isVisible()));
        verifyThat(Ids.commandTableView, not(isVisible()));
        verifyThat(Ids.commandSearchPane, not(isVisible()));
        verifyThat(Ids.consoleOutput, not(isVisible()));
        verifyThat(Ids.consoleLabel, not(isVisible()));
        verifyThat(Ids.selectedDataLabel, not(isVisible()));
        verifyThat(Ids.separatorLabel, not(isVisible()));
        verifyThat(Ids.dataSearchBox, isVisible());

        if(expanded) {
            verifyThat(Ids.dataTableView, isVisible());
        } else {
            verifyThat(Ids.dataTableView, not(isVisible()));
        }
    }

    public static void verifyCommandSearchMode(final String selectedDataLabelText) {
        verifyThat(Ids.commandSearchBox, isVisible());
        verifyThat(Ids.commandTableContentPane, isVisible());
        verifyThat(Ids.commandTableView, isVisible());
        verifyThat(Ids.consoleOutput, not(isVisible()));
        verifyThat(Ids.consoleLabel, not(isVisible()));
        verifyThat(Ids.selectedDataLabel, isVisible());
        verifyThat(Ids.separatorLabel, isVisible());
        verifyThat(Ids.dataSearchBox, not(isVisible()));
        verifyThat(Ids.dataTableView, not(isVisible()));
        verifyThat(Ids.selectedDataLabel, hasText(selectedDataLabelText));
    }

    public static void verifyConsoleMode() {
        verifyThat(Ids.commandSearchBox, isVisible());
        verifyThat(Ids.commandSearchBox, not(isEditable()));
        verifyThat(Ids.commandTableContentPane, not(isVisible()));
        verifyThat(Ids.commandTableView, not(isVisible()));
        verifyThat(Ids.consoleOutput, isVisible());
        verifyThat(Ids.consoleLabel, isVisible());
        verifyThat(Ids.selectedDataLabel, isVisible());
        verifyThat(Ids.separatorLabel, isVisible());
        verifyThat(Ids.dataSearchBox, not(isVisible()));
        verifyThat(Ids.dataTableView, not(isVisible()));
    }
}
