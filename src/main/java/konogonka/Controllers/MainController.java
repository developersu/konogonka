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

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("NS files", "*.nsp", "*.nsz", "*.xci", "*.nca", "*.tik", "*.xml", "*.npdm"));

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
            String fileExtension = this.selectedFile.getName().toLowerCase().replaceAll("^.*\\.", "");
            switch (fileExtension){
                case "nsp":
                case "nsz":
                    tabPane.getSelectionModel().select(0);
                    break;
                case "xci":
                    tabPane.getSelectionModel().select(1);
                    break;
                case "nca":
                    tabPane.getSelectionModel().select(2);
                    break;
                case "tic":
                    tabPane.getSelectionModel().select(3);
                    break;
                case "xml":
                    tabPane.getSelectionModel().select(4);
                    break;
                case "npdm":
                    tabPane.getSelectionModel().select(5);
                    break;
            }
        }

        logArea.clear();
    }
    /**
     * Start analyze
     * */
    private void analyzeFile(){
        final String fileExtension = selectedFile.getName().toLowerCase().replaceAll("^.*\\.", "");
        switch (fileExtension){
            case "nsp":
            case "nsz":
                NSPTabController.analyze(selectedFile);      // TODO: NSP OR XCI
                break;
            case "xci":
                XCITabController.analyze(selectedFile);
                break;
            case "nca":
                NCATabController.analyze(selectedFile);
                break;
            case "tic":
                TIKTabController.analyze(selectedFile);
                break;
            case "xml":
                XMLTabController.analyze(selectedFile);
                break;
            case "npdm":
                NPDMTabController.analyze(selectedFile);
                break;
        }
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