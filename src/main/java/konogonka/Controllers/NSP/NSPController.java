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
package konogonka.Controllers.NSP;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import konogonka.AppPreferences;
import konogonka.Controllers.IRowModel;
import konogonka.Controllers.ITabController;
import konogonka.MediatorControl;
import libKonogonka.fs.ISuperProvider;
import libKonogonka.fs.PFS0.PFS0Provider;
import konogonka.Workers.Analyzer;
import konogonka.Workers.Extractor;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static libKonogonka.Converter.byteArrToHexStringAsLE;

public class NSPController implements ITabController {

    @FXML
    private Button extractBtn;
    @FXML
    private Pfs0TableViewController tableFilesListController;
    @FXML
    private Label filesCountLbl,
            RawDataStartLbl,
            NSPSizeLbl,
            magicLbl,
            stringTableSizeLbl,
            paddingLbl,
            fileEntryTableSizeLbl,
            stringsTableSizeLbl,
            stringsTableOffsetLbl,
            rawFileDataOffsetLbl;

    private long rawFileDataStart;

    private File selectedFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        extractBtn.setOnAction(e->this.extractFiles());
    }

    private void extractFiles(){
        List<IRowModel> models = tableFilesListController.getFilesForDump();
        ISuperProvider provider = tableFilesListController.getProvider();
        if (models != null && !models.isEmpty() && (provider != null)){
            File dir = new File(AppPreferences.getInstance().getExtractFilesDir()+File.separator+provider+" extracted");
            try {
                dir.mkdir();
            }
            catch (SecurityException se){
                MediatorControl.getInstance().getContoller().logArea.setText("Can't create dir to store files.");
            }
            if (!dir.exists())
                return;

            extractBtn.setDisable(true);

            Extractor extractor = new Extractor(provider, models, dir.getAbsolutePath()+File.separator);
            extractor.setOnSucceeded(e->{
                extractBtn.setDisable(false);
            });
            Thread workThread = new Thread(extractor);
            workThread.setDaemon(true);
            workThread.start();
        }
    }
    @Override
    public void resetTab(){
        tableFilesListController.setNSPToTable(null);
        filesCountLbl.setText("-");
        RawDataStartLbl.setText("-");
        NSPSizeLbl.setText("-");
        magicLbl.setText("-");
        stringTableSizeLbl.setText("-");
        paddingLbl.setText("-");
        fileEntryTableSizeLbl.setText("X");
        stringsTableSizeLbl.setText("Y");
        stringsTableOffsetLbl.setText("0x10+X");
        rawFileDataOffsetLbl.setText("0x10+X+Y");
        selectedFile = null;
    }
    /**
     * Start analyze NSP
     * */
    @Override
    public void analyze(File selectedFile, long offset){
        // TODO: IMPLEMENT??
        System.out.print("NOT IMPLEMENTED: NSPController -> analyze(File selectedFile, long offset)");
    }
    @Override
    public void analyze(File selectedFile){
        Task<PFS0Provider> analyzer = Analyzer.analyzePFS0(selectedFile);
        analyzer.setOnSucceeded(e->{
            PFS0Provider pfs0 = analyzer.getValue();
            this.setData(pfs0, selectedFile);
        });
        Thread workThread = new Thread(analyzer);
        workThread.setDaemon(true);
        workThread.start();
    }
    @Override
    public void analyze(ISuperProvider parentProvider, int fileNo) throws Exception {
        throw new Exception("Not supported for NSP");
    }
    /**
     * Just populate fields by already analyzed PFS0
     * */
    public void setData(PFS0Provider pfs0, File fileWithNca){
        if (pfs0 == null)
            return;
        this.selectedFile = fileWithNca;

        filesCountLbl.setText(Integer.toString(pfs0.getHeader().getFilesCount()));
        RawDataStartLbl.setText(Long.toString(pfs0.getRawFileDataStart()));
        rawFileDataStart = pfs0.getRawFileDataStart();
        tableFilesListController.setNSPToTable(pfs0);
        if (fileWithNca == null)
            NSPSizeLbl.setText("skipping calculation for in-file PFS0");
        else
            NSPSizeLbl.setText(Long.toString(selectedFile.length()));

        extractBtn.setDisable(false);
        magicLbl.setText(pfs0.getHeader().getMagic());
        stringTableSizeLbl.setText(Integer.toString(pfs0.getHeader().getStringTableSize()));
        paddingLbl.setText(byteArrToHexStringAsLE(pfs0.getHeader().getPadding()));

        fileEntryTableSizeLbl.setText(String.format("0x%02x", 0x18* pfs0.getHeader().getFilesCount()));
        stringsTableSizeLbl.setText(String.format("0x%02x", pfs0.getHeader().getStringTableSize()));
        stringsTableOffsetLbl.setText(String.format("0x%02x", 0x18* pfs0.getHeader().getFilesCount()+0x10));
        rawFileDataOffsetLbl.setText(String.format("0x%02x", 0x18* pfs0.getHeader().getFilesCount()+0x10+
                pfs0.getHeader().getStringTableSize()));  // same to RawFileDataStart for NSP ONLY
    }
}
