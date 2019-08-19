package konogonka.Child;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import konogonka.Controllers.IRowModel;
import konogonka.Controllers.NCA.NCAController;
import konogonka.Tools.ISuperProvider;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class ChildWindow {
    public ChildWindow(ISuperProvider provider, IRowModel model) throws IOException{
        Stage stageSettings = new Stage();

        stageSettings.setMinWidth(570);
        stageSettings.setMinHeight(500);

        FXMLLoader loaderSettings;

        if (model.getFileName().endsWith(".nca")){
            loaderSettings = new FXMLLoader(getClass().getResource("/FXML/NCA/NCATab.fxml"));
        }
        else if(model.getFileName().endsWith(".cert")){
            // TODO: IMPLEMENT
            return;
        }
        else if(model.getFileName().endsWith(".tik")){
            // TODO: IMPLEMENT
            return;
        }
        else if(model.getFileName().endsWith(".xml")){
            // TODO: IMPLEMENT
            return;
        }
        else
            return;

        Locale userLocale = new Locale(Locale.getDefault().getISO3Language());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("locale", userLocale);
        loaderSettings.setResources(resourceBundle);
        Parent parentAbout = loaderSettings.load();


        // TODO: REFACTOR
        if (model.getFileName().endsWith(".nca")){
            NCAController ncaController = loaderSettings.<NCAController>getController();
            ncaController.analyze(provider.getFile(), provider.getRawFileDataStart()+model.getFileOffset());
        }


        stageSettings.setTitle(model.getFileName());
        stageSettings.getIcons().addAll(
                new Image(getClass().getResourceAsStream("/res/app_icon32x32.png")),
                new Image(getClass().getResourceAsStream("/res/app_icon48x48.png")),
                new Image(getClass().getResourceAsStream("/res/app_icon64x64.png")),
                new Image(getClass().getResourceAsStream("/res/app_icon128x128.png"))
        );
        Scene settingsScene = new Scene(parentAbout, 800, 800);
        settingsScene.getStylesheets().add("/res/app_light.css");
        stageSettings.setScene(settingsScene);
        stageSettings.setMinWidth(550.0);
        stageSettings.setMinHeight(550.0);
        stageSettings.show();
    }
}
