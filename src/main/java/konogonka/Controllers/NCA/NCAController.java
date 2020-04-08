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
package konogonka.Controllers.NCA;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import konogonka.AppPreferences;
import konogonka.Controllers.ITabController;
import konogonka.Tools.ISuperProvider;
import konogonka.Tools.NCA.NCAContentPFS0;
import konogonka.Tools.NCA.NCAProvider;
import konogonka.Workers.Analyzer;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import static konogonka.LoperConverter.byteArrToHexString;

public class NCAController implements ITabController {

    private File selectedFile;
    @FXML
    private NCASectionHeaderBlockController
            NCASectionHeaderFirstController,
            NCASectionHeaderSecondController,
            NCASectionHeaderThirdController,
            NCASectionHeaderFourthController;
    @FXML
    private NCASectionContentController
            NCASectionContentFirstController,
            NCASectionContentSecondController,
            NCASectionContentThirdController,
            NCASectionContentFourthController;
    @FXML
    private NCATableController
            NCATable1Controller,
            NCATable2Controller,
            NCATable3Controller,
            NCATable4Controller;
    @FXML
    private Label
            magicnumLbl,
            systemOrGcIndLbl,
            contentTypeLbl,
            cryptoType1Lbl,
            keyIndexLbl,
            ncaSizeLbl,
            titleIdLbl,
            contentIndexLbl,
            sdkVersionLbl,
            cryptoType2Lbl,
            header1SignatureKeyGenerationLbl,
            keyGenerationReservedLbl,
            ticketLbl;
    @FXML
    private TextField
            rsa2048oneTF,
            rsa2048twoTF,
            sha256section1TF,
            sha256section2TF,
            sha256section3TF,
            sha256section4TF,
            keyAreaEnKey0TF,
            keyAreaEnKey1TF,
            keyAreaEnKey2TF,
            keyAreaEnKey3TF,
            keyAreaDecKey0TF,
            keyAreaDecKey1TF,
            keyAreaDecKey2TF,
            keyAreaDecKey3TF;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void analyze(File file, long offset) {
        this.selectedFile = file;
        HashMap<String, String> keysMap = new HashMap<>();
        keysMap.put("header_key", AppPreferences.getInstance().getHeaderKey());

        for (int i = 0; i < AppPreferences.getInstance().getKAKAppCount(); i++)
            keysMap.put(String.format("key_area_key_application_%02x", i), AppPreferences.getInstance().getApplicationKey(i));
        for (int i = 0; i < AppPreferences.getInstance().getKAKOceanCount(); i++)
            keysMap.put(String.format("key_area_key_ocean_%02x", i), AppPreferences.getInstance().getOceanKey(i));
        for (int i = 0; i < AppPreferences.getInstance().getKAKSysCount(); i++)
            keysMap.put(String.format("key_area_key_system_%02x", i), AppPreferences.getInstance().getSystemKey(i));
        for (int i = 0; i < AppPreferences.getInstance().getTitleKeksCount(); i++)
            keysMap.put(String.format("titlekek_%02x", i), AppPreferences.getInstance().getTitleKek(i));
        for (int i = 0; i < AppPreferences.getInstance().getTitleKeysCount(); i++){
            String[] pair = AppPreferences.getInstance().getTitleKeyPair(i);
            if ( ! pair[0].equals("0") && ! pair[1].equals("0"))
                keysMap.put(pair[0], pair[1]);
        }

        Task<NCAProvider> analyzer = Analyzer.analyzeNCA(file, keysMap, offset);
        analyzer.setOnSucceeded(e->{
            populateFields(analyzer.getValue());
        });
        Thread workThread = new Thread(analyzer);
        workThread.setDaemon(true);
        workThread.start();
    }

    @Override
    public void analyze(File file) {
        analyze(file, 0);
    }
    @Override
    public void analyze(ISuperProvider parentProvider, int fileNo) throws Exception {
        throw new Exception("Not supported for NCA");
    }
    @Override
    public void resetTab() {
        // Header
        rsa2048oneTF.setText("-");
        rsa2048twoTF.setText("-");
        magicnumLbl.setText("-");
        systemOrGcIndLbl.setText("-");
        contentTypeLbl.setText("-");
        cryptoType1Lbl.setText("-");
        keyIndexLbl.setText("-");
        ncaSizeLbl.setText("-");
        titleIdLbl.setText("-");
        sdkVersionLbl.setText("-");
        cryptoType2Lbl.setText("-");
        header1SignatureKeyGenerationLbl.setText("-");
        keyGenerationReservedLbl.setText("-");
        ticketLbl.setText("-");
        contentIndexLbl.setText("-");
        sha256section1TF.setText("-");
        sha256section2TF.setText("-");
        sha256section3TF.setText("-");
        sha256section4TF.setText("-");
        keyAreaEnKey0TF.setText("-");
        keyAreaEnKey1TF.setText("-");
        keyAreaEnKey2TF.setText("-");
        keyAreaEnKey3TF.setText("-");

        keyAreaDecKey0TF.setText("-");
        keyAreaDecKey1TF.setText("-");
        keyAreaDecKey2TF.setText("-");
        keyAreaDecKey3TF.setText("-");
        // Tables
        NCATable1Controller.resetTab();
        NCATable2Controller.resetTab();
        NCATable3Controller.resetTab();
        NCATable4Controller.resetTab();
        // Table blocks
        NCASectionHeaderFirstController.resetTab();
        NCASectionHeaderSecondController.resetTab();
        NCASectionHeaderThirdController.resetTab();
        NCASectionHeaderFourthController.resetTab();
        // Section content blocks
        NCASectionContentFirstController.resetTab();
        NCASectionContentSecondController.resetTab();
        NCASectionContentThirdController.resetTab();
        NCASectionContentFourthController.resetTab();
    }

