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
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import libKonogonka.Tools.NPDM.ACID.FSAccessControlProvider;

import java.net.URL;
import java.util.ResourceBundle;

import static libKonogonka.LoperConverter.byteArrToHexString;
import static libKonogonka.LoperConverter.longToOctString;

public class FSAccessControlController implements Initializable {
    @FXML
    private Label ACID_FSAcccessControlVersionLbl,
            ACID_FSAcccessControlPaddingLbl,
            ACID_FSAcccessControlBitbaskLbl;
    @FXML
    private TextField ACID_FSAcccessControlReservedTf;

    @FXML
    private Label mask0,
            mask1,
            mask2,
            mask3,
            mask4,
            mask5,
            mask6,
            mask7,
            mask8,
            mask9,
            mask10,
            mask11,
            mask12,
            mask13,
            mask14,
            mask15,
            mask16,
            mask17,
            mask18,
            mask19,
            mask20,
            mask21,
            mask22,
            mask23,
            mask24,
            mask25,
            mask26,
            mask27,
            mask28,
            mask29,
            mask30,
            mask31,
            mask32,
            mask33,
            mask34,
            mask35,
            mask36,
            mask37,
            mask38,
            mask39,
            mask40,
            mask41,
            mask42,
            mask43,
            mask44,
            mask45,
            mask46,
            mask47,
            mask48,
            mask49,
            mask50,
            mask51,
            mask52,
            mask53,
            mask54,
            mask55,
            mask56,
            mask57,
            mask58,
            mask59,
            mask60,
            mask61,
            mask62,
            mask63;

    private Label[] masksArr;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        masksArr = new Label[64];
        masksArr[0] = mask0;
        masksArr[1] = mask1;
        masksArr[2] = mask2;
        masksArr[3] = mask3;
        masksArr[4] = mask4;
        masksArr[5] = mask5;
        masksArr[6] = mask6;
        masksArr[7] = mask7;
        masksArr[8] = mask8;
        masksArr[9] = mask9;
        masksArr[10] = mask10;
        masksArr[11] = mask11;
        masksArr[12] = mask12;
        masksArr[13] = mask13;
        masksArr[14] = mask14;
        masksArr[15] = mask15;
        masksArr[16] = mask16;
        masksArr[17] = mask17;
        masksArr[18] = mask18;
        masksArr[19] = mask19;
        masksArr[20] = mask20;
        masksArr[21] = mask21;
        masksArr[22] = mask22;
        masksArr[23] = mask23;
        masksArr[24] = mask24;
        masksArr[25] = mask25;
        masksArr[26] = mask26;
        masksArr[27] = mask27;
        masksArr[28] = mask28;
        masksArr[29] = mask29;
        masksArr[30] = mask30;
        masksArr[31] = mask31;
        masksArr[32] = mask32;
        masksArr[33] = mask33;
        masksArr[34] = mask34;
        masksArr[35] = mask35;
        masksArr[36] = mask36;
        masksArr[37] = mask37;
        masksArr[38] = mask38;
        masksArr[39] = mask39;
        masksArr[40] = mask40;
        masksArr[41] = mask41;
        masksArr[42] = mask42;
        masksArr[43] = mask43;
        masksArr[44] = mask44;
        masksArr[45] = mask45;
        masksArr[46] = mask46;
        masksArr[47] = mask47;
        masksArr[48] = mask48;
        masksArr[49] = mask49;
        masksArr[50] = mask50;
        masksArr[51] = mask51;
        masksArr[52] = mask52;
        masksArr[53] = mask53;
        masksArr[54] = mask54;
        masksArr[55] = mask55;
        masksArr[56] = mask56;
        masksArr[57] = mask57;
        masksArr[58] = mask58;
        masksArr[59] = mask59;
        masksArr[60] = mask60;
        masksArr[61] = mask61;
        masksArr[62] = mask62;
        masksArr[63] = mask63;
    }

    public void resetTab() {
        ACID_FSAcccessControlVersionLbl.setText("-");
        ACID_FSAcccessControlPaddingLbl.setText("-");
        ACID_FSAcccessControlBitbaskLbl.setText("-");
        ACID_FSAcccessControlReservedTf.setText("-");

        for (int i = 0; i < 64; i++)
            masksArr[i].setText("-");
    }

    public void populateFields(FSAccessControlProvider provider){
        ACID_FSAcccessControlVersionLbl.setText(String.format("0x%02x", provider.getVersion()));
        ACID_FSAcccessControlPaddingLbl.setText(byteArrToHexString(provider.getPadding()));
        StringBuilder sb = new StringBuilder(longToOctString(provider.getPermissionsBitmask()));
        sb.reverse();
        String mask = sb.toString();
        ACID_FSAcccessControlBitbaskLbl.setText(mask);
        ACID_FSAcccessControlReservedTf.setText(byteArrToHexString(provider.getReserved()));

        for (int i = 0; i < 64; i++)
            masksArr[i].setText(mask.substring(i, i+1));
    }
}
