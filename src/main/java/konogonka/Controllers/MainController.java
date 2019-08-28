package konogonka.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import konogonka.AppPreferences;
import konogonka.Child.ChildWindow;
import konogonka.Controllers.NCA.NCAController;
import konogonka.Controllers.NPDM.NPDMController;
import konogonka.Controllers.NSP.NSPController;
import konogonka.Controllers.TIK.TIKController;
import konogonka.Controllers.XCI.XCIController;
import konogonka.Controllers.XML.XMLController;
import konogonka.MediatorControl;
import konogonka.Settings.SettingsWindow;
import konogonka.Tools.ISuperProvider;
import konogonka.Tools.TIK.TIKProvider;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private SplitPane splitPane;
    @FXML
    private AnchorPane logPane;
    @FXML
    private TabPane tabPane;

    @FXML
    public TextArea logArea;
    @FXML
    public ProgressBar progressBar;
    @FXML
    private Label filenameSelected;
    @FXML
    private Button analyzeBtn, settingsBtn;

    private String previouslyOpenedPath;

    @FXML
    private NSPController NSPTabController;
    @FXML
    private XCIController XCITabController;
    @FXML
    private NCAController NCATabController;
    @FXML
    private TIKController TIKTabController;
    @FXML
    private XMLController XMLTabController;
    @FXML
    private NPDMController NPDMTabController;

    private File selectedFile;

    private ResourceBundle rb;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.rb = resourceBundle;
        MediatorControl.getInstance().setController(this);

        progressBar.setPrefWidth(Double.POSITIVE_INFINITY);
        previouslyOpenedPath = AppPreferences.getInstance().getRecentPath();

        analyzeBtn.setOnAction(e->this.analyzeFile());

        splitPane.getItems().remove(logPane);
        settingsBtn.setOnAction(e->{
            new SettingsWindow();
        });
    }

    /**
     * Functionality for selecting NSP button.
     * */
    public void selectFilesBtnAction(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(rb.getString("btnFileOpen"));

        File validator = new File(previouslyOpenedPath);
        if (validator.exists())
            fileChooser.setInitialDirectory(validator);
        else
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("NS files", "*.nsp", "*.xci", "*.nca", "*.tik", "*.xml", "*.npdm"));

        this.selectedFile = fileChooser.showOpenDialog(analyzeBtn.getScene().getWindow());
        // todo: fix
        analyzeBtn.setDisable(true);
        NSPTabController.resetTab();
        XCITabController.resetTab();
        NCATabController.resetTab();
        TIKTabController.resetTab();
        XMLTabController.resetTab();
        NPDMTabController.resetTab();

        if (this.selectedFile != null && this.selectedFile.exists()) {
            filenameSelected.setText(this.selectedFile.getAbsolutePath());
            previouslyOpenedPath = this.selectedFile.getParent();
            analyzeBtn.setDisable(false);
            if (this.selectedFile.getName().toLowerCase().endsWith(".nsp"))
                tabPane.getSelectionModel().select(0);
            else if (this.selectedFile.getName().toLowerCase().endsWith(".xci"))
                tabPane.getSelectionModel().select(1);
            else if (this.selectedFile.getName().toLowerCase().endsWith(".nca"))
                tabPane.getSelectionModel().select(2);
            else if (this.selectedFile.getName().toLowerCase().endsWith(".tik"))
                tabPane.getSelectionModel().select(3);
            else if (this.selectedFile.getName().toLowerCase().endsWith(".xml"))
                tabPane.getSelectionModel().select(4);
            else if (this.selectedFile.getName().toLowerCase().endsWith(".npdm"))
                tabPane.getSelectionModel().select(5);
        }

        logArea.clear();
    }
    /**
     * Start analyze
     * */
    private void analyzeFile(){
        if (selectedFile.getName().toLowerCase().endsWith("nsp"))
            NSPTabController.analyze(selectedFile);      // TODO: NSP OR XCI
        else if (selectedFile.getName().toLowerCase().endsWith("xci"))
            XCITabController.analyze(selectedFile);
        else if (selectedFile.getName().toLowerCase().endsWith("nca"))
            NCATabController.analyze(selectedFile);
        else if (selectedFile.getName().toLowerCase().endsWith("tik"))
            TIKTabController.analyze(selectedFile);
        else if (selectedFile.getName().toLowerCase().endsWith("xml"))
            XMLTabController.analyze(selectedFile);
        else if (selectedFile.getName().toLowerCase().endsWith("npdm"))
            NPDMTabController.analyze(selectedFile);
    }
    @FXML
    private void showHideLogs(){
        if (splitPane.getItems().size() == 2)
            splitPane.getItems().remove(logPane);
        else
            splitPane.getItems().add(logPane);
    }
    public void showContentWindow(ISuperProvider provider, IRowModel model){
        try{
            new ChildWindow(provider, model);
        }
        catch (IOException e){
            logArea.appendText("\nUnable to create windows for "+model.getFileName()+"\n"+e.getMessage());
        }

    };
    public void exit(){ AppPreferences.getInstance().setRecentPath(previouslyOpenedPath); }
}