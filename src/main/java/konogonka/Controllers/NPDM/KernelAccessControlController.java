package konogonka.Controllers.NPDM;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import konogonka.Tools.NPDM.ACID.KernelAccessControlProvider;

import java.util.Map;

public class KernelAccessControlController {
    @FXML
    private VBox KACRawPane;

    @FXML Label kFlagsAval,
            hiAllCpuId,
            loAllCpuId,
            hiAllThreadPrio,
            loAllThreadPrio;
    @FXML
    private VBox syscallMaskPane;

    @FXML
    private VBox mapIoPane;

    @FXML
    private Label mapNormalPageRwLbl;

    @FXML
    private Label interruptPairAvalLbl,
            irq0Lbl,
            irq1Lbl;

    @FXML
    private Label appTypeLbl,
            kerRelVerLbl,
            handleTableSizeLbl;

    @FXML
    private Label dbgAvalLbl,
                    canBeDbgLbl,
                    canDbgLbl;

    public void resetTab(){
        KACRawPane.getChildren().clear();
        kFlagsAval.setText("?");
        hiAllCpuId.setText("-");
        loAllCpuId.setText("-");
        hiAllThreadPrio.setText("-");
        loAllThreadPrio.setText("-");
        syscallMaskPane.getChildren().clear();
        mapIoPane.getChildren().clear();
        mapIoPane.getChildren().add(new Separator());
        mapNormalPageRwLbl.setText("-");
        interruptPairAvalLbl.setText("?");
        irq0Lbl.setText("-");
        irq1Lbl.setText("-");
        appTypeLbl.setText("-");
        kerRelVerLbl.setText("-");
        handleTableSizeLbl.setText("-");
        dbgAvalLbl.setText("?");
        canBeDbgLbl.setText("-");
        canDbgLbl.setText("-");
    }

    public void populateFields(KernelAccessControlProvider kacProvider){
        resetTab();
        StringBuilder stringBuilder;
        for (Integer i: kacProvider.getRawData()){
            Label entry = new Label(String.format("%32s", Integer.toBinaryString(i)).replace(' ', '0'));
            entry.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
            KACRawPane.getChildren().add(entry);
        }
        if (kacProvider.isKernelFlagsAvailable()){
            kFlagsAval.setText("(available)");
            hiAllCpuId.setText(Integer.toString(kacProvider.getKernelFlagCpuIdHi()));
            loAllCpuId.setText(Integer.toString(kacProvider.getKernelFlagCpuIdLo()));
            hiAllThreadPrio.setText(Integer.toString(kacProvider.getKernelFlagThreadPrioHi()));
            loAllThreadPrio.setText(Integer.toString(kacProvider.getKernelFlagThreadPrioLo()));
        }
        else
            kFlagsAval.setText("(not available)");
        for (Map.Entry entry : kacProvider.getSyscallMasks().entrySet()) {
            byte[] maskBarr = (byte[]) entry.getValue();
            stringBuilder = new StringBuilder();
            for (byte b: maskBarr)
                stringBuilder.append(b);
            stringBuilder.reverse();    // Flip to show as is
            Label mask = new Label(stringBuilder.toString());
            Label maskTableIndex = new Label(((Byte) entry.getKey()).toString());

            mask.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
            maskTableIndex.setPadding(new Insets(5.0, 15.0, 5.0, 5.0));

            syscallMaskPane.getChildren().add(new HBox(maskTableIndex, mask));
        }

        for (Map.Entry entry : kacProvider.getMapIoOrNormalRange().entrySet()){
            byte[] altStPgNnumOfPg = (byte[]) entry.getKey();
            stringBuilder = new StringBuilder();
            for (byte b : altStPgNnumOfPg)
                stringBuilder.append(b);
            stringBuilder.reverse();

            Label altStPgNnumOfPgLbl = new Label("Alternating start page and number of pages:");
            Label altStPgNnumOfPgVal = new Label(stringBuilder.toString());

            Label roFlagLbl = new Label("Alternating read-only flag:");
            Label roFlagVal = new Label(((Boolean) entry.getValue()).toString());

            altStPgNnumOfPgLbl.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
            altStPgNnumOfPgVal.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
            roFlagLbl.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
            roFlagVal.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));

            mapIoPane.getChildren().add(new HBox(altStPgNnumOfPgLbl, altStPgNnumOfPgVal));
            mapIoPane.getChildren().add(new HBox(roFlagLbl, roFlagVal));
            mapIoPane.getChildren().add(new Separator());
        }
        byte[] mapNormalPageRwBarr = kacProvider.getMapNormalPage();
        if (mapNormalPageRwBarr != null){
            stringBuilder = new StringBuilder();
            for (byte b : mapNormalPageRwBarr)
                stringBuilder.append(b);
            stringBuilder.reverse();

            mapNormalPageRwLbl.setText(stringBuilder.toString());
        }
        if (kacProvider.isInterruptPairAvailable()){
            interruptPairAvalLbl.setText("(available)");
            stringBuilder = new StringBuilder();
            for (byte b : kacProvider.getIrq0())
                stringBuilder.append(b);
            stringBuilder.reverse();
            irq0Lbl.setText(stringBuilder.toString());

            stringBuilder = new StringBuilder();
            for (byte b : kacProvider.getIrq1())
                stringBuilder.append(b);
            stringBuilder.reverse();
            irq1Lbl.setText(stringBuilder.toString());
        }
        else {
            interruptPairAvalLbl.setText("(not available)");
        }
        switch (kacProvider.getApplicationType()){
            case 0:
                appTypeLbl.setText(kacProvider.getApplicationType()+" (sysmodule)");
                break;
            case 1:
                appTypeLbl.setText(kacProvider.getApplicationType()+" (application)");
                break;
            case 2:
                appTypeLbl.setText(kacProvider.getApplicationType()+" (applet)");
                break;
            default:
                appTypeLbl.setText(kacProvider.getApplicationType()+" (???)");
        }

        if (kacProvider.isKernelRelVersionAvailable())
            kerRelVerLbl.setText(kacProvider.getKernelRelVersionMajor()+"."+kacProvider.getKernelRelVersionMinor()+".0");

        handleTableSizeLbl.setText(Integer.toString(kacProvider.getHandleTableSize())); // todo: validate if not null

        if (kacProvider.isDebugFlagsAvailable()){
            dbgAvalLbl.setText("(available)");
            canBeDbgLbl.setText(Boolean.toString(kacProvider.isCanBeDebugged()));
            canDbgLbl.setText(Boolean.toString(kacProvider.isCanDebugOthers()));
        }
        else
            dbgAvalLbl.setText("(not available)");
    }
}
