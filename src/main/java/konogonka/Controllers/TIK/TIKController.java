package konogonka.Controllers.TIK;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import konogonka.Controllers.ITabController;
import konogonka.Tools.TIK.TIKProvider;
import konogonka.Workers.AnalyzerTIK;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static konogonka.LoperConverter.byteArrToHexString;

public class TIKController implements ITabController {

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
                    titleKeyBlockTf,
                    rightsIdTf;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    @Override
    public void analyze(File file) { analyze(file, 0); }

    @Override
    public void analyze(File file, long offset) {
        AnalyzerTIK analyzerTIK = new AnalyzerTIK(file, offset);
        analyzerTIK.setOnSucceeded(e->{
            TIKProvider tik = analyzerTIK.getValue();
            if (offset == 0)
                setData(tik, file);
            else
                setData(tik, null);
        });
        Thread workThread = new Thread(analyzerTIK);
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

        issuerTf.setText(byteArrToHexString(tikProvider.getIssuer()));
        titleKeyBlockTf.setText(byteArrToHexString(tikProvider.getTitleKeyBlock()));
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
    }
    @Override
    public void resetTab() {
        sigTypeLbl.setText("-");
        sigTypeStrLbl.setText("-");
        tikSizeLbl.setText("-");
        sSizeLbl.setText("...");
        pOffsetLbl.setText("...");
        pSizeLbl.setText("...");
        signatureTF.setText("-");

        issuerTf.setText("-");
        titleKeyBlockTf.setText("-");
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
