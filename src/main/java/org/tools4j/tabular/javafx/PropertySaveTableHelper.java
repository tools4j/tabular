package org.tools4j.tabular.javafx;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PropertySaveTableHelper<T> {
    private final static Logger LOG = LoggerFactory.getLogger(PropertySaveTableHelper.class);

    private final PropertyPersistenceService propertyPersistenceService;
    private final String tableName;
    private final TableView<T> tableView;
    private List<TableColumn<T, ?>> unchangedColumns;

    public PropertySaveTableHelper(final TableView<T> tableView, final String tableName, final PropertyPersistenceService propertyPersistenceService){
        this.tableView = tableView;
        this.tableName = tableName;
        this.propertyPersistenceService = propertyPersistenceService;
    }

    public void init(){

        //
        // Column order
        //

        unchangedColumns = Collections.unmodifiableList(new ArrayList<>(tableView.getColumns()));

        tableView.getColumns().addListener((ListChangeListener<TableColumn<T, ?>>) change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    ObservableList<TableColumn<T, ?>> columns = tableView.getColumns();
                    String order = "";
                    for (int i = 0; i < columns.size(); ++i) {
                        order += unchangedColumns.indexOf(columns.get(i)) + ((i==columns.size()-1)?"":",");
                    }
                    storeProperty("column.order",order);
                }
            }
        });

        restoreOrder();

        //
        // Column width
        //
        for (final TableColumn<T, ?> column: unchangedColumns) {
            column.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {

                String columnLabel = column.getText().toUpperCase();
                storeProperty("column.width."+columnLabel,newWidth+"");
                // LOGGER.info(columnLabel + ": Width: " + oldWidth + " -> " + newWidth);
            });
        }

        restoreWidth();

    }


    /**
     * Restores the order of the columns or leave it, if no property is saved yet.
     */
    private void restoreOrder(){
        List<TableColumn<T, ?>> currentColumns = Collections.unmodifiableList(new ArrayList<TableColumn<T, ?>>(tableView.getColumns()));
        List<TableColumn<T, ?>> orderedColumns = new ArrayList<>();
        String property = getProperty("column.order");

        try {
            if (property != null) {
                String[] values = property.split(",");

                for (int i = 0; i < values.length; i++) {
                    Integer index = Integer.valueOf(values[i]);
                    orderedColumns.add(currentColumns.get(index));
                }

                tableView.getColumns().setAll(orderedColumns);
            }
        } catch(IndexOutOfBoundsException e){
            LOG.error("Column ordering invalid, columns must have changed.  Will wait until order is changed and persist that.");
        }
    }

    /**
     * Restores the width of the columns or leave it, if no property is saved yet
     */
    private void restoreWidth(){
        List<TableColumn<T, ?>> currentColumns = Collections.unmodifiableList(new ArrayList<TableColumn<T, ?>>(tableView.getColumns()));
        for(int i = 0; i < currentColumns.size(); i++){
            TableColumn<?,?> column = currentColumns.get(i);
            String propertyKey = "column.width."+column.getText().toUpperCase();
            String property = getProperty(propertyKey);
            if (property != null) {
                Double width = Double.valueOf(property);
                column.setPrefWidth(width);
            }
        }
    }

    /**
     * Stores the property value under the given key
     * @param key the property key
     * @param value the String value
     */
    private void storeProperty(String key, String value){
        String propertyKey = tableName + "." + key;
        propertyPersistenceService.save(propertyKey, value);
    }

    private String getProperty(String key){
        return propertyPersistenceService.getByKey(tableName + "." + key);
    }
}