package backend.Models;

import java.util.List;

public record ResultadoRuta(
        List<Parada> paradas,
        List<Ruta> rutas,
        int distanciaTotal,
        double costoTotal,
        int transbordosTotal,
        int tiempoTotal
) {
    @Override
    public String toString() {
        return "Ruta: " + paradas +
                "\nDistancia total: " + distanciaTotal + " m" +
                "\nCosto total: $" + costoTotal +
                "\nTransbordos totales: " + transbordosTotal +
                "\nTiempo total: " + tiempoTotal + " min";
    }
}
