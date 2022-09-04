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
import javafx.scene.control.Label;
import libKonogonka.Tools.NCA.NCAHeaderTableEntry;

import static libKonogonka.Converter.byteArrToHexString;

public class NCATableController {
    @FXML
    private Label
            mediaStartOffsetLbl,
            mediaEndOffsetLbl,
            unknwn1Lbl,
            unknwn2Lbl;

    public void resetTab() {
        mediaStartOffsetLbl.setText("-");
        mediaEndOffsetLbl.setText("-");
        unknwn1Lbl.setText("-");
        unknwn2Lbl.setText("-");
    }

    public void populateTab(NCAHeaderTableEntry ncaHeaderTableEntry){
        mediaStartOffsetLbl.setText(Long.toString(ncaHeaderTableEntry.getMediaStartOffset()));
        mediaEndOffsetLbl.setText(Long.toString(ncaHeaderTableEntry.getMediaEndOffset()));
        unknwn1Lbl.setText(byteArrToHexString(ncaHeaderTableEntry.getUnknwn1()));
        unknwn2Lbl.setText(byteArrToHexString(ncaHeaderTableEntry.getUnknwn2()));
    }
}
