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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import konogonka.Controllers.ITabController;
import konogonka.Tools.ISuperProvider;
import konogonka.Tools.RomFs.RomFsDecryptedProvider;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class RomFsController implements ITabController {

    @FXML
    private TreeView<RFSFolderEntry> filesTreeView;
    @FXML
    private VBox folderContentVBox;

    private RomFsDecryptedProvider RomFsProvider;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TreeItem<RFSFolderEntry> rootTest = getEmptyRoot();
        TreeItem<RFSFolderEntry> test = new TreeItem<>(new RFSFolderEntry("WIP"), getFolderImage());

        rootTest.getChildren().add(test);

        filesTreeView.setRoot(rootTest);
    }

    @Override
    public void analyze(File file) {
        this.analyze(file, 0);
    }

    @Override
    public void analyze(File file, long offset) {
        try {
            this.RomFsProvider = new RomFsDecryptedProvider(file);
        }
        catch (Exception e){    // TODO: FIX
            e.printStackTrace();
        }
        TreeItem<RFSFolderEntry> rootItem = getEmptyRoot();

        filesTreeView.setRoot(rootItem);
    }

    @Override
    public void analyze(ISuperProvider parentProvider, int fileNo) throws Exception {

    }

    @Override
    public void resetTab() {
        filesTreeView.setRoot(null);
    }

    private Region getFolderImage(){
        final Region folderImage = new Region();
        folderImage.getStyleClass().add("regionFolder");
        return folderImage;
    }

    private TreeItem<RFSFolderEntry> getEmptyRoot(){
        return new TreeItem<>(new RFSFolderEntry("/"));
    }
}
