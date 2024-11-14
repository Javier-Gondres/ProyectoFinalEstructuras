package backend.Utils;

import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.GrafoTransporte;
import backend.Models.Parada;
import backend.Models.Ruta;

public class GrafoUtils {

    public static double[] normalizarPesos(double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) {
        double sumaPesos = pesoTiempo + pesoDistancia + pesoTransbordos + pesoCosto;
        if (sumaPesos == 0) {
            // Priorizar distancia por defecto
            return new double[] { 0.0, 1.0, 0.0, 0.0 };
        }
        return new double[] {
                pesoTiempo / sumaPesos,
                pesoDistancia / sumaPesos,
                pesoTransbordos / sumaPesos,
                pesoCosto / sumaPesos
        };
    }

    public static double calcularPesoRuta(Ruta ruta, double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) {
        return (pesoDistancia * ruta.getDistancia()) +
                (pesoTransbordos * ruta.getTransbordos()) +
                (pesoTiempo * ruta.getTiempo()) +
                (pesoCosto * ruta.getCosto());
    }

    public static void verificarParadasExistentes(GrafoTransporte grafo, Parada origen, Parada destino) throws ParadaInexistenteException {
        if (!grafo.contieneParada(origen)) {
            throw new ParadaInexistenteException("Error: La parada de origen '" + origen.getNombre() + "' no existe.");
        }
        if (!grafo.contieneParada(destino)) {
            throw new ParadaInexistenteException("Error: La parada de destino '" + destino.getNombre() + "' no existe.");
        }
    }
}