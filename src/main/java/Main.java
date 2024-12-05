import backend.Controllers.GrafoController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            GrafoController controlador =  GrafoController.getInstance();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Screens/Grafo/Grafo.fxml"));
            BorderPane root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Gestión de Grafo de Transporte");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
