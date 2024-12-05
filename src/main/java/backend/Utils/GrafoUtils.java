package backend.Utils;

import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Controllers.GrafoTransporte;
import backend.Models.Parada;
import backend.Models.ParadaWrapper;
import backend.Models.Ruta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GrafoUtils {

    private static final GrafoTransporte grafo = GrafoTransporte.getInstance();

    public static double[] normalizarPesos(double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) {
        double sumaPesos = pesoTiempo + pesoDistancia + pesoTransbordos + pesoCosto;
        if (sumaPesos == 0) {
            // Priorizar distancia por defecto
            return new double[]{0.0, 1.0, 0.0, 0.0};
        }
        return new double[]{
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

    public static void verificarParadasExistentes(Parada origen, Parada destino) throws ParadaInexistenteException {
        if (!grafo.contieneParada(origen)) {
            throw new ParadaInexistenteException("Error: La parada de origen '" + origen.getNombre() + "' no existe.");
        }
        if (!grafo.contieneParada(destino)) {
            throw new ParadaInexistenteException("Error: La parada de destino '" + destino.getNombre() + "' no existe.");
        }
    }

    public static List<ParadaWrapper> construirRuta(ParadaWrapper nodeWrapper) {
        List<ParadaWrapper> path = new ArrayList<>();
        while (nodeWrapper != null) {
            path.add(nodeWrapper);
            nodeWrapper = nodeWrapper.getPredecesor();
        }
        Collections.reverse(path);
        return path;
    }
}