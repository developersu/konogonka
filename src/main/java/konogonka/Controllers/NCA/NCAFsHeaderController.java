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
import libKonogonka.Tools.NCA.NCASectionTableBlock.CompressionInfo;
import libKonogonka.Tools.NCA.NCASectionTableBlock.MetaDataHashDataInfo;
import libKonogonka.Tools.NCA.NCASectionTableBlock.NcaFsHeader;
import libKonogonka.Tools.NCA.NCASectionTableBlock.SparseInfo;

import static libKonogonka.Converter.byteArrToHexStringAsLE;

public class NCAFsHeaderController {
    @FXML
    private Label
            versionLbl,
            fsTypeLbl,
            hashTypeLbl,
            cryptoTypeLbl,
            metaDataHashTypeLbl,
            paddingLbl;
    @FXML
    private TitledPane
            romFsTitlePanel,
            pfs0TitlePanel;
    // PFS0Provider
    @FXML
    private Label
            pfs0blockSizeLbl,
            pfs0LayerCountLbl,
            pfs0hashTableOffsetLbl,
            pfs0hashTableSizeLbl,
            pfs0relativeToSectionStartOffsetLbl,
            pfs0sizePfs0Lbl,

            sparseInfoTableOffsetLbl,
            sparseInfoTableSizeLbl,
            sparseInfoMagicLbl,
            sparseInfoVersionLbl,
            sparseInfoEntryCountLbl,
            sparseInfoUnknownLbl,
            sparseInfoPhysicalOffsetLbl,
            sparseInfoGenerationLbl,
                    sparseInfoReseredLbl,
            compressionInfoTableOffsetLbl,
            compressionInfoTableSizeLbl,
            compressionMagicLbl,
            compressionInfoVersionLbl,
            compressionInfoEntryCountLbl,
            compressionInfoUnknownLbl,
                    compressionInfoReservedLbl,
            metaDataHashDataTableOffsetLbl,
            metaDataHashDataTableSizeLbl,
            metaDataHashDataTableHashLbl;
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
            romFsReserved6Lbl,
            romFsReservedTailLbl;
    @FXML
    private TextField
            signatureSaltTf,
            romFsMasterHashTf,
            unknwnEndPaddingTF;

    @FXML
    private Label
            indirectOffsetLbl,
            indirectSizeLbl,
            indirectInfoMagicLbl,
            indirectInfoVersionLbl,
            indirectInfoEntryCountLbl,
            indirectInfoUnknownLbl,
            aesCtrExOffsetLbl,
            aesCtrExSizeLbl,
            aesCtrExMagicLbl,
            aesCtrExVersionLbl,
            aesCtrExEntryCountLbl,
            aesCtrExUnknownLbl,
            sectionCTRLbl,
            generationLbl;

    public void resetTab() {
        versionLbl.setText("-");
        fsTypeLbl.setText("-");
        hashTypeLbl.setText("-");
        cryptoTypeLbl.setText("-");
        metaDataHashTypeLbl.setText("-");
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

        signatureSaltTf.setText("-");
        romFsMasterHashTf.setText("-");
        romFsReservedTailLbl.setText("-");

        // PFS0Provider
        pfs0SHA256hashTf.setText("-");
        pfs0blockSizeLbl.setText("-");
        pfs0LayerCountLbl.setText("-");
        pfs0hashTableOffsetLbl.setText("-");
        pfs0hashTableSizeLbl.setText("-");
        pfs0relativeToSectionStartOffsetLbl.setText("-");
        pfs0sizePfs0Lbl.setText("-");
        pfs0zeroesTf.setText("-");

        indirectOffsetLbl.setText("-");
        indirectSizeLbl.setText("-");
        indirectInfoMagicLbl.setText("-");
        indirectInfoVersionLbl.setText("-");
        indirectInfoEntryCountLbl.setText("-");
        indirectInfoUnknownLbl.setText("-");
        aesCtrExOffsetLbl.setText("-");
        aesCtrExSizeLbl.setText("-");
        aesCtrExMagicLbl.setText("-");
        aesCtrExVersionLbl.setText("-");
        aesCtrExEntryCountLbl.setText("-");
        aesCtrExUnknownLbl.setText("-");
        sectionCTRLbl.setText("-");
        generationLbl.setText("-");
        unknwnEndPaddingTF.setText("-");

        sparseInfoTableOffsetLbl.setText("-");
        sparseInfoTableSizeLbl.setText("-");
        sparseInfoMagicLbl.setText("-");
        sparseInfoVersionLbl.setText("-");
        sparseInfoEntryCountLbl.setText("-");
        sparseInfoUnknownLbl.setText("-");
        sparseInfoPhysicalOffsetLbl.setText("-");
        sparseInfoGenerationLbl.setText("-");
        sparseInfoReseredLbl.setText("-");
        compressionInfoTableOffsetLbl.setText("-");
        compressionInfoTableSizeLbl.setText("-");
        compressionMagicLbl.setText("-");
        compressionInfoVersionLbl.setText("-");
        compressionInfoEntryCountLbl.setText("-");
        compressionInfoUnknownLbl.setText("-");
        compressionInfoReservedLbl.setText("-");
        metaDataHashDataTableOffsetLbl.setText("-");
        metaDataHashDataTableSizeLbl.setText("-");
        metaDataHashDataTableHashLbl.setText("-");
    }

