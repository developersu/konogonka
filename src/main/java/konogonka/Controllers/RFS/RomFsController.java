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

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import konogonka.Controllers.ITabController;
import konogonka.Tools.ISuperProvider;
import konogonka.Tools.RomFs.FileSystemEntry;
import konogonka.Tools.RomFs.Level6Header;
import konogonka.Tools.RomFs.RomFsDecryptedProvider;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class RomFsController implements ITabController {

    @FXML
    private Label headerHeaderLengthLbl,
        headerDirectoryHashTableOffsetLbl,
        headerDirectoryHashTableLengthLbl,
        headerDirectoryMetadataTableOffsetLbl,
        headerDirectoryMetadataTableLengthLbl,
        headerFileHashTableOffsetLbl,
        headerFileHashTableLengthLbl,
        headerFileMetadataTableOffsetLbl,
        headerFileMetadataTableLengthLbl,
        headerFileDataOffsetLbl;
    @FXML
    private Label headerHeaderLengthHexLbl,
            headerDirectoryHashTableOffsetHexLbl,
            headerDirectoryHashTableLengthHexLbl,
            headerDirectoryMetadataTableOffsetHexLbl,
            headerDirectoryMetadataTableLengthHexLbl,
            headerFileHashTableOffsetHexLbl,
            headerFileHashTableLengthHexLbl,
            headerFileMetadataTableOffsetHexLbl,
            headerFileMetadataTableLengthHexLbl,
            headerFileDataOffsetHexLbl;

    @FXML
    private TreeView<RFSEntry> filesTreeView;

    private RomFsDecryptedProvider RomFsProvider;

    @FXML
    private RFSFolderTableViewController RFSTableViewController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filesTreeView.setOnMouseClicked(mouseEvent -> {
            TreeItem<RFSEntry> item = filesTreeView.getSelectionModel().getSelectedItem();
            if (item != null && item.getValue().isDirectory())
                RFSTableViewController.setContent(item);
            mouseEvent.consume();
        });
    }

    private final class RFSTreeCell extends TreeCell<RFSEntry>{

    }

    @Override
    public void analyze(File file) {
        this.analyze(file, 0);
    }

    @Override
    public void analyze(File file, long offset) {
        try {
            this.RomFsProvider = new RomFsDecryptedProvider(file);
            Level6Header header = RomFsProvider.getHeader();
            long tempValue;
            tempValue = header.getHeaderLength();
            headerHeaderLengthLbl.setText(Long.toString(tempValue));
            headerHeaderLengthHexLbl.setText(getHexString(tempValue));
            tempValue = header.getDirectoryHashTableOffset();
            headerDirectoryHashTableOffsetLbl.setText(Long.toString(tempValue));
            headerDirectoryHashTableOffsetHexLbl.setText(getHexString(tempValue));
            tempValue = header.getDirectoryHashTableLength();
            headerDirectoryHashTableLengthLbl.setText(Long.toString(tempValue));
            headerDirectoryHashTableLengthHexLbl.setText(getHexString(tempValue));
            tempValue = header.getDirectoryMetadataTableOffset();
            headerDirectoryMetadataTableOffsetLbl.setText(Long.toString(tempValue));
            headerDirectoryMetadataTableOffsetHexLbl.setText(getHexString(tempValue));
            tempValue = header.getDirectoryMetadataTableLength();
            headerDirectoryMetadataTableLengthLbl.setText(Long.toString(tempValue));
            headerDirectoryMetadataTableLengthHexLbl.setText(getHexString(tempValue));
            tempValue = header.getFileHashTableOffset();
            headerFileHashTableOffsetLbl.setText(Long.toString(tempValue));
            headerFileHashTableOffsetHexLbl.setText(getHexString(tempValue));
            tempValue = header.getFileHashTableLength();
            headerFileHashTableLengthLbl.setText(Long.toString(tempValue));
            headerFileHashTableLengthHexLbl.setText(getHexString(tempValue));
            tempValue = header.getFileMetadataTableOffset();
            headerFileMetadataTableOffsetLbl.setText(Long.toString(tempValue));
            headerFileMetadataTableOffsetHexLbl.setText(getHexString(tempValue));
            tempValue = header.getFileMetadataTableLength();
            headerFileMetadataTableLengthLbl.setText(Long.toString(tempValue));
            headerFileMetadataTableLengthHexLbl.setText(getHexString(tempValue));
            tempValue = header.getFileDataOffset();
            headerFileDataOffsetLbl.setText(Long.toString(tempValue));
            headerFileDataOffsetHexLbl.setText(getHexString(tempValue));

            TreeItem<RFSEntry> rootItem = getTreeFolderItem(RomFsProvider.getRootEntry());

            filesTreeView.setRoot(rootItem);

            RFSTableViewController.setContent(rootItem);
        }
        catch (Exception e){    // TODO: FIX
            e.printStackTrace();
        }
    }

    private TreeItem<RFSEntry> getTreeFolderItem(FileSystemEntry childEntry){
        TreeItem<RFSEntry> entryTreeItem = new TreeItem<>(new RFSEntry(childEntry), getFolderImage());
        for (FileSystemEntry entry : childEntry.getContent()){
            if (entry.isDirectory()) {
                entryTreeItem.getChildren().add(getTreeFolderItem(entry));
            }
            else
                entryTreeItem.getChildren().add( getTreeFileItem(entry) );;
        }
        entryTreeItem.setExpanded(true);

        return entryTreeItem;
    }
    private TreeItem<RFSEntry> getTreeFileItem(FileSystemEntry childEntry) {
        return new TreeItem<>(new RFSEntry(childEntry), getFileImage());
    }

    @Override
    public void analyze(ISuperProvider parentProvider, int fileNo) throws Exception {
        throw new Exception("NOT IMPLEMENTED: analyze(ISuperProvider parentProvider, int fileNo)");
    }

    @Override
    public void resetTab() {
        headerHeaderLengthLbl.setText("");
        headerDirectoryHashTableOffsetLbl.setText("");
        headerDirectoryHashTableLengthLbl.setText("");
        headerDirectoryMetadataTableOffsetLbl.setText("");
        headerDirectoryMetadataTableLengthLbl.setText("");
        headerFileHashTableOffsetLbl.setText("");
        headerFileHashTableLengthLbl.setText("");
        headerFileMetadataTableOffsetLbl.setText("");
        headerFileMetadataTableLengthLbl.setText("");
        headerFileDataOffsetLbl.setText("");

        headerHeaderLengthHexLbl.setText("");
        headerDirectoryHashTableOffsetHexLbl.setText("");
        headerDirectoryHashTableLengthHexLbl.setText("");
        headerDirectoryMetadataTableOffsetHexLbl.setText("");
        headerDirectoryMetadataTableLengthHexLbl.setText("");
        headerFileHashTableOffsetHexLbl.setText("");
        headerFileHashTableLengthHexLbl.setText("");
        headerFileMetadataTableOffsetHexLbl.setText("");
        headerFileMetadataTableLengthHexLbl.setText("");
        headerFileDataOffsetHexLbl.setText("");

        filesTreeView.setRoot(null);
    }

    private Region getFolderImage(){
        final Region folderImage = new Region();
        folderImage.getStyleClass().add("regionFolder");
        return folderImage;
    }
    private Region getFileImage(){
        final Region folderImage = new Region();
        folderImage.getStyleClass().add("regionFile");
        return folderImage;
    }

    private String getHexString(long value){
        return String.format("0x%x", value);
    }
}
