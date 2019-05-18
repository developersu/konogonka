package konogonka;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import konogonka.Controllers.MainController;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainFx extends Application {
    public static final String appVersion = "v0.1-DEV";

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/landingPage.fxml"));

        Locale userLocale = new Locale(Locale.getDefault().getISO3Language());      // NOTE: user locale based on ISO3 Language codes
        ResourceBundle rb = ResourceBundle.getBundle("locale", userLocale);

        loader.setResources(rb);

        Parent root = loader.load();

        primaryStage.getIcons().addAll(
                new Image(getClass().getResourceAsStream("/res/app_icon32x32.png")),
                new Image(getClass().getResourceAsStream("/res/app_icon48x48.png")),
                new Image(getClass().getResourceAsStream("/res/app_icon64x64.png")),
                new Image(getClass().getResourceAsStream("/res/app_icon128x128.png"))
        );
        primaryStage.setTitle("konogonka "+appVersion);

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
