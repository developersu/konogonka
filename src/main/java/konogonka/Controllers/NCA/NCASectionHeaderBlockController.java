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
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import libKonogonka.Tools.NCA.NCASectionTableBlock.NCASectionBlock;

import static libKonogonka.LoperConverter.byteArrToHexString;

public class NCASectionHeaderBlockController {
    @FXML
    private Label
            versionLbl,
            fsTypeLbl,
            hashTypeLbl,
            cryptoTypeLbl,
            paddingLbl;
    @FXML
    private TextField
            BKTRHeaderTf;
    @FXML
    private TitledPane
            romFsTitlePanel,
            pfs0TitlePanel;
    // PFS0Provider
    @FXML
    private Label
            pfs0blockSizeLbl,
            pfs0unknownNumberTwoLbl,
            pfs0hashTableOffsetLbl,
            pfs0hashTableSizeLbl,
            pfs0relativeToSectionStartOffsetLbl,
            pfs0sizePfs0Lbl;
    @FXML
    private TextField
            pfs0SHA256hashTf,
            pfs0zeroesTf;
    // RomFS (IVFC)
    @FXML
    private Label
            romFsMagicLbl,
            romFsMagicNumberLbl,
            romFsMasterHashSizeLbl,
            romFsTotalNumberOfLevelsLbl,
            romFsLvl1OffsetLbl,
            romFsLvl1SizeLbl,
            romFsLvl1SBlockSizeLbl,
            romFsReserved1Lbl,

            romFsLvl2OffsetLbl,
            romFsLvl2SizeLbl,
            romFsLvl2SBlockSizeLbl,
            romFsReserved2Lbl,

            romFsLvl3OffsetLbl,
            romFsLvl3SizeLbl,
            romFsLvl3SBlockSizeLbl,
            romFsReserved3Lbl,

            romFsLvl4OffsetLbl,
            romFsLvl4SizeLbl,
            romFsLvl4SBlockSizeLbl,
            romFsReserved4Lbl,

            romFsLvl5OffsetLbl,
            romFsLvl5SizeLbl,
            romFsLvl5SBlockSizeLbl,
            romFsReserved5Lbl,

            romFsLvl6OffsetLbl,
            romFsLvl6SizeLbl,
            romFsLvl6SBlockSizeLbl,
            romFsReserved6Lbl;
    @FXML
    private TextField
            romFsUnknownTf,
            romFsHashTf,
            unknwnEndPaddingTF;

    @FXML
    private Label
            BKTRoffsetSection1Lbl,
            BKTRsizeSection1Lbl,
            BKTRmagicSection1Lbl,
            BKTRu32Section1Lbl,
            BKTRs32Section1Lbl,
            BKTRunknownSection1Lbl,
            BKTRoffsetSection2Lbl,
            BKTRsizeSection2Lbl,
            BKTRmagicSection2Lbl,
            BKTRu32Section2Lbl,
            BKTRs32Section2Lbl,
            BKTRunknownSection2Lbl,
            sectionCTRLbl;

