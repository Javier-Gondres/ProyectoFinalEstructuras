package backend.Models;

public record ObtenerRutaParam(Parada origen, Parada destino,
                               double pesoTiempo, double pesoDistancia,
                               double pesoTransbordos, double pesoCosto) {
}
