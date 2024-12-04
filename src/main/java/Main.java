import backend.Controllers.ParadaController;
import backend.Controllers.RutaController;
import backend.Models.Parada;
import backend.Models.Ruta;
import com.google.cloud.firestore.Firestore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
//            ParadaController paradaController = new ParadaController();
//            Parada destino = paradaController.create("Destino");
//            Parada origen = paradaController.get("8840f814");
//
//            RutaController rutaController= new RutaController();
//
//            Ruta r = new Ruta(origen.getId(), destino.getId(), 2, 2, 2, 2);
//            rutaController.create(r);

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
