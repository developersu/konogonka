package konogonka.Controllers.NCA;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import konogonka.Tools.NCA.NCAHeaderTableEntry;

import static konogonka.LoperConverter.byteArrToHexString;

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
