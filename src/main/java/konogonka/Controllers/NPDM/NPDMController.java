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
package konogonka.Controllers.NPDM;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import konogonka.Controllers.ITabController;
import konogonka.Tools.ISuperProvider;
import konogonka.Tools.NPDM.ACI0.ACI0Provider;
import konogonka.Tools.NPDM.ACID.ACIDProvider;
import konogonka.Tools.NPDM.NPDMProvider;
import konogonka.Workers.Analyzer;

import java.io.File;
import java.net.URL;
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

    // ACI0
    @FXML
    private Label aci0MagicNumLbl,
            aci0Reserved1Lbl,
            aci0TitleIDLbl,
            aci0Reserved2Lbl,
            aci0FsAccessHeaderOffsetLbl,
            aci0FsAccessHeaderSizeLbl,
            aci0ServiceAccessControlOffsetLbl,
            aci0ServiceAccessControlSizeLbl,
            aci0KernelAccessControlOffsetLbl,
            aci0KernelAccessControlSizeLbl,
            aci0Reserved3Lbl;
    // ACID
    @FXML
    TextField acidRsa2048signatureTf,
                acidRsa2048publicKeyTf;
    @FXML
    private Label acidMagicNumLbl,
            acidDataSizeLbl,
            acidReserved1Lbl,
            acidFlag1Lbl,
            acidFlag2Lbl,
            acidFlag3Lbl,
            acidFlag4Lbl,
            acidTitleRangeMinLbl,
            acidTitleRangeMaxLbl,
            acidFsAccessControlOffsetLbl,
            acidFsAccessControlSizeLbl,
            acidServiceAccessControlOffsetLbl,
            acidServiceAccessControlSizeLbl,
            acidKernelAccessControlOffsetLbl,
            acidKernelAccessControlSizeLbl,
            acidReserved2Lbl;
    //ACID
    @FXML
    private FSAccessControlController ACIDFSAccessControlTableController;
    @FXML
    private ServiceAccessControlController ACIDServiceAccessControlTableController;
    @FXML
    private KernelAccessControlController ACIDKernelAccessControlTableController;
    // ACI0
    @FXML
    private FSAccessHeaderController ACI0FSAccessHeaderTableController;
    @FXML
    private ServiceAccessControlController ACI0ServiceAccessControlTableController;
    @FXML
    private KernelAccessControlController ACI0KernelAccessControlTableController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    @Override
    public void analyze(File file) { analyze(file, 0); }
    @Override
    public void analyze(ISuperProvider parentProvider, int fileNo) throws Exception {
        Task analyzer = Analyzer.analyzeNPDM(parentProvider, fileNo);
        analyzer.setOnSucceeded(e->{
            NPDMProvider npdm = (NPDMProvider) analyzer.getValue();
            setData(npdm, null);
        });
        Thread workThread = new Thread(analyzer);
        workThread.setDaemon(true);
        workThread.start();
    }
    @Override
    public void analyze(File file, long offset) {
        Task analyzer = Analyzer.analyzeNPDM(file, offset);
        analyzer.setOnSucceeded(e->{
            NPDMProvider npdm = (NPDMProvider) analyzer.getValue();
            if (offset == 0)
                setData(npdm, file);
            else
                setData(npdm, null);
        });
        Thread workThread = new Thread(analyzer);
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

        // ACI0
        aci0MagicNumLbl.setText("-");
        aci0Reserved1Lbl.setText("-");
        aci0TitleIDLbl.setText("-");
        aci0Reserved2Lbl.setText("-");
        aci0FsAccessHeaderOffsetLbl.setText("-");
        aci0FsAccessHeaderSizeLbl.setText("-");
        aci0ServiceAccessControlOffsetLbl.setText("-");
        aci0ServiceAccessControlSizeLbl.setText("-");
        aci0KernelAccessControlOffsetLbl.setText("-");
        aci0KernelAccessControlSizeLbl.setText("-");
        aci0Reserved3Lbl.setText("-");
        // ACID
        acidRsa2048signatureTf.setText("-");
        acidRsa2048publicKeyTf.setText("-");
        acidMagicNumLbl.setText("-");
        acidDataSizeLbl.setText("-");
        acidReserved1Lbl.setText("-");
        acidFlag1Lbl.setText("-");
        acidFlag2Lbl.setText("-");
        acidFlag3Lbl.setText("-");
        acidFlag4Lbl.setText("-");
        acidTitleRangeMinLbl.setText("-");
        acidTitleRangeMaxLbl.setText("-");
        acidFsAccessControlOffsetLbl.setText("-");
        acidFsAccessControlSizeLbl.setText("-");
        acidServiceAccessControlOffsetLbl.setText("-");
        acidServiceAccessControlSizeLbl.setText("-");
        acidKernelAccessControlOffsetLbl.setText("-");
        acidKernelAccessControlSizeLbl.setText("-");
        acidReserved2Lbl.setText("-");
        // ACI0
        ACI0FSAccessHeaderTableController.resetTab();
        ACI0ServiceAccessControlTableController.resetTab();
        ACI0KernelAccessControlTableController.resetTab();
        // ACID
        ACIDFSAccessControlTableController.resetTab();
        ACIDServiceAccessControlTableController.resetTab();
        ACIDKernelAccessControlTableController.resetTab();
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
        aci0offsetLbl.setText(Integer.toString(npdmProvider.getAci0offset()));
        aci0sizeLbl.setText(Integer.toString(npdmProvider.getAci0size()));
        acidOffsetLbl.setText(Integer.toString(npdmProvider.getAcidOffset()));
        acidSizeLbl.setText(Integer.toString(npdmProvider.getAcidSize()));
        // ACI0
        ACI0Provider aci0 = npdmProvider.getAci0();
        aci0MagicNumLbl.setText(aci0.getMagicNum());
        aci0Reserved1Lbl.setText(byteArrToHexString(aci0.getReserved1()));
        aci0TitleIDLbl.setText(byteArrToHexString(aci0.getTitleID()));
        aci0Reserved2Lbl.setText(byteArrToHexString(aci0.getReserved2()));
        aci0FsAccessHeaderOffsetLbl.setText(Integer.toString(aci0.getFsAccessHeaderOffset()));
        aci0FsAccessHeaderSizeLbl.setText(Integer.toString(aci0.getFsAccessHeaderSize()));
        aci0ServiceAccessControlOffsetLbl.setText(Integer.toString(aci0.getServiceAccessControlOffset()));
        aci0ServiceAccessControlSizeLbl.setText(Integer.toString(aci0.getServiceAccessControlSize()));
        aci0KernelAccessControlOffsetLbl.setText(Integer.toString(aci0.getKernelAccessControlOffset()));
        aci0KernelAccessControlSizeLbl.setText(Integer.toString(aci0.getKernelAccessControlSize()));
        aci0Reserved3Lbl.setText(byteArrToHexString(aci0.getReserved3()));

        ACI0FSAccessHeaderTableController.populateFields(aci0.getFsAccessHeaderProvider());
        ACI0ServiceAccessControlTableController.populateFields(aci0.getServiceAccessControlProvider().getCollection());
        ACI0KernelAccessControlTableController.populateFields(aci0.getKernelAccessControlProvider());
        // ACID
        ACIDProvider acid = npdmProvider.getAcid();
        acidRsa2048signatureTf.setText(byteArrToHexString(acid.getRsa2048signature()));
        acidRsa2048publicKeyTf.setText(byteArrToHexString(acid.getRsa2048publicKey()));
        acidMagicNumLbl.setText(acid.getMagicNum());
        acidDataSizeLbl.setText(Integer.toString(acid.getDataSize()));
        acidReserved1Lbl.setText(byteArrToHexString(acid.getReserved1()));
        acidFlag1Lbl.setText(String.format("0x%02x", acid.getFlag1()));
        acidFlag2Lbl.setText(String.format("0x%02x", acid.getFlag2()));
        acidFlag3Lbl.setText(String.format("0x%02x", acid.getFlag3()));
        acidFlag4Lbl.setText(String.format("0x%02x", acid.getFlag4()));
        acidTitleRangeMinLbl.setText(Long.toString(acid.getTitleRangeMin()));
        acidTitleRangeMaxLbl.setText(Long.toString(acid.getTitleRangeMax()));
        acidFsAccessControlOffsetLbl.setText(Integer.toString(acid.getFsAccessControlOffset()));
        acidFsAccessControlSizeLbl.setText(Integer.toString(acid.getFsAccessControlSize()));
        acidServiceAccessControlOffsetLbl.setText(Integer.toString(acid.getServiceAccessControlOffset()));
        acidServiceAccessControlSizeLbl.setText(Integer.toString(acid.getServiceAccessControlSize()));
        acidKernelAccessControlOffsetLbl.setText(Integer.toString(acid.getKernelAccessControlOffset()));
        acidKernelAccessControlSizeLbl.setText(Integer.toString(acid.getKernelAccessControlSize()));
        acidReserved2Lbl.setText(byteArrToHexString(acid.getReserved2()));

        ACIDFSAccessControlTableController.populateFields(acid.getFsAccessControlProvider());
        ACIDServiceAccessControlTableController.populateFields(acid.getServiceAccessControlProvider().getCollection());
        ACIDKernelAccessControlTableController.populateFields(acid.getKernelAccessControlProvider());
    }
}