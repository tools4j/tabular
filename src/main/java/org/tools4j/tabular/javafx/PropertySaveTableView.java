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

public class PropertySaveTableView extends TableView {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertySaveTableView.class);
    private List<TableColumn<?, ?>> unchangedColumns;
    private PropertyPersistenceService propertyPersistenceService;
    private String tableName;

    /**
     * Initialises the table with the given tableName
     * @param tableName the table Name
     * @param propertyPersistenceService the propertyService for storing the properties
     */
    public void init(String tableName, PropertyPersistenceService propertyPersistenceService){

        //Table name and propertyStore service
        this.tableName = tableName;
        this.propertyPersistenceService = propertyPersistenceService;

        //
        // Column order
        //

        unchangedColumns = Collections.unmodifiableList(new ArrayList<TableColumn<?, ?>>(getColumns()));

        getColumns().addListener((ListChangeListener<TableColumn<?, ?>>) change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    ObservableList<TableColumn<?, ?>> columns = getColumns();

                    String order = "";
                    for (int i = 0; i < columns.size(); ++i) {
                        order+= unchangedColumns.indexOf(columns.get(i)) + ((i==columns.size()-1)?"":",");
                    }

                    storeProperty("column.order",order);
                }
            }
        });

        restoreOrder();

        //
        // Column width
        //
        for (final TableColumn<?, ?> column: unchangedColumns) {
            column.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {

                String columnLabel = column.getText().toUpperCase();
                storeProperty("column.width."+columnLabel,newWidth+"");
                // LOGGER.info(columnLabel + ": Width: " + oldWidth + " -> " + newWidth);
            });
        }

        restoreWidth();

    }


    /**
     * Restores the order of the columns or leave it, if no propterty is saved yet.
     */
    private void restoreOrder(){
        List<TableColumn<?, ?>> currentColumns = Collections.unmodifiableList(new ArrayList<TableColumn<?, ?>>(getColumns()));

        List<TableColumn<?, ?>> orderedColumns = new ArrayList<>();

        String property = getProperty("column.order");

        if(property != null){
            String[] values = property.split(",");

            for(int i = 0 ; i < values.length; i++){
                Integer index = Integer.valueOf(values[i]);
                orderedColumns.add(currentColumns.get(index));
            }

            getColumns().setAll(orderedColumns);
        }
    }

    /**
     * Restores the width of the columns or leave it, if no property is saved yet
     */
    private void restoreWidth(){
        List<TableColumn<?, ?>> currentColumns = Collections.unmodifiableList(new ArrayList<TableColumn<?, ?>>(getColumns()));

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