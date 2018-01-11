package org.tools4j.tabular.integration;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.After;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.tools4j.tabular.javafx.ExecutionService;
import org.tools4j.tabular.javafx.Main;
import org.tools4j.tabular.util.PropertiesRepo;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 *
 * User: ben
 * Date: 29/11/17
 * Time: 6:00 PM
 */
public class AbstractLauncherTest extends ApplicationTest {
    public static final String WORKING_DIR_CONTAINING_SEARCHABLE_COMMANDS = "src/integration-test/resources/table_with_multiple_commands_with_substitutions";
    public static final String WORKING_DIR_CONTAINING_JUST_ONE_COMMAND = "src/integration-test/resources/table_with_single_command_with_substitutions";
    public static final String WORKING_DIR_CONTAINING_ZERO_COMMANDS = "src/integration-test/resources/table_no_commands";
    protected final AtomicBoolean destroyCalled = new AtomicBoolean(false);
    protected ExecutionService executionService;

    @Override
    public void init() throws TimeoutException {
        System.setProperty("workingDir", getWorkingDir());
    }

    @Override
    public void start(Stage stage) throws TimeoutException {
        destroyCalled.set(false);
        executionService = getExecutionService();
        final Main main = new Main(new PropertiesRepo(), executionService);
        try {
            main.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        FxToolkit.showStage();
    }

    @After
    public void tearDown() throws Exception {
        executionService.destroy();
    }

    public ExecutionService getExecutionService(){
        return getExecutionServiceWithSucessfullyFinished();
    }

    public String getWorkingDir() {
        return WORKING_DIR_CONTAINING_SEARCHABLE_COMMANDS;
    }

    public final ExecutionService getExecutionServiceWithBusyProcess(final int hangForSeconds){
        return new MockExecutionService(MockExecutionService.getBusyProcess(hangForSeconds, aVoid -> {
            destroyCalled.set(true);
            return null;
        }));
    }

    public final ExecutionService getExecutionServiceWithSucessfullyFinished(){
        return new MockExecutionService(MockExecutionService.getFinishedProcess());
    }

    public final ExecutionService getExecutionServiceWithFinishedWithErrors(){
        return new MockExecutionService(MockExecutionService.getFinishedWithErrorProcess());
    }

    private TableView<?> getTableView(String tableSelector) {
        Node node = super.lookup(tableSelector).query();
        if (!(node instanceof TableView)) {
            throw new RuntimeException(tableSelector + " selected " + node + " which is not a TableView!");
        }
        return (TableView<?>) node;
    }

    /**
     * @param tableSelector Selektor zur Identifikation der Tabelle.
     * @param row Zeilennummer
     * @param column Spaltennummer
     * @return Der Wert der gegebenen Zelle in der Tabelle. Es handelt sich nicht um das, was auf der UI dransteht,
     *         sondern um den Wert, also nicht notwendigerweise ein String.
     */
    protected Object cellValue(String tableSelector, int row, int column) {
        return getTableView(tableSelector).getColumns().get(column).getCellData(row);
    }

    /**
     * @param tableSelector Selektor zur Identifikation der Tabelle.
     * @param row Zeilennummer
     * @return Die entsprechende Zeile.
     */
    protected TableRow<?> row(String tableSelector, int row) {

        TableView<?> tableView = getTableView(tableSelector);

        List<Node> current = tableView.getChildrenUnmodifiable();
        while (current.size() == 1) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        current = ((Parent) current.get(1)).getChildrenUnmodifiable();
        while (!(current.get(0) instanceof TableRow)) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        Node node = current.get(row);
        if (node instanceof TableRow) {
            return (TableRow<?>) node;
        } else {
            throw new RuntimeException("Expected Group with only TableRows as children");
        }
    }

    /**
     * @param tableSelector Selektor zur Identifikation der Tabelle.
     * @param row Zeilennummer
     * @param column Spaltennummer
     * @return Die entsprechende Zelle.
     */
    protected TableCell<?, ?> cell(String tableSelector, int row, int column) {
        List<Node> current = row(tableSelector, row).getChildrenUnmodifiable();
        while (current.size() == 1 && !(current.get(0) instanceof TableCell)) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        Node node = current.get(column);
        if (node instanceof TableCell) {
            return (TableCell<?, ?>) node;
        } else {
            throw new RuntimeException("Expected TableRowSkin with only TableCells as children");
        }
    }
}
