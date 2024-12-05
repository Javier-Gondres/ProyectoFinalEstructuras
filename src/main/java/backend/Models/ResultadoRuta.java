package backend.Models;

import java.util.List;
import java.util.stream.Collectors;

public record ResultadoRuta(
        List<Parada> paradas,
        List<Ruta> rutas,
        int distanciaTotal,
        double costoTotal,
        int transbordosTotal,
        int tiempoTotal
) {

    public String mostrarParadas() {
        if (paradas.isEmpty()) {
            return "No hay paradas disponibles.";
        }

        return paradas.stream()
                .map(Parada::toString)
                .collect(Collectors.joining("\n"));
    }


    @Override
    public String toString() {
        return "Ruta: " + paradas +
                "\nDistancia total: " + distanciaTotal + " m" +
                "\nCosto total: $" + costoTotal +
                "\nTransbordos totales: " + transbordosTotal +
                "\nTiempo total: " + tiempoTotal + " min";
    }
}
