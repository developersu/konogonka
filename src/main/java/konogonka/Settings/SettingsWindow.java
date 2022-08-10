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
package konogonka.Settings;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import konogonka.MainFx;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class SettingsWindow {
    public SettingsWindow(){
        Stage stageSettings = new Stage();

        stageSettings.setMinWidth(570);
        stageSettings.setMinHeight(500);

        FXMLLoader loaderSettings = new FXMLLoader(getClass().getResource("/FXML/Settings/SettingsLayout.fxml"));

        Locale userLocale = new Locale(Locale.getDefault().getISO3Language());

        ResourceBundle resourceBundle = ResourceBundle.getBundle("locale", userLocale);

        loaderSettings.setResources(resourceBundle);

        try {
            Parent parentAbout = loaderSettings.load();

            stageSettings.setTitle(resourceBundle.getString("settings_SettingsName"));

            stageSettings.getIcons().addAll(
                    new Image(MainFx.class.getResourceAsStream("/res/settings_icon32x32.png")),
                    new Image(MainFx.class.getResourceAsStream("/res/settings_icon48x48.png")),
                    new Image(MainFx.class.getResourceAsStream("/res/settings_icon64x64.png")),
                    new Image(MainFx.class.getResourceAsStream("/res/settings_icon128x128.png"))
            );

            Scene settingsScene = new Scene(parentAbout, 800, 800);
            settingsScene.getStylesheets().add("/res/app_light.css");
            stageSettings.setScene(settingsScene);
            stageSettings.setMinWidth(550.0);
            stageSettings.setMinHeight(550.0);
            stageSettings.show();

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
