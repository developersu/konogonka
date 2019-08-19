package konogonka.Settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import konogonka.ServiceWindow;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

public class ListSelectorController implements Initializable {
    @FXML
    private ListView<String> listView;
    @FXML
    private TextField newRecordName, newRecordValue;
    private ObservableList<String> observableList;

    private int mandatoryValueLength;
    private String predictionPattern;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        observableList = FXCollections.observableArrayList();
        listView.setItems(observableList);

        newRecordValue.setTextFormatter(new TextFormatter<Object>(change -> {
            if (change.getControlNewText().matches("^[a-fA-F0-9]+$") || change.getControlNewText().isEmpty())
                return change;
            return null;
        }));
    }
    public void initSelector(int length, String predictionPattern){
        this.mandatoryValueLength = length;

        if (predictionPattern == null){                                                                         // If we need no validation, restrict only spaces/tabs adding
            newRecordName.setTextFormatter(new TextFormatter<Object>(change -> {
                if (change.getControlNewText().contains(" ") || change.getControlNewText().contains("\t"))
                    return null;
                return change;
            }));
        }
        else {                                                                                                  // Otherwise use pattern as name of key + 2 dec numbers
            this.predictionPattern = predictionPattern;

            newRecordName.setText(predictionPattern);
            newRecordName.setTextFormatter(new TextFormatter<Object>(change -> {
                if (change.getControlNewText().matches("^"+predictionPattern+"[0-9]{0,2}$"))
                    return change;
                return null;
            }));
        }
    }
    /**
     * Must be run on start
     * Set list content
     */
    void setList(LinkedHashMap<String, String> stringPairsArray){
        if (stringPairsArray != null && ! stringPairsArray.isEmpty())
            for (String name: stringPairsArray.keySet())
                validateAndAdd(name+" = "+stringPairsArray.get(name));
    }
    /**
     * Return list content
     * */
    String[] getList(){
        return Arrays.copyOf(observableList.toArray(), observableList.toArray().length, String[].class);
    }

    @FXML
    private void listKeyPressed(KeyEvent event){
        if (event.getCode().toString().equals("DELETE"))
            removeRecord();
    }

    @FXML
    private void removeRecord(){ observableList.remove(listView.getSelectionModel().getSelectedItem()); }

    @FXML
    private void addNewRecord(){
        if (newRecordValue.getText().length() == mandatoryValueLength && ! newRecordName.getText().isEmpty()) {
            if (predictionPattern == null) {
                validateAndAdd(newRecordName.getText() + " = " + newRecordValue.getText());
                newRecordName.clear();
                newRecordValue.clear();
            }
            else {
                if (newRecordName.getText().matches("^"+predictionPattern+"[0-9]{2}$")){
                    validateAndAdd(newRecordName.getText() + " = " + newRecordValue.getText());
                    newRecordName.setText(predictionPattern);
                    newRecordValue.clear();
                }
                else
                    ServiceWindow.getErrorNotification("Error", "Value name should be: '"+predictionPattern+"XX' where XX are two decimal numbers.");
            }
        }
        else {
            ServiceWindow.getErrorNotification("Error", "One of the fields empty or value leigh is incorrect.");
        }
    }

    private void validateAndAdd(String addingItem){
        if (!observableList.contains(addingItem))
            observableList.add(addingItem);
    }
}