    private void populateFields(NCAProvider ncaProvider){
        if (ncaProvider == null)
            return;
        rsa2048oneTF.setText(byteArrToHexString(ncaProvider.getRsa2048one()));
        rsa2048twoTF.setText(byteArrToHexString(ncaProvider.getRsa2048two()));
        magicnumLbl.setText(ncaProvider.getMagicnum());
        systemOrGcIndLbl.setText(Byte.toString(ncaProvider.getSystemOrGcIndicator()));
        contentTypeLbl.setText(Byte.toString(ncaProvider.getContentType()));
        cryptoType1Lbl.setText(Byte.toString(ncaProvider.getCryptoType1()));
        keyIndexLbl.setText(Byte.toString(ncaProvider.getKeyIndex()));
        ncaSizeLbl.setText(Long.toString(ncaProvider.getNcaSize()));
        titleIdLbl.setText(byteArrToHexString(ncaProvider.getTitleId()));
        contentIndexLbl.setText(byteArrToHexString(ncaProvider.getContentIndx()));   //
        sdkVersionLbl.setText(ncaProvider.getSdkVersion()[3]
                +"."+ncaProvider.getSdkVersion()[2]
                +"."+ncaProvider.getSdkVersion()[1]
                +"."+ncaProvider.getSdkVersion()[0]);
        cryptoType2Lbl.setText(Byte.toString(ncaProvider.getCryptoType2()));
        header1SignatureKeyGenerationLbl.setText(Byte.toString(ncaProvider.getHeader1SignatureKeyGeneration()));
        keyGenerationReservedLbl.setText(byteArrToHexString(ncaProvider.getKeyGenerationReserved()));
        ticketLbl.setText(byteArrToHexString(ncaProvider.getRightsId()));
        sha256section1TF.setText(byteArrToHexString(ncaProvider.getSha256hash0()));
        sha256section2TF.setText(byteArrToHexString(ncaProvider.getSha256hash1()));
        sha256section3TF.setText(byteArrToHexString(ncaProvider.getSha256hash2()));
        sha256section4TF.setText(byteArrToHexString(ncaProvider.getSha256hash3()));
        keyAreaEnKey0TF.setText(byteArrToHexString(ncaProvider.getEncryptedKey0()));
        keyAreaEnKey1TF.setText(byteArrToHexString(ncaProvider.getEncryptedKey1()));
        keyAreaEnKey2TF.setText(byteArrToHexString(ncaProvider.getEncryptedKey2()));
        keyAreaEnKey3TF.setText(byteArrToHexString(ncaProvider.getEncryptedKey3()));

        keyAreaDecKey0TF.setText(byteArrToHexString(ncaProvider.getDecryptedKey0()));
        keyAreaDecKey1TF.setText(byteArrToHexString(ncaProvider.getDecryptedKey1()));
        keyAreaDecKey2TF.setText(byteArrToHexString(ncaProvider.getDecryptedKey2()));
        keyAreaDecKey3TF.setText(byteArrToHexString(ncaProvider.getDecryptedKey3()));
        // Tables
        NCATable1Controller.populateTab(ncaProvider.getTableEntry0());
        NCATable2Controller.populateTab(ncaProvider.getTableEntry1());
        NCATable3Controller.populateTab(ncaProvider.getTableEntry2());
        NCATable4Controller.populateTab(ncaProvider.getTableEntry3());
        // Table blocks
        NCASectionHeaderFirstController.populateTab(ncaProvider.getSectionBlock0());
        NCASectionHeaderSecondController.populateTab(ncaProvider.getSectionBlock1());
        NCASectionHeaderThirdController.populateTab(ncaProvider.getSectionBlock2());
        NCASectionHeaderFourthController.populateTab(ncaProvider.getSectionBlock3());
        // Section content blocks
        // TODO: FIX: This code executes getNCAContentPFS0() method twice
        NCAContentPFS0 ncaContentPFS0;
        ncaContentPFS0 = ncaProvider.getNCAContentPFS0(0);
        NCASectionContentFirstController.populateFields(ncaContentPFS0.getPfs0(), ncaContentPFS0.getSHA256hashes());
        ncaContentPFS0 = ncaProvider.getNCAContentPFS0(1);
        NCASectionContentSecondController.populateFields(ncaContentPFS0.getPfs0(), ncaContentPFS0.getSHA256hashes());
        ncaContentPFS0 = ncaProvider.getNCAContentPFS0(2);
        NCASectionContentThirdController.populateFields(ncaContentPFS0.getPfs0(), ncaContentPFS0.getSHA256hashes());
        ncaContentPFS0 = ncaProvider.getNCAContentPFS0(3);
        NCASectionContentFourthController.populateFields(ncaContentPFS0.getPfs0(), ncaContentPFS0.getSHA256hashes());
    }
}
