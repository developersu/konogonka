/*
    Copyright 2019-2020 Dmitry Isaenko

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
package konogonka.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import konogonka.AppPreferences;
import konogonka.Child.ChildWindow;
import konogonka.Controllers.NCA.NCAController;
import konogonka.Controllers.NPDM.NPDMController;
import konogonka.Controllers.NSP.NSPController;
import konogonka.Controllers.RFS.RomFsController;
import konogonka.Controllers.TIK.TIKController;
import konogonka.Controllers.XCI.XCIController;
import konogonka.Controllers.XML.XMLController;
import konogonka.MediatorControl;
import konogonka.Settings.SettingsWindow;
import konogonka.Tools.ISuperProvider;

import java.io.*;
import java.net.URL;
import java.util.List;
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
    @FXML
    private RomFsController RFSTabController;

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

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("NS files",
                "*.nsp", "*.nsz", "*.xci", "*.nca", "*.tik", "*.xml", "*.npdm", "*.romfs"));

        this.selectedFile = fileChooser.showOpenDialog(analyzeBtn.getScene().getWindow());

        if (this.selectedFile != null && this.selectedFile.exists()) {
            resetAllTabsContent();
            filenameSelected.setText(this.selectedFile.getAbsolutePath());
            previouslyOpenedPath = this.selectedFile.getParent();
            analyzeBtn.setDisable(false);
            String fileExtension = this.selectedFile.getName().toLowerCase().replaceAll("^.*\\.", "");
            setFocusOnPane(fileExtension);
        }

        logArea.clear();
    }
    private void resetAllTabsContent(){
        analyzeBtn.setDisable(true);
        NSPTabController.resetTab();
        XCITabController.resetTab();
        NCATabController.resetTab();
        TIKTabController.resetTab();
        XMLTabController.resetTab();
        NPDMTabController.resetTab();
        RFSTabController.resetTab();
    }
    private void setFocusOnPane(String fileExtension){
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
            case "tik":
                tabPane.getSelectionModel().select(3);
                break;
            case "xml":
                tabPane.getSelectionModel().select(4);
                break;
            case "npdm":
                tabPane.getSelectionModel().select(5);
                break;
            case "romfs":
                tabPane.getSelectionModel().select(6);
        }
    }
    /**
     * Start analyze
     * */
    private void analyzeFile(){
        final String fileExtension = selectedFile.getName().toLowerCase().replaceAll("^.*\\.", "");
        switch (fileExtension){
            case "nsp":
            case "nsz":
                NSPTabController.analyze(selectedFile);      // TODO: NSP OR NSZ ?
                break;
            case "xci":
                XCITabController.analyze(selectedFile);
                break;
            case "nca":
                NCATabController.analyze(selectedFile);
                break;
            case "tik":
                TIKTabController.analyze(selectedFile);
                break;
            case "xml":
                XMLTabController.analyze(selectedFile);
                break;
            case "npdm":
                NPDMTabController.analyze(selectedFile);
                break;
            case "romfs":
                RFSTabController.analyze(selectedFile);
        }
    }
    private boolean isNotSupportedFileFormat(String fileExtension){
        switch (fileExtension){
            case "nsp":
            case "nsz":
            case "xci":
            case "nca":
            case "tik":
            case "xml":
            case "npdm":
            case "romfs":
                return false;
            default:
                return true;
        }
    }
    @FXML
    private void showHideLogs(){
        if (splitPane.getItems().size() == 2)
            splitPane.getItems().remove(logPane);
        else
            splitPane.getItems().add(logPane);
    }
    /**
     * Drag-n-drop support (dragOver consumer)
     * */
    @FXML
    private void handleDragOver(DragEvent event){
        event.acceptTransferModes(TransferMode.ANY);

        event.consume();
    }
    /**
     * Drag-n-drop support (drop consumer)
     * */
    @FXML
    private void handleDrop(DragEvent event){
        List<File> filesDropped = event.getDragboard().getFiles();

        if ( filesDropped.isEmpty() ) {
            event.setDropCompleted(true);
            event.consume();
            return;
        }

        File droppedFile = filesDropped.get(0);

        String fileExtension = droppedFile.getName().toLowerCase().replaceAll("^.*\\.", "");

        if (isNotSupportedFileFormat(fileExtension)) {
            event.setDropCompleted(true);
            event.consume();
            return;
        }

        selectedFile = droppedFile;

        resetAllTabsContent();
        filenameSelected.setText(selectedFile.getAbsolutePath());
        previouslyOpenedPath = selectedFile.getParent();
        analyzeBtn.setDisable(false);
        setFocusOnPane(fileExtension);

        event.setDropCompleted(true);
        event.consume();
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