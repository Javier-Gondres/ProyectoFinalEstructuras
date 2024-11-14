package backend.Models.GraphAlgorithms;

import backend.Models.*;
import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.Excepciones.RutaInexistenteException;
import backend.Models.Interfaces.Grafo;
import backend.Utils.GrafoUtils;

import java.util.*;

public class Dijkstra extends AlgoritmoGrafo{

    private Map<Parada, List<Ruta>> listaAdyacencia;
    private double pesoTiempo;
    private double pesoDistancia;
    private double pesoTransbordos;
    private double pesoCosto;

    public Dijkstra(Map<Parada, List<Ruta>> listaAdyacencia, double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) {
        this.listaAdyacencia = listaAdyacencia;
        this.pesoTiempo = pesoTiempo;
        this.pesoDistancia = pesoDistancia;
        this.pesoTransbordos = pesoTransbordos;
        this.pesoCosto = pesoCosto;
    }

    public List<ParadaWrapper> ejecutarDijkstra(Parada origen, Parada destino) {
        Map<Parada, ParadaWrapper> paradaWrappers = new HashMap<>();
        PriorityQueue<ParadaWrapper> queue = new PriorityQueue<>();
        Set<Parada> shortestPathFound = new HashSet<>();

        ParadaWrapper sourceWrapper = new ParadaWrapper(origen, 0, null, null);
        paradaWrappers.put(origen, sourceWrapper);
        queue.add(sourceWrapper);

        while (!queue.isEmpty()) {
            ParadaWrapper paradaWrapper = queue.poll();
            Parada parada = paradaWrapper.getParadaNodo();
            if (shortestPathFound.contains(parada)) {
                continue;
            }
            shortestPathFound.add(parada);

            if (parada.equals(destino)) {
                return GrafoUtils.construirRuta(paradaWrapper);
            }

            for (Ruta ruta : listaAdyacencia.get(parada)) {
                Parada vecino = ruta.getDestino();
                if (shortestPathFound.contains(vecino)) {
                    continue;
                }

                double distanciaActual = paradaWrapper.getDistanciaTotal();
                double pesoRuta = GrafoUtils.calcularPesoRuta(ruta, pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);
                double distanciaAcumulada = distanciaActual + pesoRuta;

                ParadaWrapper vecinoWrapper = paradaWrappers.get(vecino);
                if (vecinoWrapper == null || distanciaAcumulada < vecinoWrapper.getDistanciaTotal()) {
                    vecinoWrapper = new ParadaWrapper(vecino, distanciaAcumulada, paradaWrapper, ruta);
                    paradaWrappers.put(vecino, vecinoWrapper);
                    queue.add(vecinoWrapper);
                }
            }
        }

        return null;
    }

    @Override
    public ResultadoRuta obtenerRutaEntreParadas(Parada origen, Parada destino) throws IllegalArgumentException {
        if (pesoTiempo < 0 || pesoDistancia < 0 || pesoTransbordos < 0 || pesoCosto < 0) {
            throw new IllegalArgumentException("Los pesos deben ser valores positivos.");
        }

        double[] pesosNormalizados = GrafoUtils.normalizarPesos(pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);
        pesoTiempo = pesosNormalizados[0];
        pesoDistancia = pesosNormalizados[1];
        pesoTransbordos = pesosNormalizados[2];
        pesoCosto = pesosNormalizados[3];

        List<ParadaWrapper> rutaWrappers = ejecutarDijkstra(origen, destino);

        if (rutaWrappers == null) {
            return new ResultadoRuta(Collections.emptyList(), 0, 0.0, 0, 0);
        }

        List<Parada> paradas = new ArrayList<>();
        int distanciaTotal = 0;
        double costoTotal = 0;
        int transbordosTotal = 0;
        int tiempoTotal = 0;

        for (int i = 0; i < rutaWrappers.size(); i++) {
            ParadaWrapper wrapper = rutaWrappers.get(i);
            paradas.add(wrapper.getParadaNodo());

            if (i > 0) { // El primer nodo no tiene rutaUsada
                Ruta ruta = wrapper.getRutaUsada();
                if (ruta != null) {
                    distanciaTotal += ruta.getDistancia();
                    costoTotal += ruta.getCosto();
                    transbordosTotal += ruta.getTransbordos();
                    tiempoTotal += ruta.getTiempo();
                }
            }
        }

        return new ResultadoRuta(paradas, distanciaTotal, costoTotal, transbordosTotal, tiempoTotal);
    }
}
