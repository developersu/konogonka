package konogonka.Controllers.NSP;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import konogonka.Controllers.IRowModel;
import konogonka.Controllers.TabController;
import konogonka.MediatorControl;
import konogonka.Tools.ISuperProvider;
import konogonka.Tools.PFS0.IPFS0Provider;
import konogonka.Tools.PFS0.PFS0Provider;
import konogonka.Workers.AnalyzerNSP;
import konogonka.Workers.NspXciExtractor;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static konogonka.LoperConverter.byteArrToHexString;

public class NSPController implements TabController {

    @FXML
    private Button extractBtn;
    @FXML
    private Pfs0TableViewController tableFilesListController;
    @FXML
    private Label filesCountLbl,
            RawDataStartLbl,
            NSPSizeLbl,
            magicLbl,
            stringTableSizeLbl,
            paddingLbl,
            fileEntryTableSizeLbl,
            stringsTableSizeLbl,
            stringsTableOffsetLbl,
            rawFileDataOffsetLbl;

    private long rawFileDataStart;

    private File selectedFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        extractBtn.setOnAction(e->this.extractFiles());
    }

    private void extractFiles(){
        List<IRowModel> models = tableFilesListController.getFilesForDump();
        ISuperProvider provider = tableFilesListController.getProvider();
        if (models != null && !models.isEmpty() && (provider != null)){

            File dir = new File(System.getProperty("user.dir")+File.separator+selectedFile.getName()+" extracted");
            try {
                dir.mkdir();
            }
            catch (SecurityException se){
                MediatorControl.getInstance().getContoller().logArea.setText("Can't create dir to store files.");
            }
            if (!dir.exists())
                return;

            extractBtn.setDisable(true);

            //NspXciExtractor extractor = new NspXciExtractor(rawFileDataStart, models, dir.getAbsolutePath()+File.separator, selectedFile); //TODO: REMOVE
            NspXciExtractor extractor = new NspXciExtractor(provider, models, dir.getAbsolutePath()+File.separator);
            extractor.setOnSucceeded(e->{
                extractBtn.setDisable(false);
            });
            Thread workThread = new Thread(extractor);
            workThread.setDaemon(true);
            workThread.start();
        }
    }
    @Override
    public void resetTab(){
        tableFilesListController.setNSPToTable(null);
        filesCountLbl.setText("-");
        RawDataStartLbl.setText("-");
        NSPSizeLbl.setText("-");
        magicLbl.setText("-");
        stringTableSizeLbl.setText("-");
        paddingLbl.setText("-");
        fileEntryTableSizeLbl.setText("X");
        stringsTableSizeLbl.setText("Y");
        stringsTableOffsetLbl.setText("0x10+X");
        rawFileDataOffsetLbl.setText("0x10+X+Y");
        selectedFile = null;
    }
    /**
     * Start analyze NSP
     * */
    @Override
    public void analyze(File selectedFile){
        this.selectedFile = selectedFile;
        AnalyzerNSP analyzerNSP = new AnalyzerNSP(selectedFile);
        analyzerNSP.setOnSucceeded(e->{
            PFS0Provider pfs0 = analyzerNSP.getValue();
            this.setData(pfs0, null);
        });
        Thread workThread = new Thread(analyzerNSP);
        workThread.setDaemon(true);
        workThread.start();
    }
    /**
     * Just populate fields by already analyzed PFS0
     * */
    public void setData(IPFS0Provider pfs0, File fileWithNca){
        if (pfs0 != null){
            if (fileWithNca != null)
                this.selectedFile = fileWithNca;
            filesCountLbl.setText(Integer.toString(pfs0.getFilesCount()));
            RawDataStartLbl.setText(Long.toString(pfs0.getRawFileDataStart()));
            rawFileDataStart = pfs0.getRawFileDataStart();
            tableFilesListController.setNSPToTable(pfs0);
            if (fileWithNca != null)
                NSPSizeLbl.setText("skipping calculation for in-file PFS0");
            else
                NSPSizeLbl.setText(Long.toString(selectedFile.length()));

            extractBtn.setDisable(false);
            magicLbl.setText(pfs0.getMagic());
            stringTableSizeLbl.setText(Integer.toString(pfs0.getStringTableSize()));
            paddingLbl.setText(byteArrToHexString(pfs0.getPadding()));

            fileEntryTableSizeLbl.setText(String.format("0x%02x", 0x18* pfs0.getFilesCount()));
            stringsTableSizeLbl.setText(String.format("0x%02x", pfs0.getStringTableSize()));
            stringsTableOffsetLbl.setText(String.format("0x%02x", 0x18* pfs0.getFilesCount()+0x10));
            rawFileDataOffsetLbl.setText(String.format("0x%02x", 0x18* pfs0.getFilesCount()+0x10+ pfs0.getStringTableSize()));  // same to RawFileDataStart for NSP ONLY
        }
    }
}
