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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import konogonka.AppPreferences;
import konogonka.Controllers.IRowModel;
import konogonka.MediatorControl;
import konogonka.Tools.ISuperProvider;
import konogonka.Tools.XCI.HFS0Provider;
import konogonka.Workers.Extractor;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HFSBlockController implements Initializable {
    @FXML
    private TitledPane currentTitledPane;

    @FXML
    private Button extractMainBtn;

    @FXML
    private Label hfs0mainMagicNumLbl,
            hfs0mainFileCntLbl,
            hfs0mainStrTblSizeLbl,
            hfs0mainPaddingLbl,
            hfs0mainRawFileDataStartLbl;

    @FXML
    private Hfs0TableViewController
            hfs0tableFilesListMainController;

    private long bodySize;
    private String
            type,
            paneName;

    private static File selectedFile;

    public static void setSelectedFile(File file){
        selectedFile = file;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        extractMainBtn.setOnAction(e->{
            extractFiles();
        });
    }

    public void setTitlePaneTypeName(String type, String name){
        this.currentTitledPane.setText(name);
        paneName = name;
        this.type = type;
    }

    public void resetTab(){
        currentTitledPane.setExpanded(false);
        hfs0mainMagicNumLbl.setText("-");
        hfs0mainFileCntLbl.setText("-");
        hfs0mainStrTblSizeLbl.setText("-");
        hfs0mainPaddingLbl.setText("-");
        hfs0mainRawFileDataStartLbl.setText("-");
        hfs0tableFilesListMainController.setContentToTable(null);
        extractMainBtn.setDisable(true);
        currentTitledPane.setText(paneName);
    }
    public void populateTab(HFS0Provider hfs0Provider){
        if (hfs0Provider != null){
            bodySize = hfs0Provider.getRawFileDataStart();
            hfs0mainMagicNumLbl.setText(Boolean.toString(hfs0Provider.isMagicHFS0()));
            hfs0mainFileCntLbl.setText(Integer.toString(hfs0Provider.getFilesCnt()));
            hfs0mainStrTblSizeLbl.setText(Integer.toString(hfs0Provider.getStringTableSize()));
            hfs0mainPaddingLbl.setText(Boolean.toString(hfs0Provider.isPaddingHfs0()));
            hfs0mainRawFileDataStartLbl.setText(Long.toString(hfs0Provider.getRawFileDataStart()));
            hfs0tableFilesListMainController.setContentToTable(hfs0Provider);
            if (hfs0Provider.getFilesCnt() > 0)
                extractMainBtn.setDisable(false);
        }
        else {
            currentTitledPane.setText(paneName+" (doesn't exist in this file)");
            currentTitledPane.setExpanded(false);
        }
    }

    private void extractFiles(){
        List<IRowModel> models;

        models = hfs0tableFilesListMainController.getFilesForDump();
        ISuperProvider provider = hfs0tableFilesListMainController.getProvider();

        if (models != null && !models.isEmpty() && (provider != null)){
            File dir = new File(AppPreferences.getInstance().getExtractFilesDir()+File.separator+selectedFile.getName()+" "+type+" extracted");
            try {
                dir.mkdir();
            }
            catch (SecurityException se){
                MediatorControl.getInstance().getContoller().logArea.setText("Can't create dir to store files.");
            }
            if (!dir.exists())
                return;

            extractMainBtn.setDisable(true);
            System.out.println(dir.getAbsolutePath()+File.separator);
            //Extractor extractor = new Extractor(bodySize, models, dir.getAbsolutePath()+File.separator, selectedFile);    // TODO: REMOVE
            Extractor extractor = new Extractor(provider, models, dir.getAbsolutePath()+File.separator);
            extractor.setOnSucceeded(e->{
                extractMainBtn.setDisable(false);
            });
            Thread workThread = new Thread(extractor);
            workThread.setDaemon(true);
            workThread.start();
        }
    }
}
