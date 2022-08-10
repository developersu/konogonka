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
package konogonka;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import konogonka.Controllers.MainController;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainFx extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/landingPage.fxml"));

        Locale userLocale = new Locale(Locale.getDefault().getISO3Language());      // NOTE: user locale based on ISO3 Language codes
        ResourceBundle rb = ResourceBundle.getBundle("locale", userLocale);

        ResourceBundle rbFiltered = ResourceBundle.getBundle("app", userLocale);


        loader.setResources(rb);

        Parent root = loader.load();

        primaryStage.getIcons().addAll(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/res/app_icon32x32.png"))),
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/res/app_icon48x48.png"))),
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/res/app_icon64x64.png"))),
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/res/app_icon128x128.png")))
        );
        primaryStage.setTitle("konogonka "+rbFiltered.getString("_version"));

        Scene mainScene = new Scene(root, 1200, 800);
        mainScene.getStylesheets().add("/res/app_light.css");

        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(1000.0);
        primaryStage.setMinHeight(750.0);
        primaryStage.show();

        MainController controller = loader.getController();
        primaryStage.setOnHidden(e-> controller.exit());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
