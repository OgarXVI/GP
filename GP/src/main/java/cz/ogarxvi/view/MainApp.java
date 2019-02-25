package cz.ogarxvi.view;

import cz.ogarxvi.model.Localizator;
import java.util.Locale;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
/**
 * Hlavní spouštěcí třída, pouští primární okno aplikace.
 * @author OgarXVI
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        //Set localization
        //force
        Localizator.setLocale(Locale.getDefault());
        //set encoding
        System.setProperty("file.encoding", "UTF-8");
        // načte vzor 
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        // vytvoří scénu
        Scene scene = new Scene(root);
        // načte styly
        scene.getStylesheets().add("/styles/Styles.css");
        // načte ikonku 
        stage.getIcons().add(new Image("/images/icon.jpg"));
        // nastaví titulek
        stage.setTitle(Localizator.getString("window.title.main"));
        // nastaví aplikaci scénu 
        stage.setScene(scene);
        // zobrazí scénu
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

}
