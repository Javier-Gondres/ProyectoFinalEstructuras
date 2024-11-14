import backend.Models.GrafoTransporte;
import backend.Models.Parada;
import backend.Models.ResultadoRuta;

public class Main {
    public static void main(String[] args) {
        GrafoTransporte grafo = new GrafoTransporte();

        try {
            // Crear paradas
            Parada p1 = new Parada("Parada 1");
            Parada p2 = new Parada("Parada 2");
            Parada p3 = new Parada("Parada 3");
            Parada p4 = new Parada("Parada 4");
            Parada p5 = new Parada("Parada 5");
            Parada p6 = new Parada("Parada 6");
            Parada p7 = new Parada("Parada 7");
            Parada p8 = new Parada("Parada 8");
            Parada p9 = new Parada("Parada 9");
            Parada p10 = new Parada("Parada 10");

            // Agregar paradas al grafo
            grafo.agregarParada(p1);
            grafo.agregarParada(p2);
            grafo.agregarParada(p3);
            grafo.agregarParada(p4);
            grafo.agregarParada(p5);
            grafo.agregarParada(p6);
            grafo.agregarParada(p7);
            grafo.agregarParada(p8);
            grafo.agregarParada(p9);
            grafo.agregarParada(p10);

            // Agregar rutas entre paradas
            grafo.agregarRuta(p1, p2, 10, 5, 1.5, 0);
            grafo.agregarRuta(p2, p3, 15, 8, 2.0, 1);
            grafo.agregarRuta(p3, p4, 7, 3, 0.75, 0);
            grafo.agregarRuta(p4, p5, 12, 6, 1.2, 0);
            grafo.agregarRuta(p5, p6, 9, 4, 1.0, 0);
            grafo.agregarRuta(p6, p7, 5, 2, 0.5, 0);
            grafo.agregarRuta(p7, p8, 8, 3, 0.8, 0);
            grafo.agregarRuta(p8, p9, 6, 2, 0.6, 0);
            grafo.agregarRuta(p9, p10, 10, 5, 1.5, 0);
            grafo.agregarRuta(p1, p5, 20, 10, 8.0, 1);
            grafo.agregarRuta(p2, p6, 25, 12, 3.5, 1);
            grafo.agregarRuta(p3, p7, 30, 15, 4.0, 1);
            grafo.agregarRuta(p4, p8, 35, 17, 4.5, 1);
            grafo.agregarRuta(p5, p9, 40, 20, 5.0, 1);
            grafo.agregarRuta(p6, p10, 45, 22, 5.5, 1);
            grafo.agregarRuta(p1, p3, 18, 9, 2.5, 1);
            grafo.agregarRuta(p2, p4, 22, 11, 3.0, 0);
            grafo.agregarRuta(p3, p5, 26, 13, 3.5, 0);
            grafo.agregarRuta(p4, p6, 30, 15, 4.0, 0);
            grafo.agregarRuta(p5, p7, 34, 17, 4.5, 0);
            grafo.agregarRuta(p6, p8, 38, 19, 5.0, 0);
            grafo.agregarRuta(p7, p9, 42, 21, 5.5, 0);
            grafo.agregarRuta(p8, p10, 46, 23, 6.0, 0);

            // Definir los pesos para los criterios
            double pesoTiempo = 0.5;
            double pesoDistancia = 0.5;
            double pesoTransbordo = 0.0;
            double pesoCosto = 0.8;

            ResultadoRuta resultado = grafo.encontrarRutaMasCorta(p1, p10, pesoTiempo, pesoDistancia, pesoTransbordo, pesoCosto);

            System.out.println("Ruta más corta desde " + p1.getNombre() + " hasta " + p10.getNombre() + ":");
            for (Parada parada : resultado.getRuta()) {
                System.out.print(parada.getNombre() + " -> ");
            }
            System.out.println("FIN");
            System.out.println("Distancia total: " + resultado.getDistanciaTotal());
            System.out.println("Costo total: " + resultado.getCostoTotal());
            System.out.println("Tiempo total: " + resultado.getTiempoTotal());
            System.out.println("Transbordos totales: " + resultado.getTransbordosTotal());

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
