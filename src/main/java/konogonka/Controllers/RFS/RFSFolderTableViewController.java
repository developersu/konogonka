/*
    Copyright 2019-2022 Dmitry Isaenko

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

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import libKonogonka.fs.RomFs.FileSystemEntry;

import java.net.URL;
import java.util.*;

public class RFSFolderTableViewController implements Initializable {
    @FXML
    private TableView<RFSModelEntry> table;
    private ObservableList<RFSModelEntry> rowsObsLst;
    @FXML
    private HBox navigationHBox;

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
                    for (RFSModelEntry item : table.getSelectionModel().getSelectedItems()) {
                        item.setMarkSelected( ! item.isMarkSelected());
                    }
                    table.refresh();
                }
            }
            keyEvent.consume();
        });

        TableColumn<RFSModelEntry, Node> imageColumn = new TableColumn<>();
        TableColumn<RFSModelEntry, String> fileNameColumn = new TableColumn<>(resourceBundle.getString("tableFileNameLbl"));
        TableColumn<RFSModelEntry, Long> fileOffsetColumn = new TableColumn<>(resourceBundle.getString("tableOffsetLbl"));
        TableColumn<RFSModelEntry, Long> fileSizeColumn = new TableColumn<>(resourceBundle.getString("tableSizeLbl"));
        TableColumn<RFSModelEntry, Boolean> checkBoxColumn = new TableColumn<>(resourceBundle.getString("tableUploadLbl"));

        imageColumn.setEditable(false);
        fileNameColumn.setEditable(false);
        fileOffsetColumn.setEditable(false);
        fileSizeColumn.setEditable(false);
        checkBoxColumn.setEditable(true);

        // See https://bugs.openjdk.java.net/browse/JDK-8157687
        imageColumn.setMinWidth(30.0);
        imageColumn.setPrefWidth(30.0);
        imageColumn.setMaxWidth(30.0);
        imageColumn.setResizable(false);

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

        imageColumn.setCellValueFactory(paramFeatures -> {
            RFSModelEntry model = paramFeatures.getValue();
            return new ObservableValue<Node>() {
                @Override
                public Node getValue() {
                    final Region folderImage = new Region();
                    if (model.isDirectory())
                        folderImage.getStyleClass().add("regionFolder");
                    else
                        folderImage.getStyleClass().add("regionFile");
                    return folderImage;
                }
                @Override
                public void addListener(ChangeListener<? super Node> changeListener) {}
                @Override
                public void removeListener(ChangeListener<? super Node> changeListener) {}
                @Override
                public void addListener(InvalidationListener invalidationListener) {}
                @Override
                public void removeListener(InvalidationListener invalidationListener) {}
            };
        });

        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        fileOffsetColumn.setCellValueFactory(new PropertyValueFactory<>("fileOffset"));
        checkBoxColumn.setCellValueFactory(paramFeatures -> {
            RFSModelEntry model = paramFeatures.getValue();

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
                    final TableRow<RFSModelEntry> row = new TableRow<>();
                    /*
                    ContextMenu contextMenu = new ContextMenu();
                     // TODO: ADD'Export'?
                    MenuItem openMenuItem = new MenuItem("Export");
                    openMenuItem.setOnAction(event -> {
                        RFSEntry entry = row.getItem();
                        System.out.print("Selected: "+entry.getFileName());
                    });

                    contextMenu.getItems().add(openMenuItem);
                    row.setContextMenu(contextMenu);

                    row.contextMenuProperty().bind(
                            Bindings.when(Bindings.isNotNull(row.itemProperty())).then(contextMenu).otherwise((ContextMenu)null)
                    );
                     */
                    row.setOnMouseClicked(mouseEvent -> {
                        if (!row.isEmpty() && mouseEvent.getButton() == MouseButton.PRIMARY){
                            RFSModelEntry thisItem = row.getItem();
                            thisItem.setMarkSelected(!thisItem.isMarkSelected());
                            table.refresh();
                        }

                        mouseEvent.consume();
                    });
                    return row;
                }
        );
        table.setItems(rowsObsLst);
        table.getColumns().add(imageColumn);
        table.getColumns().add(fileNameColumn);
        table.getColumns().add(fileOffsetColumn);
        table.getColumns().add(fileSizeColumn);
        table.getColumns().add(checkBoxColumn);
    }

    /**
     * Add files when user selected them on left-hand tree
     * */
    public void setContent(TreeItem<RFSModelEntry> containerTreeItem){
        reset();

        if (containerTreeItem == null) {
            table.refresh();
            return;
        }

        for (TreeItem<RFSModelEntry> childTreeItem : containerTreeItem.getChildren())
            rowsObsLst.add(childTreeItem.getValue());

        setNavigationContent(containerTreeItem);

        table.refresh();
    }

    private void setNavigationContent(TreeItem<RFSModelEntry> childTreeItem){
        TreeItem<RFSModelEntry> parentTreeItem;

        LinkedList<Button> content = new LinkedList<>();

        content.add(new Button(childTreeItem.getValue().getFileName()));

        while ((parentTreeItem = childTreeItem.getParent()) != null) {
            content.add(createNavigationButton(parentTreeItem));
            childTreeItem = parentTreeItem;
        }

        Collections.reverse(content);
        for (Button button : content)
            navigationHBox.getChildren().add(button);
    }
    private Button createNavigationButton(TreeItem<RFSModelEntry> treeItem){
        Button button = new Button(treeItem.getValue().getFileName());
        button.setOnAction(event -> setContent(treeItem));
        return button;
    }

    public void reset(){
        rowsObsLst.clear();
        navigationHBox.getChildren().clear();
        table.refresh();
    }

    public List<FileSystemEntry> getFilesForDump(){
        if (rowsObsLst.isEmpty())
            return null;

        List<FileSystemEntry> fsEntries = new ArrayList<>();

        for (RFSModelEntry model: rowsObsLst) {
            if (model.isMarkSelected())
                fsEntries.add(model.getFileSystemEntry());
        }
        return fsEntries;
    }
}