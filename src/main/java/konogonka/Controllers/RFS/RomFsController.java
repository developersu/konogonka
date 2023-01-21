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

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import konogonka.AppPreferences;
import konogonka.Controllers.ITabController;
import konogonka.MediatorControl;
import libKonogonka.fs.ISuperProvider;
import libKonogonka.fs.RomFs.FileSystemEntry;
import libKonogonka.fs.RomFs.Level6Header;
import konogonka.Workers.Analyzer;
import konogonka.Workers.DumbRomFsExtractor;
import libKonogonka.fs.RomFs.RomFsProvider;

import java.io.File;
import java.net.URL;
import java.util.List;
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
    private TreeView<RFSModelEntry> filesTreeView;

    private RomFsProvider provider;

    @FXML
    private RFSFolderTableViewController RFSTableViewController;

    @FXML
    private Button extractRootBtn, extractBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filesTreeView.setOnMouseClicked(mouseEvent -> {
            TreeItem<RFSModelEntry> item = filesTreeView.getSelectionModel().getSelectedItem();
            if (item != null && item.getValue().isDirectory())
                RFSTableViewController.setContent(item);
            mouseEvent.consume();
        });

        extractRootBtn.setOnAction(event -> extractRootBtn());
        extractBtn.setOnAction(event -> extractSelectedBtn());
    }
    private void extractRootBtn(){
        File dir = new File(AppPreferences.getInstance().getExtractFilesDir()+File.separator+provider.toString()+" extracted");
        try {
            dir.mkdir();
        }
        catch (SecurityException se){
            MediatorControl.getInstance().getContoller().logArea.setText("Can't create dir to store files.");
        }
        if (!dir.exists())
            return;

        extractBtn.setDisable(true);
        extractRootBtn.setDisable(true);

        DumbRomFsExtractor extractor = new DumbRomFsExtractor(provider, provider.getRootEntry(), dir.getAbsolutePath()+File.separator);
        extractor.setOnSucceeded(e->{
            extractBtn.setDisable(false);
            extractRootBtn.setDisable(false);
        });
        Thread workThread = new Thread(extractor);
        workThread.setDaemon(true);
        workThread.start();
    }
    private void extractSelectedBtn(){
        List<FileSystemEntry> fsEntries = RFSTableViewController.getFilesForDump();

        if (fsEntries == null || fsEntries.isEmpty() || provider == null)
            return;

        File dir = new File(AppPreferences.getInstance().getExtractFilesDir()+File.separator+ provider.getFile().getName()+" extracted");
        try {
            dir.mkdir();
        }
        catch (SecurityException se){
            MediatorControl.getInstance().getContoller().logArea.setText("Can't create dir to store files.");
        }
        if (!dir.exists())
            return;

        extractBtn.setDisable(true);
        extractRootBtn.setDisable(true);

        DumbRomFsExtractor extractor = new DumbRomFsExtractor(provider, fsEntries, dir.getAbsolutePath()+File.separator);
        extractor.setOnSucceeded(e->{
            extractBtn.setDisable(false);
            extractRootBtn.setDisable(false);
        });
        Thread workThread = new Thread(extractor);
        workThread.setDaemon(true);
        workThread.start();

    }

    @Override
    public void analyze(File file, long offset) {
        // TODO: IMPLEMENT?
        System.out.print("NOT IMPLEMENTED: RomFsController -> analyze(File selectedFile, long offset)");
    }

    @Override
    public void analyze(File file) {
        long lv6offset = -1;
        try{
            System.out.println("lv6offset expected: "+file.getName().replaceAll("(^.*lv6\\s)|(]\\.bin)", ""));
            lv6offset = Long.parseLong(file.getName().replaceAll("(^.*lv6\\s)|(]\\.bin)", ""));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Task<RomFsProvider> analyzer = Analyzer.analyzeRomFS(file, lv6offset);
        analyzer.setOnSucceeded(e->{
            RomFsProvider provider = analyzer.getValue();
            this.setData(provider);
        });
        Thread workThread = new Thread(analyzer);
        workThread.start();
    }

    public void setData(RomFsProvider provider) {
        try {
            this.provider = provider;
            Level6Header header = provider.getHeader();
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

            TreeItem<RFSModelEntry> rootItem = getTreeFolderItem(provider.getRootEntry());

            filesTreeView.setRoot(rootItem);

            RFSTableViewController.setContent(rootItem);

            extractBtn.setDisable(false);
            extractRootBtn.setDisable(false);
        }
        catch (Exception e){    // TODO: FIX?
            e.printStackTrace();
        }
    }

    private TreeItem<RFSModelEntry> getTreeFolderItem(FileSystemEntry childEntry){
        TreeItem<RFSModelEntry> entryTreeItem = new TreeItem<>(new RFSModelEntry(childEntry), getFolderImage());
        for (FileSystemEntry entry : childEntry.getContent()){
            if (entry.isDirectory())
                entryTreeItem.getChildren().add(getTreeFolderItem(entry));
            else
                entryTreeItem.getChildren().add( getTreeFileItem(entry) );;
        }
        entryTreeItem.setExpanded(true);

        return entryTreeItem;
    }
    private TreeItem<RFSModelEntry> getTreeFileItem(FileSystemEntry childEntry) {
        return new TreeItem<>(new RFSModelEntry(childEntry), getFileImage());
    }

    @Override
    public void analyze(ISuperProvider parentProvider, int fileNo) throws Exception {
        throw new Exception("NOT SUPPORTED FOR 'RomFS' Controller: analyze(ISuperProvider parentProvider, int fileNo)");
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
        RFSTableViewController.reset();
        extractBtn.setDisable(true);
        extractRootBtn.setDisable(true);
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
