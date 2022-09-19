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
package konogonka.Controllers.NCA;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import konogonka.AppPreferences;
import konogonka.Controllers.NSP.NSPController;
import konogonka.Controllers.RFS.RomFsController;
import libKonogonka.Converter;
import konogonka.MediatorControl;
import libKonogonka.Tools.NCA.NCAContent;
import konogonka.Workers.DumbNCA3ContentExtractor;
import libKonogonka.Tools.PFS0.IPFS0Provider;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class NCASectionContentController implements Initializable {

    private NCAContent ncaContent;
    private int sectionNumber;

    @FXML
    private Button extractRawContentBtn;
    @FXML
    private NSPController SectionPFS0Controller;
    @FXML
    private RomFsController SectionRomFsController;
    @FXML
    private VBox sha256pane;
    @FXML
    private TitledPane pfs0TitledPane, RomFsTitledPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        extractRawContentBtn.setDisable(true);
        extractRawContentBtn.setOnAction(event -> this.extractFiles());
    }

    public void resetTab() {
        SectionPFS0Controller.resetTab();
        SectionRomFsController.resetTab();
        sha256pane.getChildren().clear();
        extractRawContentBtn.setDisable(true);
    }

    public void populateFields(NCAContent ncaContent, int sectionNumber) {
        resetTab();

        if (ncaContent == null)
            return;

        this.ncaContent = ncaContent;
        this.sectionNumber = sectionNumber;
        this.extractRawContentBtn.setDisable(false);

        setPFS0Content();
        setRomFsContent();
    }
    private void setPFS0Content(){
        if (ncaContent.getPfs0() != null)
            SectionPFS0Controller.setData(ncaContent.getPfs0(), null);;

        IPFS0Provider ipfs0Provider = ncaContent.getPfs0();

        if (ipfs0Provider == null)
            return;

        LinkedList<byte[]> sha256hashList = ipfs0Provider.getPfs0SHA256hashes();

        if (sha256hashList == null)
            return;

        for (int i = 0; i < sha256hashList.size(); i++){
            Label numberLblTmp = new Label(String.format("%10d", i));
            numberLblTmp.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
            Label sha256LblTmp = new Label(Converter.byteArrToHexString(sha256hashList.get(i)));
            sha256LblTmp.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));

            sha256pane.getChildren().add(new HBox(numberLblTmp, sha256LblTmp));
        }
        pfs0TitledPane.setExpanded(true);
    }
    private void setRomFsContent(){
        if (ncaContent.getRomfs() == null)
            return;
        SectionRomFsController.setData(ncaContent.getRomfs());
        RomFsTitledPane.setExpanded(true);
    }

    private void extractFiles(){
        if (ncaContent == null)
            return;

        File dir = new File(AppPreferences.getInstance().getExtractFilesDir()+File.separator+ncaContent.getFileName()+" extracted");
        try {
            dir.mkdir();
        }
        catch (SecurityException se){
            MediatorControl.getInstance().getContoller().logArea.setText("Can't create dir to store files.");
        }
        if (!dir.exists())
            return;

        extractRawContentBtn.setDisable(true);

        DumbNCA3ContentExtractor extractor = new DumbNCA3ContentExtractor(ncaContent, sectionNumber, dir.getAbsolutePath()+File.separator);
        extractor.setOnSucceeded(e->{
            extractRawContentBtn.setDisable(false);
        });
        Thread workThread = new Thread(extractor);
        workThread.setDaemon(true);
        workThread.start();
    }
}
