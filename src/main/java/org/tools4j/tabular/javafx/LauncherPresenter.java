package org.tools4j.tabular.javafx;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.tools4j.tabular.service.AsyncIndex;
import org.tools4j.tabular.service.DataSetContext;
import org.tools4j.tabular.service.LuceneIndex;
import org.tools4j.tabular.service.PostExecutionBehaviour;
import org.tools4j.tabular.service.Row;
import org.tools4j.tabular.service.RowWithCommands;
import org.tools4j.tabular.service.commands.Command;
import org.tools4j.tabular.service.commands.CommandMetadata;
import org.tools4j.tabular.util.Utils;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * User: ben
 * Date: 8/01/15
 * Time: 7:14 AM
 */
public class LauncherPresenter implements Initializable {
    private final static Logger LOG = Logger.getLogger(LauncherPresenter.class);

    @FXML
    public Label textTitle;
    @FXML
    public Label textTags;
    @FXML
    public Label labelLogo;
    @FXML
    public Label separatorLabel;
    @FXML
    public Label selectedDataLabel;
    @FXML
    public BorderPane listViewResultsParentPane;
    @FXML
    public Region resultDetailsScrollPane;
    @FXML
    public VBox mainPane;
    @FXML
    public HBox textSearchPane;
    @FXML
    public Pane commandSearchPane;
    @FXML
    public Pane whiteSpaceCover;
    @FXML
    public Pane dataSearchPane;
    @FXML
    public TextArea consoleOutput;
    @FXML
    public Pane consoleOutputContentPane;
    @FXML
    public Label consoleLabel;
    @FXML
    private TextField dataSearchBox;
    @FXML
    private TextField commandSearchBox;
    @FXML
    private TableView<RowWithCommands> dataTableView;
    @FXML
    private TableView<Command> commandTableView;
    @FXML
    private Pane dataTableContentPane;
    @FXML
    private Pane commandTableContentPane;
    @FXML
    private Pane outerTablePane;
    @FXML
    private Label dataSearchBoxBackgroundLabel;
    @FXML
    private Label commandSearchBoxBackgroundLabel;
    @FXML
    private Pane searchStackPane;
    @FXML
    private Pane commandStackPane;

    private static double xOffset = 0;
    private static double yOffset = 0;

    @Inject
    private DataSetContext dataSetContext;

    @Inject
    private Stage stage;

    @Inject
    private ExecutionService executionService;

    private ObservableList<RowWithCommands> dataTableItems;
    private ObservableList<Command> commandTableItems;

