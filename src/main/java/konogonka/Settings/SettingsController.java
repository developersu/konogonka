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
package konogonka.Settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import konogonka.AppPreferences;


import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML
    private Button okBtn, cancelBtn, importKeysBtn, importTitleKeysBtn;

    @FXML
    ListSelectorController
            ListSelectorKAEKAppController,
            ListSelectorKAEKOceanController,
            ListSelectorKAEKSysController,
            ListSelectorTitleKeksController,
            ListSelectorTitleKeysController;

    @FXML
    private TextField
            xciHdrKeyTF,
            hdrKeyTF;

    @FXML
    private Button extractFilesToBtn;
    @FXML
    private Label extractFilesPathLbl;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        extractFilesPathLbl.setText(AppPreferences.getInstance().getExtractFilesDir());
        extractFilesToBtn.setOnAction(e -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Extract files to...");
            dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File dir = dirChooser.showDialog(extractFilesToBtn.getScene().getWindow());
            if (dir != null && dir.exists())
                extractFilesPathLbl.setText(dir.getAbsolutePath());
        });

        ListSelectorKAEKAppController.initSelector(32, "key_area_key_application_");
        ListSelectorKAEKOceanController.initSelector(32, "key_area_key_ocean_");
        ListSelectorKAEKSysController.initSelector(32, "key_area_key_system_");
        ListSelectorTitleKeksController.initSelector(32, "titlekek_");
        ListSelectorTitleKeysController.initSelector(32, null);  // 32 required

        LinkedHashMap<String, String> preparedPairsMapInit = new LinkedHashMap<>();

        for (int i = 0; i < AppPreferences.getInstance().getKAKAppCount(); i++){
            preparedPairsMapInit.put(String.format("key_area_key_application_%02d", i), AppPreferences.getInstance().getApplicationKey(i));
        }
        ListSelectorKAEKAppController.setList(preparedPairsMapInit);
        preparedPairsMapInit.clear();

        for (int i = 0; i < AppPreferences.getInstance().getKAKOceanCount(); i++){
            preparedPairsMapInit.put(String.format("key_area_key_ocean_%02d", i), AppPreferences.getInstance().getOceanKey(i));
        }
        ListSelectorKAEKOceanController.setList(preparedPairsMapInit);
        preparedPairsMapInit.clear();

        for (int i = 0; i < AppPreferences.getInstance().getKAKSysCount(); i++){
            preparedPairsMapInit.put(String.format("key_area_key_system_%02d", i), AppPreferences.getInstance().getSystemKey(i));
        }
        ListSelectorKAEKSysController.setList(preparedPairsMapInit);
        preparedPairsMapInit.clear();

        for (int i = 0; i < AppPreferences.getInstance().getTitleKeksCount(); i++){
            preparedPairsMapInit.put(String.format("titlekek_%02d", i), AppPreferences.getInstance().getTitleKek(i));
        }
        ListSelectorTitleKeksController.setList(preparedPairsMapInit);
        preparedPairsMapInit.clear();

        for (int i = 0; i < AppPreferences.getInstance().getTitleKeysCount(); i++){
            preparedPairsMapInit.put(
                    AppPreferences.getInstance().getTitleKeyPair(i)[0],
                    AppPreferences.getInstance().getTitleKeyPair(i)[1]
            );
        }
        ListSelectorTitleKeysController.setList(preparedPairsMapInit);

        xciHdrKeyTF.setText(AppPreferences.getInstance().getXciHeaderKey());
        hdrKeyTF.setText(AppPreferences.getInstance().getHeaderKey());
        
        setTextValidation(xciHdrKeyTF);
        setTextValidation(hdrKeyTF);

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

                    LinkedHashMap<String, String> kaekSingle = new LinkedHashMap<>();

                    String keyParsed;
                    int counter = 0;
                    while ((keyParsed = fileMap.get(String.format("key_area_key_application_%02d", counter))) != null){
                        kaekSingle.put(String.format("key_area_key_application_%02d", counter), keyParsed);
                        counter++;
                    }
                    ListSelectorKAEKAppController.setList(kaekSingle);

                    kaekSingle.clear();
                    counter = 0;
                    while ((keyParsed = fileMap.get(String.format("key_area_key_ocean_%02d", counter))) != null){
                        kaekSingle.put(String.format("key_area_key_ocean_%02d", counter), keyParsed);
                        counter++;
                    }
                    ListSelectorKAEKOceanController.setList(kaekSingle);

                    kaekSingle.clear();
                    counter = 0;
                    while ((keyParsed = fileMap.get(String.format("key_area_key_system_%02d", counter))) != null){
                        kaekSingle.put(String.format("key_area_key_system_%02d", counter), keyParsed);
                        counter++;
                    }
                    ListSelectorKAEKSysController.setList(kaekSingle);

                    kaekSingle.clear();
                    counter = 0;
                    while ((keyParsed = fileMap.get(String.format("titlekek_%02d", counter))) != null){
                        kaekSingle.put(String.format("titlekek_%02d", counter), keyParsed);
                        counter++;
                    }
                    ListSelectorTitleKeksController.setList(kaekSingle);
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
                    LinkedHashMap<String, String> preparedPairsMap = new LinkedHashMap<>();
                    while ((fileLine = br.readLine()) != null){
                        keyValue = fileLine.trim().split("\\s*=\\s*", 2);
                        if (keyValue.length == 2 && keyValue[0].length() > 16 && ! (keyValue[0].length() > 32) && keyValue[1].length() == 32){
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
            // Saving all keys we have
            AppPreferences.getInstance().setAll(
                    xciHdrKeyTF.getText(),
                    hdrKeyTF.getText()
            );

            String[] kaekAppKeySet = ListSelectorKAEKAppController.getList();
            if (kaekAppKeySet != null){
                for (int i = 0; i < kaekAppKeySet.length; i++)
                    AppPreferences.getInstance().setApplicationKey(i, kaekAppKeySet[i].split("\\s=\\s", 2)[1]);
            }
            if (kaekAppKeySet != null)
                AppPreferences.getInstance().setKAKAppCount(kaekAppKeySet.length);

            String[] kaekOceanKeySet = ListSelectorKAEKOceanController.getList();
            if (kaekOceanKeySet != null){
                for (int i = 0; i < kaekOceanKeySet.length; i++)
                    AppPreferences.getInstance().setOceanKey(i, kaekOceanKeySet[i].split("\\s=\\s", 2)[1]);
            }
            if (kaekOceanKeySet != null)
                AppPreferences.getInstance().setKAKOceanCount(kaekOceanKeySet.length);

            String[] kaekSysKeySet = ListSelectorKAEKSysController.getList();
            if (kaekSysKeySet != null){
                for (int i = 0; i < kaekSysKeySet.length; i++)
                    AppPreferences.getInstance().setSystemKey(i, kaekSysKeySet[i].split("\\s=\\s", 2)[1]);
            }
            if (kaekSysKeySet != null)
                AppPreferences.getInstance().setKAKSysCount(kaekSysKeySet.length);

            String[] titleKekSet = ListSelectorTitleKeksController.getList();
            if (titleKekSet != null){
                for (int i = 0; i < titleKekSet.length; i++)
                    AppPreferences.getInstance().setTitleKek(i, titleKekSet[i].split("\\s=\\s", 2)[1]);
            }
            if (titleKekSet != null)
                AppPreferences.getInstance().setTitleKeksCount(titleKekSet.length);

            String[] titleKeysSet = ListSelectorTitleKeysController.getList();
            if (titleKeysSet != null){
                AppPreferences.getInstance().setTitleKeysCount(titleKeysSet.length);
                for (int i = 0; i < titleKeysSet.length; i++)
                    AppPreferences.getInstance().setTitleKey(i, titleKeysSet[i]);
            }
            AppPreferences.getInstance().setExtractFilesDir(extractFilesPathLbl.getText());
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