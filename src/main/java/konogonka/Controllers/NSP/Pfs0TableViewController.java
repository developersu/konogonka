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
package konogonka.Controllers.NSP;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import konogonka.Controllers.IRowModel;
import konogonka.MediatorControl;
import konogonka.Tools.ISuperProvider;
import konogonka.Tools.PFS0.IPFS0Provider;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Pfs0TableViewController implements Initializable {
    @FXML
    private TableView<Pfs0RowModel> table;
    private ObservableList<Pfs0RowModel> rowsObsLst;

    private ISuperProvider provider;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rowsObsLst = FXCollections.observableArrayList();

        table.setPlaceholder(new Label());
        table.setEditable(false);               // At least with hacks it works as expected. Otherwise - null pointer exception
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (!rowsObsLst.isEmpty()) {
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        for (Pfs0RowModel item : table.getSelectionModel().getSelectedItems()) {
                            item.setMarkSelected(!item.isMarkSelected());
                        }
                        table.refresh();
                    }
                }
                keyEvent.consume();
            }
        });

        TableColumn<Pfs0RowModel, Integer> numberColumn = new TableColumn<>(resourceBundle.getString("tableNumberLbl"));
        TableColumn<Pfs0RowModel, String> fileNameColumn = new TableColumn<>(resourceBundle.getString("tableFileNameLbl"));
        TableColumn<Pfs0RowModel, Long> fileOffsetColumn = new TableColumn<>(resourceBundle.getString("tableOffsetLbl"));
        TableColumn<Pfs0RowModel, Long> fileSizeColumn = new TableColumn<>(resourceBundle.getString("tableSizeLbl"));
        TableColumn<Pfs0RowModel, Boolean> uploadColumn = new TableColumn<>(resourceBundle.getString("tableUploadLbl"));

        numberColumn.setEditable(false);
        fileNameColumn.setEditable(false);
        fileOffsetColumn.setEditable(false);
        fileSizeColumn.setEditable(false);
        uploadColumn.setEditable(true);

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

        uploadColumn.setMinWidth(120.0);
        uploadColumn.setPrefWidth(120.0);
        uploadColumn.setMaxWidth(120.0);
        uploadColumn.setResizable(false);

        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        fileOffsetColumn.setCellValueFactory(new PropertyValueFactory<>("fileOffset"));
        // ><
        uploadColumn.setCellValueFactory(paramFeatures -> {
            Pfs0RowModel model = paramFeatures.getValue();

            SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(model.isMarkSelected());

            booleanProperty.addListener((observableValue, oldValue, newValue) -> {
                model.setMarkSelected(newValue);
                table.refresh();
            });
            return booleanProperty;
        });

        uploadColumn.setCellFactory(paramFeatures -> new CheckBoxTableCell<>());
        table.setRowFactory(        // this shit is made to implement context menu. It's such a pain..
                Pfs0RowModelTableView -> {
                    final TableRow<Pfs0RowModel> row = new TableRow<>();
                    ContextMenu contextMenu = new ContextMenu();

                    MenuItem openMenuItem = new MenuItem("Open");
                    openMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            MediatorControl.getInstance().getContoller().showContentWindow(provider, row.getItem());    // TODO: change to something better
                        }
                    });

                    contextMenu.getItems().addAll(openMenuItem);

                    row.setContextMenu(contextMenu);
                    row.contextMenuProperty().bind(
                            Bindings.when(Bindings.isNotNull(row.itemProperty())).then(contextMenu).otherwise((ContextMenu)null)
                    );
                    // Just.. don't ask..
                    row.setOnMouseClicked(mouseEvent -> {
                        if (!row.isEmpty() && mouseEvent.getButton() == MouseButton.PRIMARY){
                            Pfs0RowModel thisItem = row.getItem();
                            thisItem.setMarkSelected(!thisItem.isMarkSelected());
                            table.refresh();
                        }
                        mouseEvent.consume();
                    });
                    return row;
                }
        );
        table.setItems(rowsObsLst);
        table.getColumns().addAll(numberColumn, fileNameColumn, fileOffsetColumn, fileSizeColumn,  uploadColumn);
    }
    /**
     * Add files when user selected them
     * */
    public void setNSPToTable(IPFS0Provider pfs){
        this.provider = pfs;
        rowsObsLst.clear();
        Pfs0RowModel.resetNumCnt();
        if (pfs == null) {
            table.refresh();
            return;
        }

        for (int i=0; i < pfs.getFilesCount(); i++){
            rowsObsLst.add(new Pfs0RowModel(
                    pfs.getPfs0subFiles()[i].getName(),
                    pfs.getPfs0subFiles()[i].getSize(),
                    pfs.getPfs0subFiles()[i].getOffset()
            ));
        }
        table.refresh();
    }
    /**
     * Return list of models checked. Requested from NSLMainController only -> uploadBtnAction()                            //TODO: set undefined
     * @return null if no files marked for upload
     *         List<File> if there are files
     * */
    public List<IRowModel> getFilesForDump(){
        List<IRowModel> models = new ArrayList<>();
        if (rowsObsLst.isEmpty())
            return null;
        for (Pfs0RowModel model: rowsObsLst) {
            if (model.isMarkSelected()) {
                models.add(model);
            }
        }
        return models;
    }
    public ISuperProvider getProvider(){ return provider; }
}