    public void populateTab(NcaFsHeader ncaFsHeader){
        versionLbl.setText(byteArrToHexStringAsLE(ncaFsHeader.getVersion()));
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("0x%02x ", ncaFsHeader.getFsType()));
        if (ncaFsHeader.getFsType() == 0)
            sb.append("(RomFS)");
        else if (ncaFsHeader.getFsType() == 1)
            sb.append("(PFS0)");
        fsTypeLbl.setText(sb.toString());
        sb = new StringBuilder();
        sb.append(String.format("0x%02x ", ncaFsHeader.getHashType()));
        if (ncaFsHeader.getHashType() == 0x3)
            sb.append("(RomFS)");
        else if (ncaFsHeader.getHashType() == 0x2)
            sb.append("(PFS0)");
        hashTypeLbl.setText(sb.toString());
        switch (ncaFsHeader.getCryptoType()){
            case 0: cryptoTypeLbl.setText(String.format("%02x - Auto", ncaFsHeader.getCryptoType()));;
                break;
            case 1: cryptoTypeLbl.setText(String.format("%02x - None", ncaFsHeader.getCryptoType()));;
                break;
            case 2: cryptoTypeLbl.setText(String.format("%02x - AesXts", ncaFsHeader.getCryptoType()));
                break;
            case 3: cryptoTypeLbl.setText(String.format("%02x - AesCtr", ncaFsHeader.getCryptoType()));
                break;
            case 4: cryptoTypeLbl.setText(String.format("%02x - AesCtrEx", ncaFsHeader.getCryptoType()));
                break;
            case 5: cryptoTypeLbl.setText(String.format("%02x - AesCtrSkipLayerHash", ncaFsHeader.getCryptoType()));
                break;
            case 6: cryptoTypeLbl.setText(String.format("%02x - AesCtrExSkipLayerHash", ncaFsHeader.getCryptoType()));
                break;
            default: cryptoTypeLbl.setText(String.format("%02x", ncaFsHeader.getCryptoType()));
        }
        switch (ncaFsHeader.getMetaDataHashType()){
            case 0:
                metaDataHashTypeLbl.setText(String.format("%d - None", ncaFsHeader.getMetaDataHashType()));
                break;
            case 1:
                metaDataHashTypeLbl.setText(String.format("%d - HierarchicalIntegrity", ncaFsHeader.getMetaDataHashType()));
                break;
            default:
                metaDataHashTypeLbl.setText(String.format("%d", ncaFsHeader.getMetaDataHashType()));
        }

