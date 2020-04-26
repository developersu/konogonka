/*
    Copyright 2019-2020 Dmitry Isaenko

    This file is part of Konogonka.

    Konogonka is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Konogonka is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Konogonka.  If not, see <https://www.gnu.org/licenses/>.
*/
package konogonka.Controllers.RFS;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import konogonka.Controllers.IRowModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RFSFolderTableViewController implements Initializable {
    @FXML
    private TableView<RFSEntry> table;
    private ObservableList<RFSEntry> rowsObsLst;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rowsObsLst = FXCollections.observableArrayList();

        table.setPlaceholder(new Label());
        table.setEditable(false);               // At least with hacks it works as expected. Otherwise - null pointer exception
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setOnKeyPressed(keyEvent -> {
            if (!rowsObsLst.isEmpty()) {
                if (keyEvent.getCode() == KeyCode.SPACE) {
                    for (RFSEntry item : table.getSelectionModel().getSelectedItems()) {
                        item.setMarkSelected( ! item.isMarkSelected());
                    }
                    table.refresh();
                }
            }
            keyEvent.consume();
        });

        TableColumn<RFSEntry, Integer> numberColumn = new TableColumn<>(resourceBundle.getString("tableNumberLbl"));
        TableColumn<RFSEntry, String> fileNameColumn = new TableColumn<>(resourceBundle.getString("tableFileNameLbl"));
        TableColumn<RFSEntry, Long> fileOffsetColumn = new TableColumn<>(resourceBundle.getString("tableOffsetLbl"));
        TableColumn<RFSEntry, Long> fileSizeColumn = new TableColumn<>(resourceBundle.getString("tableSizeLbl"));
        TableColumn<RFSEntry, Boolean> checkBoxColumn = new TableColumn<>(resourceBundle.getString("tableUploadLbl"));

        numberColumn.setEditable(false);
        fileNameColumn.setEditable(false);
        fileOffsetColumn.setEditable(false);
        fileSizeColumn.setEditable(false);
        checkBoxColumn.setEditable(true);

        // See https://bugs.openjdk.java.net/browse/JDK-8157687
        numberColumn.setMinWidth(30.0);
        numberColumn.setPrefWidth(30.0);
        numberColumn.setMaxWidth(30.0);
        numberColumn.setResizable(false);

        fileNameColumn.setMinWidth(25.0);

        fileOffsetColumn.setMinWidth(130.0);
        fileOffsetColumn.setPrefWidth(130.0);
        fileOffsetColumn.setMaxWidth(130.0);
        fileOffsetColumn.setResizable(false);

        fileSizeColumn.setMinWidth(120.0);
        fileSizeColumn.setPrefWidth(120.0);
        fileSizeColumn.setMaxWidth(120.0);
        fileSizeColumn.setResizable(false);

        checkBoxColumn.setMinWidth(120.0);
        checkBoxColumn.setPrefWidth(120.0);
        checkBoxColumn.setMaxWidth(120.0);
        checkBoxColumn.setResizable(false);

        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        fileOffsetColumn.setCellValueFactory(new PropertyValueFactory<>("fileOffset"));

        // ><
        checkBoxColumn.setCellValueFactory(paramFeatures -> {
            RFSEntry model = paramFeatures.getValue();

            SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(model.isMarkSelected());

            booleanProperty.addListener((observableValue, oldValue, newValue) -> {
                model.setMarkSelected(newValue);
                table.refresh();
            });
            return booleanProperty;
        });

        checkBoxColumn.setCellFactory(paramFeatures -> new CheckBoxTableCell<>());
        table.setRowFactory(        // this shit is made to implement context menu. It's such a pain..
                RFSEntryTableView -> {
                    final TableRow<RFSEntry> row = new TableRow<>();
                    ContextMenu contextMenu = new ContextMenu();
                    /* // TODO: CHANGE TO 'Export' or something
                    MenuItem openMenuItem = new MenuItem("Open");
                    openMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            MediatorControl.getInstance().getContoller().showContentWindow(provider, row.getItem());
                        }
                    });

                    contextMenu.getItems().addAll(openMenuItem);
                    */
                    row.setContextMenu(contextMenu);
                    row.contextMenuProperty().bind(
                            Bindings.when(Bindings.isNotNull(row.itemProperty())).then(contextMenu).otherwise((ContextMenu)null)
                    );
                    // Just.. don't ask..
                    row.setOnMouseClicked(mouseEvent -> {
                        if (!row.isEmpty() && mouseEvent.getButton() == MouseButton.PRIMARY){
                            RFSEntry thisItem = row.getItem();
                            thisItem.setMarkSelected(!thisItem.isMarkSelected());
                            table.refresh();
                        }
                        mouseEvent.consume();
                    });
                    return row;
                }
        );
        table.setItems(rowsObsLst);
        table.getColumns().add(numberColumn);
        table.getColumns().add(fileNameColumn);
        table.getColumns().add(fileOffsetColumn);
        table.getColumns().add(fileSizeColumn);
        table.getColumns().add(checkBoxColumn);
    }
    /**
     * Add files when user selected them on left-hand tree
     * */
    public void setContent(TreeItem<RFSEntry> containerTreeItem){
        rowsObsLst.clear();

        if (containerTreeItem == null) {
            table.refresh();
            return;
        }

        for (TreeItem<RFSEntry> childTreeItem : containerTreeItem.getChildren())
            rowsObsLst.add(childTreeItem.getValue());

        table.refresh();
    }
}