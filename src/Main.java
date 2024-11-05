import backend.Models.GrafoTransporte;
import backend.Models.Parada;
import backend.Models.Ruta;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
    public static void main(String[] args) {
        GrafoTransporte grafo = new GrafoTransporte();

        try {
            // Crear paradas
            Parada p1 = new Parada("Parada 1");
            Parada p2 = new Parada("Parada 2");
            Parada p3 = new Parada("Parada 3");

            // Agregar paradas al grafo
            System.out.println("Agregando paradas:");
            grafo.agregarParada(p1);
            grafo.agregarParada(p2);
            grafo.agregarParada(p3);
            grafo.imprimirGrafo();

            // Agregar rutas entre paradas
            System.out.println("\nAgregando rutas:");
            grafo.agregarRuta(p1, p2, 10, 5000, 1.5, 0);
            grafo.agregarRuta(p2, p3, 15, 3000, 2.0, 1);
            grafo.imprimirGrafo();

            // Modificar el nombre de una parada
            System.out.println("\nModificando el nombre de 'Parada 2' a 'Parada 2 Renombrada':");
            grafo.modificarParada(p2, "Parada 2 Renombrada");
            grafo.imprimirGrafo();

            // Modificar atributos de una ruta
            Ruta ruta = grafo.buscarRuta(p1, p2);
            if (ruta != null) {
                System.out.println("\nModificando atributos de la ruta de 'Parada 1' a 'Parada 2 Renombrada':");
                grafo.modificarRuta(ruta, 12, 5200, 1.75, 1);
            }
            grafo.imprimirGrafo();

            // Eliminar una ruta
            System.out.println("\nEliminando la ruta entre 'Parada 1' y 'Parada 2 Renombrada':");
            grafo.eliminarRuta(p1, p2);
            grafo.imprimirGrafo();

            // Eliminar una parada
            System.out.println("\nEliminando 'Parada 3':");
            grafo.eliminarParada(p3);
            grafo.imprimirGrafo();

        } catch (GrafoTransporte.ParadaDuplicadaException | GrafoTransporte.RutaDuplicadaException |
                 GrafoTransporte.ParadaInexistenteException | GrafoTransporte.RutaInexistenteException e) {
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