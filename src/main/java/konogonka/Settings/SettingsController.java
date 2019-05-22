package konogonka.Settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import konogonka.AppPreferences;


import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML
    private Button okBtn, cancelBtn, importKeysBtn, importTitleKeysBtn;

    @FXML
    ListSelectorController ListSelectorTitleKeysController;

    @FXML
    private TextField
            xciHdrKeyTF,
            hdrKeyTF,
            keyApp0TF,
            keyApp1TF,
            keyApp2TF,
            keyApp3TF,
            keyApp4TF,
            keyApp5TF,
            keyApp6TF,
            keyApp7TF,
            keyOcean0TF,
            keyOcean1TF,
            keyOcean2TF,
            keyOcean3TF,
            keyOcean4TF,
            keyOcean5TF,
            keyOcean6TF,
            keyOcean7TF,
            keySys0TF,
            keySys1TF,
            keySys2TF,
            keySys3TF,
            keySys4TF,
            keySys5TF,
            keySys6TF,
            keySys7TF;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ListSelectorTitleKeysController.initSelector(32, null);  // 32 required

        HashMap<String, String> preparedPairsMapInit = new HashMap<>();
        for (int i = 0; i < AppPreferences.getInstance().getTitleKeysCount(); i++){
            preparedPairsMapInit.put(
                    AppPreferences.getInstance().getTitleKeyPair(i)[0],
                    AppPreferences.getInstance().getTitleKeyPair(i)[1]
            );
        }
        ListSelectorTitleKeysController.setList(preparedPairsMapInit);

        xciHdrKeyTF.setText(AppPreferences.getInstance().getXciHeaderKey());
        hdrKeyTF.setText(AppPreferences.getInstance().getHeaderKey());
        keyApp0TF.setText(AppPreferences.getInstance().getApplicationKey(0));
        keyApp1TF.setText(AppPreferences.getInstance().getApplicationKey(1));
        keyApp2TF.setText(AppPreferences.getInstance().getApplicationKey(2));
        keyApp3TF.setText(AppPreferences.getInstance().getApplicationKey(3));
        keyApp4TF.setText(AppPreferences.getInstance().getApplicationKey(4));
        keyApp5TF.setText(AppPreferences.getInstance().getApplicationKey(5));
        keyApp6TF.setText(AppPreferences.getInstance().getApplicationKey(6));
        keyApp7TF.setText(AppPreferences.getInstance().getApplicationKey(7));
        keyOcean0TF.setText(AppPreferences.getInstance().getOceanKey(0));
        keyOcean1TF.setText(AppPreferences.getInstance().getOceanKey(1));
        keyOcean2TF.setText(AppPreferences.getInstance().getOceanKey(2));
        keyOcean3TF.setText(AppPreferences.getInstance().getOceanKey(3));
        keyOcean4TF.setText(AppPreferences.getInstance().getOceanKey(4));
        keyOcean5TF.setText(AppPreferences.getInstance().getOceanKey(5));
        keyOcean6TF.setText(AppPreferences.getInstance().getOceanKey(6));
        keyOcean7TF.setText(AppPreferences.getInstance().getOceanKey(7));
        keySys0TF.setText(AppPreferences.getInstance().getSystemKey(0));
        keySys1TF.setText(AppPreferences.getInstance().getSystemKey(1));
        keySys2TF.setText(AppPreferences.getInstance().getSystemKey(2));
        keySys3TF.setText(AppPreferences.getInstance().getSystemKey(3));
        keySys4TF.setText(AppPreferences.getInstance().getSystemKey(4));
        keySys5TF.setText(AppPreferences.getInstance().getSystemKey(5));
        keySys6TF.setText(AppPreferences.getInstance().getSystemKey(6));
        keySys7TF.setText(AppPreferences.getInstance().getSystemKey(7));
        
        setTextValidation(xciHdrKeyTF);
        setTextValidation(hdrKeyTF);
        setTextValidation(keyApp0TF);
        setTextValidation(keyApp1TF);
        setTextValidation(keyApp2TF);
        setTextValidation(keyApp3TF);
        setTextValidation(keyApp4TF);
        setTextValidation(keyApp5TF);
        setTextValidation(keyApp6TF);
        setTextValidation(keyApp7TF);
        setTextValidation(keyOcean0TF);
        setTextValidation(keyOcean1TF);
        setTextValidation(keyOcean2TF);
        setTextValidation(keyOcean3TF);
        setTextValidation(keyOcean4TF);
        setTextValidation(keyOcean5TF);
        setTextValidation(keyOcean6TF);
        setTextValidation(keyOcean7TF);
        setTextValidation(keySys0TF);
        setTextValidation(keySys1TF);
        setTextValidation(keySys2TF);
        setTextValidation(keySys3TF);
        setTextValidation(keySys4TF);
        setTextValidation(keySys5TF);
        setTextValidation(keySys6TF);
        setTextValidation(keySys7TF);

        importKeysBtn.setOnAction(e->{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("prod.keys");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("prod.keys", "prod.keys"));

            File prodKeysFile = fileChooser.showOpenDialog(importKeysBtn.getScene().getWindow());

            if (prodKeysFile != null && prodKeysFile.exists()) {
                HashMap<String, String > fileMap = new HashMap<>();
                try {
                    BufferedReader br = new BufferedReader(
                            new FileReader(prodKeysFile)
                    );

                    String fileLine;
                    String[] keyValue;
                    while ((fileLine = br.readLine()) != null){
                        keyValue = fileLine.trim().split("\\s+?=\\s+?", 2);
                        if (keyValue.length == 2)
                            fileMap.put(keyValue[0], keyValue[1]);
                    }
                    hdrKeyTF.setText(fileMap.get("header_key"));
                    keyApp0TF.setText(fileMap.get("key_area_key_application_00"));
                    keyApp1TF.setText(fileMap.get("key_area_key_application_01"));
                    keyApp2TF.setText(fileMap.get("key_area_key_application_02"));
                    keyApp3TF.setText(fileMap.get("key_area_key_application_03"));
                    keyApp4TF.setText(fileMap.get("key_area_key_application_04"));
                    keyApp5TF.setText(fileMap.get("key_area_key_application_05"));
                    keyApp6TF.setText(fileMap.get("key_area_key_application_06"));
                    keyApp7TF.setText(fileMap.get("key_area_key_application_07"));
                    keyOcean0TF.setText(fileMap.get("key_area_key_ocean_00"));
                    keyOcean1TF.setText(fileMap.get("key_area_key_ocean_01"));
                    keyOcean2TF.setText(fileMap.get("key_area_key_ocean_02"));
                    keyOcean3TF.setText(fileMap.get("key_area_key_ocean_03"));
                    keyOcean4TF.setText(fileMap.get("key_area_key_ocean_04"));
                    keyOcean5TF.setText(fileMap.get("key_area_key_ocean_05"));
                    keyOcean6TF.setText(fileMap.get("key_area_key_ocean_06"));
                    keyOcean7TF.setText(fileMap.get("key_area_key_ocean_07"));
                    keySys0TF.setText(fileMap.get("key_area_key_system_00"));
                    keySys1TF.setText(fileMap.get("key_area_key_system_01"));
                    keySys2TF.setText(fileMap.get("key_area_key_system_02"));
                    keySys3TF.setText(fileMap.get("key_area_key_system_03"));
                    keySys4TF.setText(fileMap.get("key_area_key_system_04"));
                    keySys5TF.setText(fileMap.get("key_area_key_system_05"));
                    keySys6TF.setText(fileMap.get("key_area_key_system_06"));
                    keySys7TF.setText(fileMap.get("key_area_key_system_07"));
                }
                catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        });

        importTitleKeysBtn.setOnAction(e->{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("title.keys");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("title.keys", "title.keys"));

            File prodKeysFile = fileChooser.showOpenDialog(importKeysBtn.getScene().getWindow());

            if (prodKeysFile != null && prodKeysFile.exists()) {
                try {
                    BufferedReader br = new BufferedReader(
                            new FileReader(prodKeysFile)
                    );

                    String fileLine;
                    String[] keyValue;
                    HashMap<String, String> preparedPairsMap = new HashMap<>();
                    while ((fileLine = br.readLine()) != null){
                        keyValue = fileLine.trim().split("\\s+?=\\s+?", 2);
                        if (keyValue.length == 2 && keyValue[0].length() == 32 && keyValue[1].length() == 32){
                            preparedPairsMap.put(keyValue[0], keyValue[1]);
                        }
                    }
                    ListSelectorTitleKeysController.setList(preparedPairsMap);
                }
                catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        });

        cancelBtn.setOnAction(e->{
            Stage thisStage = (Stage)cancelBtn.getScene().getWindow();
            thisStage.close();
        });

        okBtn.setOnAction(e->{
            Stage thisStage = (Stage)cancelBtn.getScene().getWindow();
            AppPreferences.getInstance().setAll(
                    xciHdrKeyTF.getText(),
                    hdrKeyTF.getText(),
                    keyApp0TF.getText(),
                    keyApp1TF.getText(),
                    keyApp2TF.getText(),
                    keyApp3TF.getText(),
                    keyApp4TF.getText(),
                    keyApp5TF.getText(),
                    keyApp6TF.getText(),
                    keyApp7TF.getText(),
                    keyOcean0TF.getText(),
                    keyOcean1TF.getText(),
                    keyOcean2TF.getText(),
                    keyOcean3TF.getText(),
                    keyOcean4TF.getText(),
                    keyOcean5TF.getText(),
                    keyOcean6TF.getText(),
                    keyOcean7TF.getText(),
                    keySys0TF.getText(),
                    keySys1TF.getText(),
                    keySys2TF.getText(),
                    keySys3TF.getText(),
                    keySys4TF.getText(),
                    keySys5TF.getText(),
                    keySys6TF.getText(),
                    keySys7TF.getText()
            );
            String[] titleKeysSet = ListSelectorTitleKeysController.getList();
            if (titleKeysSet != null){
                AppPreferences.getInstance().setTitleKeysCount(titleKeysSet.length);
                for (int i = 0; i < titleKeysSet.length; i++)
                    AppPreferences.getInstance().setTitleKey(i, titleKeysSet[i]);
            }
            thisStage.close();
        });
    }
    
    private void setTextValidation(TextField tf){
        tf.setTextFormatter(new TextFormatter<Object>(change -> {
            if (change.getControlNewText().contains(" ") || change.getControlNewText().contains("\t"))
                return null;
            return change;
        }));
    }
}