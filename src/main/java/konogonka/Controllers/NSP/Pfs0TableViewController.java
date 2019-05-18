package konogonka.Controllers.NSP;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import konogonka.Controllers.IRowModel;
import konogonka.Tools.PFS0.PFS0Provider;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Pfs0TableViewController implements Initializable {
    @FXML
    private TableView<Pfs0RowModel> table;
    private ObservableList<Pfs0RowModel> rowsObsLst;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rowsObsLst = FXCollections.observableArrayList();

        table.setPlaceholder(new Label());
        table.setEditable(false);               // At least with hacks it works as expected. Otherwise - null pointer exception
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (!rowsObsLst.isEmpty()) {
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        for (Pfs0RowModel item : table.getSelectionModel().getSelectedItems()) {
                            item.setMarkSelected(!item.isMarkSelected());
                        }
                        table.refresh();
                    }
                }
                keyEvent.consume();
            }
        });

        TableColumn<Pfs0RowModel, Integer> numberColumn = new TableColumn<>(resourceBundle.getString("tableNumberLbl"));
        TableColumn<Pfs0RowModel, String> fileNameColumn = new TableColumn<>(resourceBundle.getString("tableFileNameLbl"));
        TableColumn<Pfs0RowModel, Long> fileOffsetColumn = new TableColumn<>(resourceBundle.getString("tableOffsetLbl"));
        TableColumn<Pfs0RowModel, Long> fileSizeColumn = new TableColumn<>(resourceBundle.getString("tableSizeLbl"));
        TableColumn<Pfs0RowModel, Boolean> uploadColumn = new TableColumn<>(resourceBundle.getString("tableUploadLbl"));

        numberColumn.setEditable(false);
        fileNameColumn.setEditable(false);
        fileOffsetColumn.setEditable(false);
        fileSizeColumn.setEditable(false);
        uploadColumn.setEditable(true);

        // See https://bugs.openjdk.java.net/browse/JDK-8157687
        numberColumn.setMinWidth(30.0);
        numberColumn.setPrefWidth(30.0);
        numberColumn.setMaxWidth(30.0);
        numberColumn.setResizable(false);

        fileNameColumn.setMinWidth(25.0);

        fileOffsetColumn.setMinWidth(130.0);
        fileOffsetColumn.setPrefWidth(130.0);
        fileOffsetColumn.setMaxWidth(130.0);
        fileOffsetColumn.setResizable(false);

        fileSizeColumn.setMinWidth(120.0);
        fileSizeColumn.setPrefWidth(120.0);
        fileSizeColumn.setMaxWidth(120.0);
        fileSizeColumn.setResizable(false);

        uploadColumn.setMinWidth(120.0);
        uploadColumn.setPrefWidth(120.0);
        uploadColumn.setMaxWidth(120.0);
        uploadColumn.setResizable(false);

        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        fileOffsetColumn.setCellValueFactory(new PropertyValueFactory<>("fileOffset"));
        // ><
        uploadColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pfs0RowModel, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Pfs0RowModel, Boolean> paramFeatures) {
                Pfs0RowModel model = paramFeatures.getValue();

                SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(model.isMarkSelected());

                booleanProperty.addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                        model.setMarkSelected(newValue);
                        table.refresh();
                    }
                });
                return booleanProperty;
            }
        });

        uploadColumn.setCellFactory(new Callback<TableColumn<Pfs0RowModel, Boolean>, TableCell<Pfs0RowModel, Boolean>>() {
            @Override
            public TableCell<Pfs0RowModel, Boolean> call(TableColumn<Pfs0RowModel, Boolean> paramFeatures) {
                CheckBoxTableCell<Pfs0RowModel, Boolean> cell = new CheckBoxTableCell<>();
                return cell;
            }
        });
        table.setRowFactory(        // this shit is made to implement context menu. It's such a pain..
                new Callback<TableView<Pfs0RowModel>, TableRow<Pfs0RowModel>>() {
                    @Override
                    public TableRow<Pfs0RowModel> call(TableView<Pfs0RowModel> nslRowModelTableView) {
                        final TableRow<Pfs0RowModel> row = new TableRow<>();
                        row.setOnMouseClicked(new EventHandler<MouseEvent>() {      // Just.. don't ask..
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                if (!row.isEmpty() && mouseEvent.getButton() == MouseButton.PRIMARY){
                                    Pfs0RowModel thisItem = row.getItem();
                                    thisItem.setMarkSelected(!thisItem.isMarkSelected());
                                    table.refresh();
                                }
                                mouseEvent.consume();
                            }
                        });
                        return row;
                    }
                }
        );
        table.setItems(rowsObsLst);
        table.getColumns().addAll(numberColumn, fileNameColumn, fileOffsetColumn, fileSizeColumn,  uploadColumn);
    }
    /**
     * Add files when user selected them
     * */
    public void setNSPToTable(PFS0Provider pfs){
        rowsObsLst.clear();
        Pfs0RowModel.resetNumCnt();
        if (pfs == null) {
            table.refresh();
            return;
        }

        for (int i=0; i < pfs.getFilesCount(); i++){
            rowsObsLst.add(new Pfs0RowModel(
                    pfs.getPfs0subFiles()[i].getName(),
                    pfs.getPfs0subFiles()[i].getSize(),
                    pfs.getPfs0subFiles()[i].getOffset()
            ));
        }
        table.refresh();
    }
    /**
     * Return files ready for upload. Requested from NSLMainController only -> uploadBtnAction()                            //TODO: set undefined
     * @return null if no files marked for upload
     *         List<File> if there are files
     * */
    public List<IRowModel> getFilesForDump(){
        List<IRowModel> models = new ArrayList<>();
        if (rowsObsLst.isEmpty())
            return null;
        for (Pfs0RowModel model: rowsObsLst) {
            if (model.isMarkSelected()) {
                models.add(model);
            }
        }
        return models;
    }
}