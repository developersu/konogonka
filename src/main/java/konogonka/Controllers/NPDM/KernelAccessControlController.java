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
package konogonka.Controllers.NPDM;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import libKonogonka.fs.NPDM.KernelAccessControlProvider;

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
    private VBox interruptPairsPane;
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
        interruptPairsPane.getChildren().clear();
        interruptPairsPane.getChildren().add(new Separator());
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

            mapIoPane.getChildren().addAll(
                    new HBox(altStPgNnumOfPgLbl, altStPgNnumOfPgVal),
                    new HBox(roFlagLbl, roFlagVal),
                    new Separator()
            );
        }
        byte[] mapNormalPageRwBarr = kacProvider.getMapNormalPage();
        if (mapNormalPageRwBarr != null){
            stringBuilder = new StringBuilder();
            for (byte b : mapNormalPageRwBarr)
                stringBuilder.append(b);
            stringBuilder.reverse();

            mapNormalPageRwLbl.setText(stringBuilder.toString());
        }
        for (Map.Entry entry : kacProvider.getInterruptPairs().entrySet()){
            Label no = new Label("# "+entry.getKey());
            Label irq0Lbl = new Label("irq0:");
            Label irq1Lbl = new Label("irq1:");
            Label irq0, irq1;

            stringBuilder = new StringBuilder();
            for (byte b : ((byte[][]) entry.getValue())[0])
                stringBuilder.append(b);
            stringBuilder.reverse();
            irq0 = new Label(stringBuilder.toString());
            for (byte b : ((byte[][]) entry.getValue())[1])
                stringBuilder.append(b);
            stringBuilder.reverse();
            irq1 = new Label(stringBuilder.toString());

            no.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
            irq0Lbl.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
            irq1Lbl.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
            irq0.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
            irq1.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));

            interruptPairsPane.getChildren().addAll(
                    no,
                    new HBox(irq0Lbl, irq0),
                    new HBox(irq1Lbl, irq1),
                    new Separator()
            );
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
