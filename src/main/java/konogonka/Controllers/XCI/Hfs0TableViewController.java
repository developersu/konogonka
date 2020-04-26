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
package konogonka.Controllers.XCI;

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
import konogonka.Tools.XCI.HFS0Provider;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Hfs0TableViewController implements Initializable {
    @FXML
    private TableView<Hfs0RowModel> table;
    private ObservableList<Hfs0RowModel> rowsObsLst;

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
                        for (Hfs0RowModel item : table.getSelectionModel().getSelectedItems()) {
                            item.setMarkSelected(!item.isMarkSelected());
                        }
                        table.refresh();
                    }
                }
                keyEvent.consume();
            }
        });

        TableColumn<Hfs0RowModel, Integer> numberColumn = new TableColumn<>(resourceBundle.getString("tableNumberLbl"));
        TableColumn<Hfs0RowModel, String> fileNameColumn = new TableColumn<>(resourceBundle.getString("tableFileNameLbl"));
        TableColumn<Hfs0RowModel, Long> fileOffsetColumn = new TableColumn<>(resourceBundle.getString("tableOffsetLbl"));
        TableColumn<Hfs0RowModel, Long> fileSizeColumn = new TableColumn<>(resourceBundle.getString("tableSizeLbl"));
        TableColumn<Hfs0RowModel, Long> fileHashedRegionSizeColumn = new TableColumn<>("Hashed Region Size");
        TableColumn<Hfs0RowModel, Boolean> filePaddingColumn = new TableColumn<>("Padding");
        TableColumn<Hfs0RowModel, String> fileSHA256HashColumn = new TableColumn<>("SHA-256 Hash");
        TableColumn<Hfs0RowModel, Boolean> uploadColumn = new TableColumn<>(resourceBundle.getString("tableUploadLbl"));

        numberColumn.setEditable(false);
        fileNameColumn.setEditable(false);
        fileOffsetColumn.setEditable(false);
        fileSizeColumn.setEditable(false);
        fileHashedRegionSizeColumn.setEditable(false);
        uploadColumn.setEditable(true);

        // See https://bugs.openjdk.java.net/browse/JDK-8157687
        numberColumn.setMinWidth(50.0);
        numberColumn.setPrefWidth(50.0);
        numberColumn.setMaxWidth(50.0);
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

        fileHashedRegionSizeColumn.setMinWidth(120.0);
        fileHashedRegionSizeColumn.setPrefWidth(120.0);
        fileHashedRegionSizeColumn.setMaxWidth(120.0);
        fileHashedRegionSizeColumn.setResizable(false);

        filePaddingColumn.setMinWidth(80.0);
        filePaddingColumn.setPrefWidth(80.0);
        filePaddingColumn.setMaxWidth(80.0);
        filePaddingColumn.setResizable(false);

        fileSHA256HashColumn.setMinWidth(600.0);
        fileSHA256HashColumn.setPrefWidth(600.0);
        fileSHA256HashColumn.setMaxWidth(600.0);
        fileSHA256HashColumn.setResizable(false);

        uploadColumn.setMinWidth(120.0);
        uploadColumn.setPrefWidth(120.0);
        uploadColumn.setMaxWidth(120.0);
        uploadColumn.setResizable(false);

        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        fileOffsetColumn.setCellValueFactory(new PropertyValueFactory<>("fileOffset"));
        filePaddingColumn.setCellValueFactory(new PropertyValueFactory<>("padding"));
        fileSHA256HashColumn.setCellValueFactory(new PropertyValueFactory<>("SHA256Hash"));
        fileHashedRegionSizeColumn.setCellValueFactory(new PropertyValueFactory<>("hashedRegionSize"));
        // ><
        uploadColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Hfs0RowModel, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Hfs0RowModel, Boolean> paramFeatures) {
                Hfs0RowModel model = paramFeatures.getValue();

                SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(model.isMarkSelected());

                booleanProperty.addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                        model.setMarkSelected(newValue);
                        table.refresh();
                    }
                });
                return booleanProperty;
            }
        });

        uploadColumn.setCellFactory(new Callback<TableColumn<Hfs0RowModel, Boolean>, TableCell<Hfs0RowModel, Boolean>>() {
            @Override
            public TableCell<Hfs0RowModel, Boolean> call(TableColumn<Hfs0RowModel, Boolean> paramFeatures) {
                CheckBoxTableCell<Hfs0RowModel, Boolean> cell = new CheckBoxTableCell<>();
                return cell;
            }
        });
        table.setRowFactory(        // this shit is made to implement context menu. It's such a pain..
                new Callback<TableView<Hfs0RowModel>, TableRow<Hfs0RowModel>>() {
                    @Override
                    public TableRow<Hfs0RowModel> call(TableView<Hfs0RowModel> nslHfs0RowModelTableView) {
                        final TableRow<Hfs0RowModel> row = new TableRow<>();
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
                        row.setOnMouseClicked(new EventHandler<MouseEvent>() {      // Just.. don't ask..
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                if (!row.isEmpty() && mouseEvent.getButton() == MouseButton.PRIMARY){
                                    Hfs0RowModel thisItem = row.getItem();
                                    thisItem.setMarkSelected(!thisItem.isMarkSelected());
                                    table.refresh();
                                }
                                mouseEvent.consume();
                            }
                        });
                        return row;
                    }
                }
        );
        table.setItems(rowsObsLst);
        table.getColumns().addAll(numberColumn, fileNameColumn, fileOffsetColumn, fileSizeColumn, fileHashedRegionSizeColumn, filePaddingColumn, fileSHA256HashColumn, uploadColumn);
    }
    /**
     * Add files when user selected them
     * */
    public void setContentToTable(HFS0Provider hfs0){
        this.provider = hfs0;
        rowsObsLst.clear();
        Hfs0RowModel.resetNumCnt();
        if (hfs0 == null) {
            table.refresh();
            return;
        }
        // Note: 'i' in here is extra important to be stored in sequence items added.
        for (int i = 0; i < hfs0.getFilesCnt(); i++){
            rowsObsLst.add(new Hfs0RowModel(
                    hfs0.getHfs0Files()[i].getName(),
                    hfs0.getHfs0Files()[i].getSize(),
                    hfs0.getHfs0Files()[i].getOffset(),
                    hfs0.getHfs0Files()[i].getHashedRegionSize(),
                    hfs0.getHfs0Files()[i].isPadding(),
                    hfs0.getHfs0Files()[i].getSHA256Hash()
            ));
        }

        table.refresh();
    }
    /**
     * Return list of models selected. Requested from NSLMainController only -> uploadBtnAction()                            //TODO: set undefined
     * @return null if no files marked for upload
     *         List<File> if there are files
     * */
    public List<IRowModel> getFilesForDump(){
        List<IRowModel> models = new ArrayList<>();
        if (rowsObsLst.isEmpty())
            return null;
        for (Hfs0RowModel model: rowsObsLst) {
            if (model.isMarkSelected()) {
                models.add(model);
            }
        }
        return models;
    }

    public ISuperProvider getProvider(){ return provider; }
}