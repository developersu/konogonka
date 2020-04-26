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
package konogonka.Controllers.TIK;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import konogonka.AppPreferences;
import konogonka.Controllers.ITabController;
import konogonka.Tools.ISuperProvider;
import konogonka.Tools.TIK.TIKProvider;
import konogonka.Workers.Analyzer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static konogonka.LoperConverter.byteArrToHexString;

public class TIKController implements ITabController {
    @FXML
    private Button btnImport;
    @FXML
    private Label sigTypeLbl,
                    sigTypeStrLbl,
                    tikSizeLbl,
                    sSizeLbl,   // cosmetic
                    pOffsetLbl, // cosmetic
                    pSizeLbl,   // cosmetic

                    unknown1Lbl,
                    titleKeyTypeLbl,
                    unknown2Lbl,
                    masterKeyRevisionLbl,
                    unknown3Lbl,
                    ticketIdLbl,
                    deviceIdLbl,
                    accountIdLbl,
                    unknown4Lbl;
    @FXML
    private TextField signatureTF,
                    issuerTf,
                    titleKeyBlockStartTf,
                    titleKeyBlockEndTf,
                    rightsIdTf;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnImport.setOnAction(e -> {
            String key = rightsIdTf.getText();
            String value = titleKeyBlockStartTf.getText();
            int titleKeysCnt = AppPreferences.getInstance().getTitleKeysCount();
            if (key.length() > 16 && ! (key.length() > 32) && value.length() == 32){
                for (int i = 0; i < titleKeysCnt; i++){
                    if (AppPreferences.getInstance().getTitleKeyPair(i)[0].equals(key))
                        return;
                }
                AppPreferences.getInstance().setTitleKey(titleKeysCnt, key+" = "+value);
                AppPreferences.getInstance().setTitleKeysCount(titleKeysCnt+1);
            }
        });
    }

    @Override
    public void analyze(File file) { analyze(file, 0); }
    @Override
    public void analyze(ISuperProvider parentProvider, int fileNo) throws Exception {
        throw new Exception("Not supported for TIK");
    }
    @Override
    public void analyze(File file, long offset) {
        Task<TIKProvider> analyzer = Analyzer.analyzeTIK(file, offset);
        analyzer.setOnSucceeded(e->{
            TIKProvider tik = analyzer.getValue();
            if (offset == 0)
                setData(tik, file);
            else
                setData(tik, null);
        });
        Thread workThread = new Thread(analyzer);
        workThread.setDaemon(true);
        workThread.start();
    }

    private void setData(TIKProvider tikProvider, File file){
        if (tikProvider == null)
            return;

        if (file != null)
            tikSizeLbl.setText(Long.toString(file.length()));
        else
            tikSizeLbl.setText("skipping calculation for in-file ticket");

        sigTypeLbl.setText(byteArrToHexString(tikProvider.getSigType()));
        switch (sigTypeLbl.getText()){
            case "00000100":
                sigTypeStrLbl.setText("RSA_4096 SHA1");
                sSizeLbl.setText("0x200");
                pOffsetLbl.setText("0x240");
                pSizeLbl.setText("0x3c");
                break;
            case "01000100":
                sigTypeStrLbl.setText("RSA_2048 SHA1");
                sSizeLbl.setText("0x100");
                pOffsetLbl.setText("0x104");
                pSizeLbl.setText("0x3c");
                break;
            case "02000100":
                sigTypeStrLbl.setText("ECDSA SHA1");
                sSizeLbl.setText("0x3c");
                pOffsetLbl.setText("0x40");
                pSizeLbl.setText("0x40");
                break;
            case "03000100":
                sigTypeStrLbl.setText("RSA_4096 SHA256");
                sSizeLbl.setText("0x200");
                pOffsetLbl.setText("0x240");
                pSizeLbl.setText("0x3c");
                break;
            case "04000100":
                sigTypeStrLbl.setText("RSA_2048 SHA256");
                sSizeLbl.setText("0x100");
                pOffsetLbl.setText("0x104");
                pSizeLbl.setText("0x3c");
                break;
            case "05000100":
                sigTypeStrLbl.setText("ECDSA SHA256");
                sSizeLbl.setText("0x3c");
                pOffsetLbl.setText("0x40");
                pSizeLbl.setText("0x40");
                break;
            default:
                sigTypeStrLbl.setText("???");
                sSizeLbl.setText("???");
                pOffsetLbl.setText("???");
                pSizeLbl.setText("???");
                break;
        }
        signatureTF.setText(byteArrToHexString(tikProvider.getSignature()));

        issuerTf.setText(tikProvider.getIssuer());
        titleKeyBlockStartTf.setText(byteArrToHexString(tikProvider.getTitleKeyBlockStartingBytes()));
        titleKeyBlockEndTf.setText(byteArrToHexString(tikProvider.getTitleKeyBlockEndingBytes()));
        unknown1Lbl.setText(String.format("0x%02x", tikProvider.getUnknown1()));
        titleKeyTypeLbl.setText(String.format("0x%02x", tikProvider.getTitleKeyType()));
        unknown2Lbl.setText(byteArrToHexString(tikProvider.getUnknown2()));
        masterKeyRevisionLbl.setText(String.format("0x%02x", tikProvider.getMasterKeyRevision()));
        unknown3Lbl.setText(byteArrToHexString(tikProvider.getUnknown3()));
        ticketIdLbl.setText(byteArrToHexString(tikProvider.getTicketId()));
        deviceIdLbl.setText(byteArrToHexString(tikProvider.getDeviceId()));
        rightsIdTf.setText(byteArrToHexString(tikProvider.getRightsId()));
        accountIdLbl.setText(byteArrToHexString(tikProvider.getAccountId()));
        unknown4Lbl.setText(byteArrToHexString(tikProvider.getUnknown4()));
        btnImport.setDisable(false);
    }
    @Override
    public void resetTab() {
        btnImport.setDisable(true);
        sigTypeLbl.setText("-");
        sigTypeStrLbl.setText("-");
        tikSizeLbl.setText("-");
        sSizeLbl.setText("...");
        pOffsetLbl.setText("...");
        pSizeLbl.setText("...");
        signatureTF.setText("-");

        issuerTf.setText("-");
        titleKeyBlockStartTf.setText("-");
        titleKeyBlockEndTf.setText("-");
        unknown1Lbl.setText("-");
        titleKeyTypeLbl.setText("-");
        unknown2Lbl.setText("-");
        masterKeyRevisionLbl.setText("-");
        unknown3Lbl.setText("-");
        ticketIdLbl.setText("-");
        deviceIdLbl.setText("-");
        rightsIdTf.setText("-");
        accountIdLbl.setText("-");
        unknown4Lbl.setText("-");
    }
}