        paddingLbl.setText(byteArrToHexStringAsLE(ncaFsHeader.getPadding()));
        if ((ncaFsHeader.getFsType() == 0) && (ncaFsHeader.getHashType() == 0x3)){
            romFsMagicLbl.setText(ncaFsHeader.getSuperBlockIVFC().getMagic());
            romFsMagicNumberLbl.setText(ncaFsHeader.getSuperBlockIVFC().getMagic()
                +(!ncaFsHeader.getSuperBlockIVFC().getMagic().matches("2")? " (OK)":" (wrong magic number)")); // TODO: TEST
            romFsMasterHashSizeLbl.setText(Integer.toString(ncaFsHeader.getSuperBlockIVFC().getMasterHashSize()));
            romFsTotalNumberOfLevelsLbl.setText(Integer.toString(ncaFsHeader.getSuperBlockIVFC().getTotalNumberOfLevels()));
            romFsLvl1OffsetLbl.setText(getCuteDecHexRepresentation(ncaFsHeader.getSuperBlockIVFC().getLvl1Offset()));
            romFsLvl1SizeLbl.setText(getCuteDecHexRepresentation(ncaFsHeader.getSuperBlockIVFC().getLvl1Size()));
            romFsLvl1SBlockSizeLbl.setText(Integer.toString(ncaFsHeader.getSuperBlockIVFC().getLvl1SBlockSize()));
            romFsReserved1Lbl.setText(byteArrToHexStringAsLE(ncaFsHeader.getSuperBlockIVFC().getReserved1()));

            romFsLvl2OffsetLbl.setText(getCuteDecHexRepresentation(ncaFsHeader.getSuperBlockIVFC().getLvl2Offset()));
            romFsLvl2SizeLbl.setText(getCuteDecHexRepresentation(ncaFsHeader.getSuperBlockIVFC().getLvl2Size()));
            romFsLvl2SBlockSizeLbl.setText(Integer.toString(ncaFsHeader.getSuperBlockIVFC().getLvl2SBlockSize()));
            romFsReserved2Lbl.setText(byteArrToHexStringAsLE(ncaFsHeader.getSuperBlockIVFC().getReserved2()));
            
            romFsLvl3OffsetLbl.setText(getCuteDecHexRepresentation(ncaFsHeader.getSuperBlockIVFC().getLvl3Offset()));
            romFsLvl3SizeLbl.setText(getCuteDecHexRepresentation(ncaFsHeader.getSuperBlockIVFC().getLvl3Size()));
            romFsLvl3SBlockSizeLbl.setText(Integer.toString(ncaFsHeader.getSuperBlockIVFC().getLvl3SBlockSize()));
            romFsReserved3Lbl.setText(byteArrToHexStringAsLE(ncaFsHeader.getSuperBlockIVFC().getReserved3()));

            romFsLvl4OffsetLbl.setText(getCuteDecHexRepresentation(ncaFsHeader.getSuperBlockIVFC().getLvl4Offset()));
            romFsLvl4SizeLbl.setText(getCuteDecHexRepresentation(ncaFsHeader.getSuperBlockIVFC().getLvl4Size()));
            romFsLvl4SBlockSizeLbl.setText(Integer.toString(ncaFsHeader.getSuperBlockIVFC().getLvl4SBlockSize()));
            romFsReserved4Lbl.setText(byteArrToHexStringAsLE(ncaFsHeader.getSuperBlockIVFC().getReserved4()));

            romFsLvl5OffsetLbl.setText(getCuteDecHexRepresentation(ncaFsHeader.getSuperBlockIVFC().getLvl5Offset()));
            romFsLvl5SizeLbl.setText(getCuteDecHexRepresentation(ncaFsHeader.getSuperBlockIVFC().getLvl5Size()));
            romFsLvl5SBlockSizeLbl.setText(Integer.toString(ncaFsHeader.getSuperBlockIVFC().getLvl5SBlockSize()));
            romFsReserved5Lbl.setText(byteArrToHexStringAsLE(ncaFsHeader.getSuperBlockIVFC().getReserved5()));

            romFsLvl6OffsetLbl.setText(getCuteDecHexRepresentation(ncaFsHeader.getSuperBlockIVFC().getLvl6Offset()));
            romFsLvl6SizeLbl.setText(getCuteDecHexRepresentation(ncaFsHeader.getSuperBlockIVFC().getLvl6Size()));
            romFsLvl6SBlockSizeLbl.setText(Integer.toString(ncaFsHeader.getSuperBlockIVFC().getLvl6SBlockSize()));
            romFsReserved6Lbl.setText(byteArrToHexStringAsLE(ncaFsHeader.getSuperBlockIVFC().getReserved6()));

            signatureSaltTf.setText(byteArrToHexStringAsLE(ncaFsHeader.getSuperBlockIVFC().getSignatureSalt()));
            romFsMasterHashTf.setText(byteArrToHexStringAsLE(ncaFsHeader.getSuperBlockIVFC().getMasterHash()));
            romFsReservedTailLbl.setText(byteArrToHexStringAsLE(ncaFsHeader.getSuperBlockIVFC().getReservedTail()));

            pfs0TitlePanel.setDisable(true);
        }
        else if ((ncaFsHeader.getFsType() == 0x1) && (ncaFsHeader.getHashType() == 0x2)){
            pfs0SHA256hashTf.setText(byteArrToHexStringAsLE(ncaFsHeader.getSuperBlockPFS0().getSHA256hash()));
            pfs0blockSizeLbl.setText(Integer.toString(ncaFsHeader.getSuperBlockPFS0().getBlockSize()));
            pfs0LayerCountLbl.setText(Integer.toString(ncaFsHeader.getSuperBlockPFS0().getLayerCount()));
            pfs0hashTableOffsetLbl.setText(Long.toString(ncaFsHeader.getSuperBlockPFS0().getHashTableOffset()));
            pfs0hashTableSizeLbl.setText(Long.toString(ncaFsHeader.getSuperBlockPFS0().getHashTableSize()));
            pfs0relativeToSectionStartOffsetLbl.setText(Long.toString(ncaFsHeader.getSuperBlockPFS0().getPfs0offset()));
            pfs0sizePfs0Lbl.setText(Long.toString(ncaFsHeader.getSuperBlockPFS0().getPfs0size()));
            pfs0zeroesTf.setText(byteArrToHexStringAsLE(ncaFsHeader.getSuperBlockPFS0().getZeroes()));
            romFsTitlePanel.setDisable(true);
        }
        else {
            pfs0TitlePanel.setDisable(true);
            romFsTitlePanel.setDisable(true);
        }

