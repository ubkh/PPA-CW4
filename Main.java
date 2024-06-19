import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.Scene;

import javafx.fxml.FXMLLoader;

import java.net.URI;
import java.net.URL;

/**
 * Write a description of class MainWindowView here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Main extends Application
{
    private Model model = new Model();
    
    /**
     * The entry point of the application
     */
    public static void main(String[] args) {
        // run application
        Application.launch(args);
    }
    
    /**
     * Starts the application and creates a container view which will hold
     * the panel views
     * @param stage The stage object of the application
     */
    public void start(Stage stage) throws Exception {

        URL url = getClass().getResource("/MainWindow.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        BorderPane root = loader.load();
        
        MainWindowController controller = loader.getController();
        controller.init(stage, root);
        controller.initModel(model);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("app.css");
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
