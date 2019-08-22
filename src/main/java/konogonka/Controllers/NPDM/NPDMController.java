package konogonka.Controllers.NPDM;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import konogonka.Controllers.ITabController;
import konogonka.Tools.NPDM.NPDMProvider;
import konogonka.Workers.AnalyzerNPDM;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static konogonka.LoperConverter.byteArrToHexString;

public class NPDMController implements ITabController {

    @FXML
    private Label magicNumLbl,
            reserved1Lbl,
            MMUFlagsLbl,
            reserved2Lbl,
            mainThreadPrioLbl,
            mainThreadCoreNumLbl,
            reserved3Lbl,
            personalMmHeapSizeLbl,
            versionLbl,
            mainThreadStackSizeLbl,
            aci0offsetLbl,
            aci0sizeLbl,
            acidOffsetLbl,
            acidSizeLbl,
            npdmFileSize;

    @FXML
    private TextField titleNameTf,
                    productCodeTf,
                    reserved4Tf;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    @Override
    public void analyze(File file) { analyze(file, 0); }

    @Override
    public void analyze(File file, long offset) {
        AnalyzerNPDM analyzerNPDM = new AnalyzerNPDM(file, offset);
        analyzerNPDM.setOnSucceeded(e->{
            NPDMProvider tik = analyzerNPDM.getValue();
            if (offset == 0)
                setData(tik, file);
            else
                setData(tik, null);
        });
        Thread workThread = new Thread(analyzerNPDM);
        workThread.setDaemon(true);
        workThread.start();
    }

    @Override
    public void resetTab() {
        magicNumLbl.setText("-");
        reserved1Lbl.setText("-");
        MMUFlagsLbl.setText("-");
        reserved2Lbl.setText("-");
        mainThreadPrioLbl.setText("-");
        mainThreadCoreNumLbl.setText("-");
        reserved3Lbl.setText("-");
        personalMmHeapSizeLbl.setText("-");
        versionLbl.setText("-");
        mainThreadStackSizeLbl.setText("-");
        aci0offsetLbl.setText("-");
        aci0sizeLbl.setText("-");
        acidOffsetLbl.setText("-");
        acidSizeLbl.setText("-");
        titleNameTf.setText("-");
        productCodeTf.setText("-");
        reserved4Tf.setText("-");
        npdmFileSize.setText("-");
    }
    private void setData(NPDMProvider npdmProvider, File file) {
        if (npdmProvider == null)
            return;
        if (file != null)
            npdmFileSize.setText(Long.toString(file.length()));
        else
            npdmFileSize.setText("skipping calculation for in-file ticket");

        magicNumLbl.setText(npdmProvider.getMagicNum());
        reserved1Lbl.setText(byteArrToHexString(npdmProvider.getReserved1()));
        MMUFlagsLbl.setText(npdmProvider.getMMUFlags()+" (0b"+String.format("%8s", Integer.toBinaryString(npdmProvider.getMMUFlags() & 0xFF)).replace(' ', '0')+")");
        reserved2Lbl.setText(String.format("0x%02x", npdmProvider.getReserved2()));
        mainThreadPrioLbl.setText(Byte.toString(npdmProvider.getMainThreadPrio()));
        mainThreadCoreNumLbl.setText(Byte.toString(npdmProvider.getMainThreadCoreNum()));
        reserved3Lbl.setText(byteArrToHexString(npdmProvider.getReserved3()));
        personalMmHeapSizeLbl.setText(Integer.toString(npdmProvider.getPersonalMmHeapSize()));
        versionLbl.setText(Integer.toString(npdmProvider.getVersion()));
        mainThreadStackSizeLbl.setText(Long.toString(npdmProvider.getMainThreadStackSize()));
        titleNameTf.setText(npdmProvider.getTitleName());
        productCodeTf.setText(byteArrToHexString(npdmProvider.getProductCode()));
        reserved4Tf.setText(byteArrToHexString(npdmProvider.getReserved4()));
        aci0offsetLbl.setText(Long.toString(npdmProvider.getAci0offset()));
        aci0sizeLbl.setText(Long.toString(npdmProvider.getAci0size()));
        acidOffsetLbl.setText(Long.toString(npdmProvider.getAcidOffset()));
        acidSizeLbl.setText(Long.toString(npdmProvider.getAcidSize()));
    }

}