    private ExpandCollapseHelper expandCollapseHelper;
    private boolean skipCommandSearch;
    private boolean zeroCommandsConfigured;

    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle){
        try {
            final PropertyPersistenceService tablePropertySaveService = new PropertyPersistenceServiceImpl("tables");
            final AtomicReference<ExecutingCommand> executingCommand = new AtomicReference<>();
            skipCommandSearch = dataSetContext.skipCommandSearch();
            zeroCommandsConfigured = dataSetContext.zeroCommandsConfigured();
            updateCommandSearchBackgroundText();
            updateDataSearchBackgroundText();

            //Ensure that these elements grow to fit their parent HBox panes
            HBox.setHgrow(dataSearchBox, Priority.ALWAYS);
            HBox.setHgrow(commandSearchBox, Priority.ALWAYS);
            HBox.setHgrow(searchStackPane, Priority.ALWAYS);
            HBox.setHgrow(commandStackPane, Priority.ALWAYS);

            final ExecutionEnvironment executionEnvironment = new ExecutionEnvironment(executionService, consoleOutput, dataSetContext.getProperties(), new PostExecutionBehaviour(
                    () -> {
                        //Running
                        Platform.runLater(() -> {
                            consoleOutput.getStyleClass().removeAll("error", "finished");
                            consoleOutput.getStyleClass().add("running");
                            consoleLabel.getStyleClass().removeAll("error", "finished");
                            consoleLabel.getStyleClass().add("running");
                            consoleLabel.setText("Running... Press [ESC] to forcibly stop.  Press [ENTER] to allow process to complete in the background.");
                        });
                    },
                    () -> {
                        //Finished
                        Platform.runLater(() -> {
                            consoleOutput.getStyleClass().removeAll("error", "running");
                            consoleOutput.getStyleClass().add("finished");
                            consoleLabel.getStyleClass().removeAll("error", "running");
                            consoleLabel.getStyleClass().add("finished");
                            consoleLabel.setText("Finished. Press [ENTER] to minimize.  [ESC] to run another command.");
                            if(dataSetContext.getProperties().getAsBoolean("app.close.console.on.command.finish", false)){
                                exitConsoleCollapseAndMinimize();
                            }
                        });
                    },
                    () -> {
                        //Finished with error
                        Platform.runLater(() -> {
                            consoleOutput.getStyleClass().removeAll("finished", "running");
                            consoleOutput.getStyleClass().add("error");
                            consoleLabel.getStyleClass().removeAll("finished", "running");
                            consoleLabel.getStyleClass().add("error");
                            consoleLabel.setText("Finished with error. Press [ENTER] to minimize.  [ESC] to run another command.");
                        });
                    }
                )
            );

            new ShortcutInstaller( stage, dataSearchBox, dataSetContext.getProperties()).install();

            expandCollapseHelper = new ExpandCollapseHelper(stage, textSearchPane, outerTablePane);
            labelLogo.setText(">");
            separatorLabel.setText(">");
            expandCollapseHelper.setExpandedMode(false);

            //Setup dataTableView
            dataTableItems = FXCollections.observableArrayList();
            dataTableView.setItems(dataTableItems);
            for(final String columnName: dataSetContext.getDataColumnsToDisplay()){
                final TableColumn<RowWithCommands, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(param.getTableColumn().getText())));
                dataTableView.getColumns().add(column);
            }
            final PropertySaveTableHelper<RowWithCommands> dataTableSaveHelper = new PropertySaveTableHelper<>(dataTableView, "dataTableView", tablePropertySaveService);
            dataTableSaveHelper.init();

            //Setup commandTableView
            commandTableItems = FXCollections.observableArrayList();
            commandTableView.setItems(commandTableItems);
            for(final String columnName: dataSetContext.getCommandColumnsToDisplay()){
                final TableColumn<Command, String> column = new TableColumn<>(columnName);
                column.setPrefWidth(200.0);
                column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(param.getTableColumn().getText())));
                commandTableView.getColumns().add(column);
            }
            final PropertySaveTableHelper<Command> commandTableSaveHelper = new PropertySaveTableHelper<>(commandTableView, "commandTableView", tablePropertySaveService);
            commandTableSaveHelper.init();

            //Indexes
            final AsyncIndex<RowWithCommands> tableIndex = new AsyncIndex<>(
                    new LuceneIndex<>(dataSetContext.getDataSet().getRows(), dataSetContext.getDataColumnToIndexPredicate() ), results -> {
                        Platform.runLater(() -> {
                            if (results.size() > 0) {
                                dataTableItems.clear();
                                if (!expandCollapseHelper.isContentVisible()) {
                                    expandCollapseHelper.setExpandedMode(true);
                                }
                                for (final RowWithCommands result : results) {
                                    try {
                                        dataTableItems.add(result);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                dataSearchBox.getStyleClass().remove("searchBoxNoResults");
                                dataTableView.getSelectionModel().selectFirst();
                            } else if(!dataSearchBox.getStyleClass().contains("searchBoxNoResults")){
                                dataSearchBox.getStyleClass().add(0, "searchBoxNoResults");
                            }
                        });
                    });
            tableIndex.init();

            final AsyncIndex<CommandMetadata> commandIndex = new AsyncIndex<>(
                    new LuceneIndex<>(dataSetContext.getCommandMetadatas(), dataSetContext.getCommandColumnToIndexPredicate()), results -> {
                        Platform.runLater(() -> {
                            if (results.size() > 0) {
                                RowWithCommands selectedItem = dataTableView.getSelectionModel().getSelectedItem();
                                if (selectedItem == null) {
                                    return;
                                }
                                Map<String, Command> rowCommandsById = selectedItem.getCommands().stream().collect(Collectors.toMap(c -> c.getId(), c -> c));
                                commandTableItems.clear();
                                for (final CommandMetadata result : results) {
                                    try {
                                        Command rowCommand = rowCommandsById.get(result.getId());
                                        if (rowCommand != null) {
                                            commandTableItems.add(rowCommand);
                                        }
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                commandSearchBox.getStyleClass().remove("searchBoxNoResults");
                                commandTableView.getSelectionModel().selectFirst();
                            }  else if(!commandSearchBox.getStyleClass().contains("searchBoxNoResults")){
                                commandSearchBox.getStyleClass().add(0, "searchBoxNoResults");
                            }
                        });
            });
            commandIndex.init();

            dataSearchBox.setOnKeyReleased((keyEvent) -> {
                //LOG.debug("dataSearchBox.onKeyReleased " + keyEvent);
                if(!keyEvent.getCode().equals(KeyCode.ESCAPE)
                    && !keyEvent.getCode().equals(KeyCode.CONTROL)) {
                    updateDataSearchBackgroundText();
                    searchDataTable(tableIndex);
                }
            });

            commandSearchBox.setOnKeyReleased((KeyEvent) -> {
                //LOG.debug("commandSearchBox.onKeyReleased");
                if(KeyEvent.getCode() == KeyCode.ENTER){
                    return;
                }
                updateCommandSearchBackgroundText();
                searchCommandTable(commandIndex);
            });


            dataSearchBox.setOnKeyPressed((KeyEvent e) -> {
                if (e.getCode() == KeyCode.DOWN) {
                    dataTableView.requestFocus();
                    dataTableView.getSelectionModel().selectFirst();

                } else if (e.getCode() == KeyCode.UP) {
                    dataTableView.requestFocus();
                    dataTableView.getSelectionModel().selectNext();

                } else if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.TAB) {
                    dataTableView.requestFocus();
                    dataTableView.getSelectionModel().selectFirst();
                    if(dataTableItems.size() == 1){
                        onSelectCurrentDatatableRow(executingCommand, executionEnvironment);
                    }

                } else if (e.getCode() == KeyCode.ESCAPE) {
                    LOG.debug("Escape key pressed from dataSearchBox");
                    onDataSearchEscapeKeyPressed();

                } else if (e.getCode() == KeyCode.SPACE) {
                    LOG.debug("Space key pressed");
                    String searchText = dataSearchBox.getText();
                    Optional<String> lastWordInSearchText = Utils.getLastWordInText(searchText);
                    if(lastWordInSearchText.isPresent()){
                        String replacement = dataSetContext.getDataSearchAbbreviation(lastWordInSearchText.get());
                        if(replacement != null){
                            String newText = Utils.replaceLastWordWith(searchText, replacement);
                            dataSearchBox.setText(newText);
                            dataSearchBox.positionCaret(dataSearchBox.getText().length());
                        }
                    }

                } else if (e.isControlDown() && !e.isShiftDown()) {
                    if (e.getCode() == KeyCode.R) {
                        LOG.debug("TODO: rebuild indexes");

                    } else if (e.getCode() == KeyCode.Q) {
                        Platform.exit();
                        System.exit(0);
                    }

                } else if (e.isControlDown() && e.isShiftDown()) {
                    if (e.getCode() == KeyCode.D) {
                        debugGui(mainPane);
                    } else if (e.getCode() == KeyCode.M) {
                        installDebugClickEventHandlerRecursivelyOnNode(mainPane);
                    }
                }
            });

            commandSearchBox.setOnKeyPressed((KeyEvent e) -> {
                if (e.getCode() == KeyCode.DOWN) {
                    commandTableView.requestFocus();
                    commandTableView.getSelectionModel().selectFirst();

                } else if (e.getCode() == KeyCode.UP) {
                    commandTableView.requestFocus();
                    commandTableView.getSelectionModel().selectNext();

                } else if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.TAB) {
                    commandTableView.requestFocus();
                    commandTableView.getSelectionModel().selectFirst();
                    if(commandTableItems.size() == 1) {
                        onSelectCurrentCommandTableRow(executingCommand, executionEnvironment);
                    }

                } else if (e.getCode() == KeyCode.ESCAPE) {
                    LOG.debug("Escape key pressed from commandSearchBox");
                    returnToDataSearchMode();

                } else if (e.getCode() == KeyCode.SPACE) {
                    LOG.debug("Space key pressed");
                    String searchText = commandSearchBox.getText();
                    Optional<String> lastWordInSearchText = Utils.getLastWordInText(searchText);
                    if(lastWordInSearchText.isPresent()){
                        String replacement = dataSetContext.getCommandSearchAbbreviation(lastWordInSearchText.get());
                        if(replacement != null){
                            String newText = Utils.replaceLastWordWith(searchText, replacement);
                            commandSearchBox.setText(newText);
                            commandSearchBox.positionCaret(commandSearchBox.getText().length());
                        }
                    }

                } else if (e.isControlDown() && !e.isShiftDown()) {
                    if (e.getCode() == KeyCode.R) {
                        LOG.debug("TODO: rebuild indexes");

                    } else if (e.getCode() == KeyCode.Q) {
                        Platform.exit();
                        System.exit(0);
                    }

                } else if (e.isControlDown() && e.isShiftDown()) {
                    if (e.getCode() == KeyCode.D) {
                        debugGui(mainPane);
                    } else if (e.getCode() == KeyCode.M) {
                        installDebugClickEventHandlerRecursivelyOnNode(mainPane);
                    }
                }
            });

            dataTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    //LOG.debug("New value selected");
                }
            });

            commandTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    //LOG.debug("New value selected");
                }
            });

            dataTableView.setOnMouseClicked(event -> {
                if(event.getClickCount() >= 2){
                    if( event.getTarget() instanceof Text ){
                        onSelectCurrentDatatableRow(executingCommand, executionEnvironment);
                    }
                } else if(event.getButton().equals(MouseButton.SECONDARY)){
                    saveToClipboard(((Text) event.getTarget()).getText());
                }
            });

            commandTableView.setOnMouseClicked(event -> {
                if(event.getClickCount() >= 2){
                    if( event.getTarget() instanceof Text ){
                        onSelectCurrentCommandTableRow(executingCommand, executionEnvironment);
                    }
                } else if(event.getButton().equals(MouseButton.SECONDARY)){
                    saveToClipboard(((Text) event.getTarget()).getText());
                }
            });

            dataTableView.setOnKeyPressed((KeyEvent e) -> {
                if( e.getCode() == KeyCode.A && e.isControlDown() && !e.isAltDown() && !e.isShiftDown()){
                    final String tableAsCsv = dataTableItems.stream().map((row) -> row.toCsv()).collect(Collectors.joining("\n"));
                    saveToClipboard(tableAsCsv);

                } else if( e.getCode() == KeyCode.C && e.isControlDown() && !e.isAltDown() && !e.isShiftDown()){
                    final String rowAsCsv = dataTableView.getSelectionModel().getSelectedItem().toCsv();
                    saveToClipboard(rowAsCsv);

                } else if (e.getCode() == KeyCode.ENTER) {
                    LOG.debug("Enter key pressed:" + e);
                    onSelectCurrentDatatableRow(executingCommand, executionEnvironment);

                } else if (e.getCode() == KeyCode.ESCAPE) {
                    LOG.debug("Escape key pressed from dataTable");
                    onDataSearchEscapeKeyPressed();

                } else if(!e.isControlDown()
                        && !e.isAltDown()
                        && e.getText().matches("\\w")){
                    dataSearchBox.setText(dataSearchBox.getText() + " ");
                    dataSearchBox.requestFocus();
                    dataSearchBox.end();
                }
            });

            commandTableView.setOnKeyPressed((KeyEvent e) -> {
                if( e.getCode() == KeyCode.A && e.isControlDown() && !e.isAltDown() && !e.isShiftDown()){
                    final String tableAsCsv = commandTableItems.stream().map((row) -> row.toCsv()).collect(Collectors.joining("\n"));
                    saveToClipboard(tableAsCsv);

                } else if( e.getCode() == KeyCode.C && e.isControlDown() && !e.isAltDown() && !e.isShiftDown()){
                    final Row selectedDataTableRow = dataTableView.getSelectionModel().getSelectedItem();
                    final String rowAsCsv = selectedDataTableRow.toCsv();
                    saveToClipboard(rowAsCsv);

                } else if (e.getCode() == KeyCode.ENTER) {
                    onSelectCurrentCommandTableRow(executingCommand, executionEnvironment);

                } else if (e.getCode() == KeyCode.ESCAPE) {
                    commandSearchBox.requestFocus();
                    commandSearchBox.selectAll();

                } else if(!e.isControlDown()
                        && !e.isAltDown()
                        && e.getText().matches("\\w")){
                    commandSearchBox.setText(commandSearchBox.getText() + " ");
                    commandSearchBox.requestFocus();
                    commandSearchBox.end();
                }
            });

            consoleOutput.setOnKeyPressed((KeyEvent e) -> {
                if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.ESCAPE) {
                    final ExecutingCommand executingCommandInstance = executingCommand.get();
                    if(executingCommandInstance != null && !executingCommandInstance.isFinished()){
                        if (e.getCode() == KeyCode.ESCAPE) {
                            LOG.warn("Forcibly stopping command...");
                            executingCommandInstance.stop();
                            executingCommand.set(null);
                        } else if (e.getCode() == KeyCode.ENTER) {
                            LOG.info("Letting process complete...");
                            executingCommand.set(null);
                            consoleLabel.setText("Letting process complete in the background... Press [ENTER] to minimize. [ESC] to run another command.");
                        }
                        return;
                    }
                }
                if (e.getCode() == KeyCode.ENTER) {
                    exitConsoleCollapseAndMinimize();

                } else if (e.getCode() == KeyCode.ESCAPE) {
                    exitConsoleMode();
                }
            });

            labelLogo.setOnMousePressed(event -> {
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            });

            labelLogo.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            });

            //whiteSpaceCover.toFront();


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void updateDataSearchBackgroundText(){
        if(dataSearchBox.getText().isEmpty()){
            if(dataSearchBoxBackgroundLabel.getText().isEmpty()) {
                dataSearchBoxBackgroundLabel.setText(dataSetContext.getProperties().get("app.data.search.background.prompt.text", ""));
            }
        } else {
            if(!dataSearchBoxBackgroundLabel.getText().isEmpty()){
                dataSearchBoxBackgroundLabel.setText("");
            }
        }
    }
    
    private void updateCommandSearchBackgroundText(){
        if(commandSearchBox.getText().isEmpty()){
            if(commandSearchBoxBackgroundLabel.getText().isEmpty()) {
                commandSearchBoxBackgroundLabel.setText(dataSetContext.getProperties().get("app.command.search.background.prompt.text", ""));
            }
        } else {
            if(!commandSearchBoxBackgroundLabel.getText().isEmpty()){
                commandSearchBoxBackgroundLabel.setText("");
            }
        }
    }

    private void searchCommandTable(final AsyncIndex<CommandMetadata> commandIndex) {
        if (commandSearchBox.getText() != null
                && commandSearchBox.getText().length() > 0
                    && !(commandSearchBox.getSelectedText() != null
                    && commandSearchBox.getSelectedText().length() > 0
                    && commandSearchBox.getSelectedText().equals(commandSearchBox.getText()))) {

            commandIndex.search(commandSearchBox.getText());
        }
    }

    private void searchDataTable(final AsyncIndex<RowWithCommands> tableIndex) {
        final List<RowWithCommands> results;
        if (dataSearchBox.getText() == null || dataSearchBox.getText().length() == 0) {
            tableIndex.search("");
        } else {
            tableIndex.search(dataSearchBox.getText());
        }
    }

    private void onSelectCurrentCommandTableRow(final AtomicReference<ExecutingCommand> executingCommand, final ExecutionEnvironment executionEnvironment) {
        final Command selectedRow = commandTableView.getSelectionModel().getSelectedItem();
        enterConsoleModeAndExecuteCommand(executingCommand, executionEnvironment, selectedRow);
    }

    private void onSelectCurrentDatatableRow(final AtomicReference<ExecutingCommand> executingCommand, final ExecutionEnvironment executionEnvironment) {
        if(!zeroCommandsConfigured) {
            final RowWithCommands selectedRow = dataTableView.getSelectionModel().getSelectedItem();
            selectedDataLabel.setText(dataSetContext.getValueToDisplayWhenDataRowSelected(selectedRow, dataSearchBox.getText()));
            dataSearchPane.setVisible(false);
            dataTableContentPane.setVisible(false);
            commandSearchPane.setVisible(true);

            if (skipCommandSearch) {
                final Command command = selectedRow.getCommands().get(0);
                commandSearchBox.setText(dataSetContext.getValueToDisplayWhenCommandRowSelected(command));
                commandSearchBox.setEditable(false);
                enterConsoleModeAndExecuteCommand(executingCommand, executionEnvironment, command);

            } else {
                commandTableItems.clear();
                commandTableItems.setAll(selectedRow.getCommands());
                commandTableContentPane.setVisible(true);
                commandSearchBox.clear();
                updateDataSearchBackgroundText();
                commandSearchBox.requestFocus();
            }
        }
    }

    private void enterConsoleModeAndExecuteCommand(final AtomicReference<ExecutingCommand> executingCommand, final ExecutionEnvironment executionEnvironment, final Command selectedRow) {
        consoleOutput.clear();
        enterConsoleMode();
        final ExecutingCommand executingCommandInstance = executionEnvironment.exec(selectedRow);
        executingCommand.set(executingCommandInstance);
    }

    private void enterConsoleMode() {
        consoleOutputContentPane.setVisible(true);
        commandTableContentPane.setVisible(false);
        consoleOutput.requestFocus();
        commandSearchBox.setEditable(false);
    }

    private void exitConsoleCollapseAndMinimize() {
        exitConsoleMode();
        returnToDataSearchModeCollapseAndMinimize();
    }

    private void exitConsoleMode() {
        consoleOutputContentPane.setVisible(false);
        if(skipCommandSearch){
            returnToDataSearchMode();
        } else {
            returnToCommandSearchMode();
        }
    }

    private void returnToCommandSearchMode() {
        commandTableContentPane.setVisible(true);
        commandSearchBox.setEditable(true);
        commandSearchBox.requestFocus();
        commandSearchBox.selectAll();
    }

    private void returnToDataSearchMode() {
        commandTableContentPane.setVisible(false);
        dataTableContentPane.setVisible(true);
        dataSearchPane.setVisible(true);
        commandSearchPane.setVisible(false);
        dataSearchBox.requestFocus();
        dataSearchBox.selectAll();
    }

    private void returnToDataSearchModeCollapseAndMinimize() {
        returnToDataSearchMode();
        clearResultsAndCollapseExpansion();
        minimize();
    }

    private void installDebugClickEventHandlerRecursivelyOnNode(final Node node) {
        LOG.debug("Installing mouseClicked event handler on node: " + node);
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> LOG.debug(node.getClass().getSimpleName()
                + " id:" + node.getId()
                + " styleClass:" + node.getStyleClass()
                + " style:" + node.getStyle()));
        if (node instanceof Parent) {
            final Parent parent = (Parent) node;
            parent.getChildrenUnmodifiable().forEach(this::installDebugClickEventHandlerRecursivelyOnNode);
        }
    }

    private void debugGui(final Node node) {
        debugGui(node, "");
    }

    private void debugGui(final Node node, String currentIndentLevel) {
        LOG.debug(currentIndentLevel
                + node.getClass().getSimpleName()
                + " id:" + node.getId()
                + " styleClass:" + node.getStyleClass()
                + " style:" + node.getStyle());
        if (node instanceof Parent) {
            final Parent parent = (Parent) node;
            parent.getChildrenUnmodifiable().forEach((child) -> debugGui(child, currentIndentLevel + "    "));
        }
    }

    private void onDataSearchEscapeKeyPressed() {
        if (expandCollapseHelper.isContentVisible()) {
            clearResultsAndCollapseExpansion();
        } else {
            minimize();
        }
    }

    private void clearResultsAndCollapseExpansion() {
        clearAndReset();
        dataSearchBox.clear();
        dataSearchBox.requestFocus();
        updateDataSearchBackgroundText();
    }

    private void minimize() {
        stage.setIconified(true);
    }


    private void clearAndReset() {
        dataTableItems.clear();
        expandCollapseHelper.setExpandedMode(false);
    }

    private void saveToClipboard(final String text){
        LOG.info("Saving to clipboard: " + text);
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }
}