        indirectOffsetLbl.setText(Long.toString(ncaFsHeader.getPatchInfoOffsetSection1()));
        indirectSizeLbl.setText(Long.toString(ncaFsHeader.getPatchInfoSizeSection1()));
        indirectInfoMagicLbl.setText(ncaFsHeader.getPatchInfoMagicSection1());
        indirectInfoVersionLbl.setText(Long.toString(ncaFsHeader.getPatchInfoSizeSection1()));
        indirectInfoEntryCountLbl.setText(Integer.toString(ncaFsHeader.getEntryCountSection1()));
        indirectInfoUnknownLbl.setText(byteArrToHexStringAsLE(ncaFsHeader.getPatchInfoUnknownSection1()));
        aesCtrExOffsetLbl.setText(Long.toString(ncaFsHeader.getPatchInfoOffsetSection2()));
        aesCtrExSizeLbl.setText(Long.toString(ncaFsHeader.getPatchInfoSizeSection2()));
        aesCtrExMagicLbl.setText(ncaFsHeader.getPatchInfoMagicSection2());
        aesCtrExVersionLbl.setText(Integer.toString(ncaFsHeader.getPatchInfoVersionSection2()));
        aesCtrExEntryCountLbl.setText(Integer.toString(ncaFsHeader.getEntryCountSection2()));
        aesCtrExUnknownLbl.setText(byteArrToHexStringAsLE(ncaFsHeader.getPatchInfoUnknownSection2()));
        sectionCTRLbl.setText(byteArrToHexStringAsLE(ncaFsHeader.getSectionCTR()));
        generationLbl.setText(byteArrToHexStringAsLE(ncaFsHeader.getGeneration()));

        SparseInfo sparseInfo = ncaFsHeader.getSparseInfo();
        sparseInfoTableOffsetLbl.setText(Long.toString(sparseInfo.getOffset()));
        sparseInfoTableSizeLbl.setText(Long.toString(sparseInfo.getSize()));
        sparseInfoMagicLbl.setText(sparseInfo.getBktrMagic());
        sparseInfoVersionLbl.setText(Long.toString(sparseInfo.getBktrVersion()));
        sparseInfoEntryCountLbl.setText(Integer.toString(sparseInfo.getBktrEntryCount()));
        sparseInfoUnknownLbl.setText(byteArrToHexStringAsLE(sparseInfo.getBktrUnknown()));
        sparseInfoPhysicalOffsetLbl.setText(Long.toString(sparseInfo.getPhysicalOffset()));
        sparseInfoGenerationLbl.setText(byteArrToHexStringAsLE(sparseInfo.getGeneration()));
        sparseInfoReseredLbl.setText(byteArrToHexStringAsLE(sparseInfo.getUnknown()));

        CompressionInfo compressionInfo = ncaFsHeader.getCompressionInfo();
        compressionInfoTableOffsetLbl.setText(Long.toString(compressionInfo.getOffset()));
        compressionInfoTableSizeLbl.setText(Long.toString(compressionInfo.getSize()));
        compressionMagicLbl.setText(compressionInfo.getBktrMagic());
        compressionInfoVersionLbl.setText(Long.toString(compressionInfo.getBktrVersion()));
        compressionInfoEntryCountLbl.setText(Integer.toString(compressionInfo.getBktrEntryCount()));
        compressionInfoUnknownLbl.setText(byteArrToHexStringAsLE(compressionInfo.getBktrUnknown()));
        compressionInfoReservedLbl.setText(byteArrToHexStringAsLE(compressionInfo.getUnknown()));

        MetaDataHashDataInfo metaDataHashDataInfo = ncaFsHeader.getMetaDataHashDataInfo();
        metaDataHashDataTableOffsetLbl.setText(Long.toString(metaDataHashDataInfo.getOffset()));
        metaDataHashDataTableSizeLbl.setText(Long.toString(metaDataHashDataInfo.getSize()));
        metaDataHashDataTableHashLbl.setText(byteArrToHexStringAsLE(metaDataHashDataInfo.getTableHash()));

        unknwnEndPaddingTF.setText(byteArrToHexStringAsLE(ncaFsHeader.getUnknownEndPadding()));
    }

    private String getCuteDecHexRepresentation(long value){
        return String.format("%d (0x%02x)", value, value);
    }
}
