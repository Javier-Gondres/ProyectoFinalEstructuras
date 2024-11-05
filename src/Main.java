import backend.Models.GrafoTransporte;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
    public static void main(String[] args) {
        GrafoTransporte grafo = new GrafoTransporte();

        // Crear paradas
        GrafoTransporte.Parada p1 = new GrafoTransporte.Parada("GrafoTransporte.Parada 1");
        GrafoTransporte.Parada p2 = new GrafoTransporte.Parada("GrafoTransporte.Parada 2");
        GrafoTransporte.Parada p3 = new GrafoTransporte.Parada("Parada 3");

        try {
            // Agregar paradas al grafo
            grafo.agregarParada(p1);
            grafo.agregarParada(p2);
            grafo.agregarParada(p3);
            grafo.agregarParada(p1);

            // Agregar rutas entre paradas
            grafo.agregarRuta(p1, p2, 10, 5000, 1.5, 0);
            grafo.agregarRuta(p1, p2, 10, 5000, 1.5, 0); // Intento de agregar ruta duplicada
            grafo.agregarRuta(p1, p3, 15, 7000, 2.0, 1);
            grafo.agregarRuta(p2, new GrafoTransporte.Parada("Parada 4"), 20, 9000, 3.0, 1); // Parada inexistente

            // Imprimir el grafo
            grafo.imprimirGrafo();

        } catch (GrafoTransporte.ParadaDuplicadaException | GrafoTransporte.RutaDuplicadaException |
                 GrafoTransporte.ParadaInexistenteException e) {
            System.err.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Argumento inválido: " + e.getMessage());
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
    }
}