    public void resetTab() {
        versionLbl.setText("-");
        fsTypeLbl.setText("-");
        hashTypeLbl.setText("-");
        cryptoTypeLbl.setText("-");
        paddingLbl.setText("-");

        romFsTitlePanel.setDisable(false);
        romFsTitlePanel.setExpanded(false);
        pfs0TitlePanel.setDisable(false);
        pfs0TitlePanel.setExpanded(false);
        //RomFS
        romFsMagicLbl.setText("-");
        romFsMagicNumberLbl.setText("-");
        romFsMasterHashSizeLbl.setText("-");
        romFsTotalNumberOfLevelsLbl.setText("-");
        romFsLvl1OffsetLbl.setText("-");
        romFsLvl1SizeLbl.setText("-");
        romFsLvl1SBlockSizeLbl.setText("-");
        romFsReserved1Lbl.setText("-");

        romFsLvl2OffsetLbl.setText("-");
        romFsLvl2SizeLbl.setText("-");
        romFsLvl2SBlockSizeLbl.setText("-");
        romFsReserved2Lbl.setText("-");

        romFsLvl3OffsetLbl.setText("-");
        romFsLvl3SizeLbl.setText("-");
        romFsLvl3SBlockSizeLbl.setText("-");
        romFsReserved3Lbl.setText("-");

        romFsLvl4OffsetLbl.setText("-");
        romFsLvl4SizeLbl.setText("-");
        romFsLvl4SBlockSizeLbl.setText("-");
        romFsReserved4Lbl.setText("-");

        romFsLvl5OffsetLbl.setText("-");
        romFsLvl5SizeLbl.setText("-");
        romFsLvl5SBlockSizeLbl.setText("-");
        romFsReserved5Lbl.setText("-");

        romFsLvl6OffsetLbl.setText("-");
        romFsLvl6SizeLbl.setText("-");
        romFsLvl6SBlockSizeLbl.setText("-");
        romFsReserved6Lbl.setText("-");

        romFsUnknownTf.setText("-");
        romFsHashTf.setText("-");

        // PFS0Provider
        pfs0SHA256hashTf.setText("-");
        pfs0blockSizeLbl.setText("-");
        pfs0unknownNumberTwoLbl.setText("-");
        pfs0hashTableOffsetLbl.setText("-");
        pfs0hashTableSizeLbl.setText("-");
        pfs0relativeToSectionStartOffsetLbl.setText("-");
        pfs0sizePfs0Lbl.setText("-");
        pfs0zeroesTf.setText("-");

        BKTRHeaderTf.setText("-");
        BKTRoffsetSection1Lbl.setText("-");
        BKTRsizeSection1Lbl.setText("-");
        BKTRmagicSection1Lbl.setText("-");
        BKTRu32Section1Lbl.setText("-");
        BKTRs32Section1Lbl.setText("-");
        BKTRunknownSection1Lbl.setText("-");
        BKTRoffsetSection2Lbl.setText("-");
        BKTRsizeSection2Lbl.setText("-");
        BKTRmagicSection2Lbl.setText("-");
        BKTRu32Section2Lbl.setText("-");
        BKTRs32Section2Lbl.setText("-");
        BKTRunknownSection2Lbl.setText("-");
        sectionCTRLbl.setText("-");
        unknwnEndPaddingTF.setText("-");
    }

