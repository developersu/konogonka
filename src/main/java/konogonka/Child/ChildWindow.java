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
package konogonka.Child;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import konogonka.Controllers.IRowModel;
import konogonka.Controllers.ITabController;
import konogonka.Controllers.XML.XMLController;
import libKonogonka.Tools.ISuperProvider;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class ChildWindow {
    public ChildWindow(ISuperProvider provider, IRowModel model) throws IOException {
        Stage stageSettings = new Stage();

        stageSettings.setMinWidth(570);
        stageSettings.setMinHeight(500);

        FXMLLoader loaderSettings;

        if (model.getFileName().endsWith(".nca")) {
            loaderSettings = new FXMLLoader(getClass().getResource("/FXML/NCA/NCATab.fxml"));
        } else if (model.getFileName().endsWith(".tik")) {
            loaderSettings = new FXMLLoader(getClass().getResource("/FXML/TIK/TIKTab.fxml"));
        } else if (model.getFileName().endsWith(".xml")) {
            loaderSettings = new FXMLLoader(getClass().getResource("/FXML/XML/XMLTab.fxml"));
        }
        else if(model.getFileName().endsWith(".npdm")){
            loaderSettings = new FXMLLoader(getClass().getResource("/FXML/NPDM/NPDMTab.fxml"));
        }
        else if(model.getFileName().endsWith(".cert")){
            // TODO: IMPLEMENT
            return;
        }
        else if(model.getFileName().endsWith(".cnmt")){
            // todo: implement
            return;
        }
        else        // TODO: Dynamic detection function
            return;

        Locale userLocale = new Locale(Locale.getDefault().getISO3Language());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("locale", userLocale);
        loaderSettings.setResources(resourceBundle);
        Parent parentAbout = loaderSettings.load();

        // TODO: fix?
        if(model.getFileName().endsWith(".xml")){
            XMLController myController = loaderSettings.getController();
            myController.analyze(provider.getFile(), provider.getRawFileDataStart()+model.getFileOffset(), model.getFileSize());
        }
        else if (model.getFileName().endsWith(".npdm")){
            ITabController myController = loaderSettings.getController();
            try {
                myController.analyze(provider, model.getNumber());
            }
            catch (Exception e){
                System.out.println("ERR"+e.getMessage());
            }
        }
        else {
            ITabController myController = loaderSettings.getController();
            myController.analyze(provider.getFile(), provider.getRawFileDataStart()+model.getFileOffset());
        }


        stageSettings.setTitle(model.getFileName());
        stageSettings.getIcons().addAll(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/res/app_icon32x32.png"))),
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/res/app_icon48x48.png"))),
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/res/app_icon64x64.png"))),
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/res/app_icon128x128.png")))
        );
        Scene settingsScene = new Scene(parentAbout, 800, 800);
        settingsScene.getStylesheets().add("/res/app_light.css");
        stageSettings.setScene(settingsScene);
        stageSettings.setMinWidth(550.0);
        stageSettings.setMinHeight(550.0);
        stageSettings.show();
    }
}