    public void populateTab(NCASectionBlock ncaSectionBlock){
        versionLbl.setText(byteArrToHexString(ncaSectionBlock.getVersion()));
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("0x%02x ", ncaSectionBlock.getFsType()));
        if (ncaSectionBlock.getFsType() == 0)
            sb.append("(RomFS)");
        else if (ncaSectionBlock.getFsType() == 1)
            sb.append("(PFS0)");
        fsTypeLbl.setText(sb.toString());
        sb = new StringBuilder();
        sb.append(String.format("0x%02x ", ncaSectionBlock.getHashType()));
        if (ncaSectionBlock.getHashType() == 0x3)
            sb.append("(RomFS)");
        else if (ncaSectionBlock.getHashType() == 0x2)
            sb.append("(PFS0)");
        hashTypeLbl.setText(sb.toString());
        cryptoTypeLbl.setText(String.format("%02x ", ncaSectionBlock.getCryptoType()));
        paddingLbl.setText(byteArrToHexString(ncaSectionBlock.getPadding()));
        if ((ncaSectionBlock.getFsType() == 0) && (ncaSectionBlock.getHashType() == 0x3)){
            romFsMagicLbl.setText(ncaSectionBlock.getSuperBlockIVFC().getMagic());
            romFsMagicNumberLbl.setText(ncaSectionBlock.getSuperBlockIVFC().getMagicNumber()
                +(ncaSectionBlock.getSuperBlockIVFC().getMagicNumber() == 0x20000? " (OK)":" (wrong magic number)"));
            romFsMasterHashSizeLbl.setText(Integer.toString(ncaSectionBlock.getSuperBlockIVFC().getMasterHashSize()));
            romFsTotalNumberOfLevelsLbl.setText(Integer.toString(ncaSectionBlock.getSuperBlockIVFC().getTotalNumberOfLevels()));
            romFsLvl1OffsetLbl.setText(getCuteDecHexRepresentation(ncaSectionBlock.getSuperBlockIVFC().getLvl1Offset()));
            romFsLvl1SizeLbl.setText(getCuteDecHexRepresentation(ncaSectionBlock.getSuperBlockIVFC().getLvl1Size()));
            romFsLvl1SBlockSizeLbl.setText(Integer.toString(ncaSectionBlock.getSuperBlockIVFC().getLvl1SBlockSize()));
            romFsReserved1Lbl.setText(byteArrToHexString(ncaSectionBlock.getSuperBlockIVFC().getReserved1()));

            romFsLvl2OffsetLbl.setText(getCuteDecHexRepresentation(ncaSectionBlock.getSuperBlockIVFC().getLvl2Offset()));
            romFsLvl2SizeLbl.setText(getCuteDecHexRepresentation(ncaSectionBlock.getSuperBlockIVFC().getLvl2Size()));
            romFsLvl2SBlockSizeLbl.setText(Integer.toString(ncaSectionBlock.getSuperBlockIVFC().getLvl2SBlockSize()));
            romFsReserved2Lbl.setText(byteArrToHexString(ncaSectionBlock.getSuperBlockIVFC().getReserved2()));
            
            romFsLvl3OffsetLbl.setText(getCuteDecHexRepresentation(ncaSectionBlock.getSuperBlockIVFC().getLvl3Offset()));
            romFsLvl3SizeLbl.setText(getCuteDecHexRepresentation(ncaSectionBlock.getSuperBlockIVFC().getLvl3Size()));
            romFsLvl3SBlockSizeLbl.setText(Integer.toString(ncaSectionBlock.getSuperBlockIVFC().getLvl3SBlockSize()));
            romFsReserved3Lbl.setText(byteArrToHexString(ncaSectionBlock.getSuperBlockIVFC().getReserved3()));

            romFsLvl4OffsetLbl.setText(getCuteDecHexRepresentation(ncaSectionBlock.getSuperBlockIVFC().getLvl4Offset()));
            romFsLvl4SizeLbl.setText(getCuteDecHexRepresentation(ncaSectionBlock.getSuperBlockIVFC().getLvl4Size()));
            romFsLvl4SBlockSizeLbl.setText(Integer.toString(ncaSectionBlock.getSuperBlockIVFC().getLvl4SBlockSize()));
            romFsReserved4Lbl.setText(byteArrToHexString(ncaSectionBlock.getSuperBlockIVFC().getReserved4()));

            romFsLvl5OffsetLbl.setText(getCuteDecHexRepresentation(ncaSectionBlock.getSuperBlockIVFC().getLvl5Offset()));
            romFsLvl5SizeLbl.setText(getCuteDecHexRepresentation(ncaSectionBlock.getSuperBlockIVFC().getLvl5Size()));
            romFsLvl5SBlockSizeLbl.setText(Integer.toString(ncaSectionBlock.getSuperBlockIVFC().getLvl5SBlockSize()));
            romFsReserved5Lbl.setText(byteArrToHexString(ncaSectionBlock.getSuperBlockIVFC().getReserved5()));

            romFsLvl6OffsetLbl.setText(getCuteDecHexRepresentation(ncaSectionBlock.getSuperBlockIVFC().getLvl6Offset()));
            romFsLvl6SizeLbl.setText(getCuteDecHexRepresentation(ncaSectionBlock.getSuperBlockIVFC().getLvl6Size()));
            romFsLvl6SBlockSizeLbl.setText(Integer.toString(ncaSectionBlock.getSuperBlockIVFC().getLvl6SBlockSize()));
            romFsReserved6Lbl.setText(byteArrToHexString(ncaSectionBlock.getSuperBlockIVFC().getReserved6()));

            romFsUnknownTf.setText(byteArrToHexString(ncaSectionBlock.getSuperBlockIVFC().getUnknown()));
            romFsHashTf.setText(byteArrToHexString(ncaSectionBlock.getSuperBlockIVFC().getHash()));
            pfs0TitlePanel.setDisable(true);
        }
        else if ((ncaSectionBlock.getFsType() == 0x1) && (ncaSectionBlock.getHashType() == 0x2)){
            pfs0SHA256hashTf.setText(byteArrToHexString(ncaSectionBlock.getSuperBlockPFS0().getSHA256hash()));
            pfs0blockSizeLbl.setText(Integer.toString(ncaSectionBlock.getSuperBlockPFS0().getBlockSize()));
            pfs0unknownNumberTwoLbl.setText(Integer.toString(ncaSectionBlock.getSuperBlockPFS0().getUnknownNumberTwo()));
            pfs0hashTableOffsetLbl.setText(Long.toString(ncaSectionBlock.getSuperBlockPFS0().getHashTableOffset()));
            pfs0hashTableSizeLbl.setText(Long.toString(ncaSectionBlock.getSuperBlockPFS0().getHashTableSize()));
            pfs0relativeToSectionStartOffsetLbl.setText(Long.toString(ncaSectionBlock.getSuperBlockPFS0().getPfs0offset()));
            pfs0sizePfs0Lbl.setText(Long.toString(ncaSectionBlock.getSuperBlockPFS0().getPfs0size()));
            pfs0zeroesTf.setText(byteArrToHexString(ncaSectionBlock.getSuperBlockPFS0().getZeroes()));
            romFsTitlePanel.setDisable(true);
        }
        else {
            pfs0TitlePanel.setDisable(true);
            romFsTitlePanel.setDisable(true);
        }
        BKTRHeaderTf.setText(byteArrToHexString(ncaSectionBlock.getBKTRfullHeader()));
        BKTRoffsetSection1Lbl.setText(Long.toString(ncaSectionBlock.getBKTRoffsetSection1()));
        BKTRsizeSection1Lbl.setText(Long.toString(ncaSectionBlock.getBKTRsizeSection1()));
        BKTRmagicSection1Lbl.setText(ncaSectionBlock.getBKTRmagicSection1());
        BKTRu32Section1Lbl.setText(Integer.toString(ncaSectionBlock.getBKTRu32Section1()));
        BKTRs32Section1Lbl.setText(Integer.toString(ncaSectionBlock.getBKTRs32Section1()));
        BKTRunknownSection1Lbl.setText(byteArrToHexString(ncaSectionBlock.getBKTRunknownSection1()));
        BKTRoffsetSection2Lbl.setText(Long.toString(ncaSectionBlock.getBKTRoffsetSection2()));
        BKTRsizeSection2Lbl.setText(Long.toString(ncaSectionBlock.getBKTRsizeSection2()));
        BKTRmagicSection2Lbl.setText(ncaSectionBlock.getBKTRmagicSection2());
        BKTRu32Section2Lbl.setText(Integer.toString(ncaSectionBlock.getBKTRu32Section2()));
        BKTRs32Section2Lbl.setText(Integer.toString(ncaSectionBlock.getBKTRs32Section2()));
        BKTRunknownSection2Lbl.setText(byteArrToHexString(ncaSectionBlock.getBKTRunknownSection2()));
        sectionCTRLbl.setText(byteArrToHexString(ncaSectionBlock.getSectionCTR()));
        unknwnEndPaddingTF.setText(byteArrToHexString(ncaSectionBlock.getUnknownEndPadding()));
    }

    private String getCuteDecHexRepresentation(long value){
        return String.format("%d (0x%02x)", value, value);
    }
}
