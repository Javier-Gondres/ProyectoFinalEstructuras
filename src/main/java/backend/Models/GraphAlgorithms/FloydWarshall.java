package backend.Models.GraphAlgorithms;

import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.Excepciones.RutaInexistenteException;
import backend.Models.GrafoTransporte;
import backend.Models.Parada;
import backend.Models.ResultadoRuta;
import backend.Models.Ruta;
import backend.Utils.GrafoUtils;

import java.util.*;

public class FloydWarshall extends AlgoritmoGrafo{

    private GrafoTransporte grafo;
    private double[][] distancias;
    private int[][] predecesores;
    private Map<Parada, Integer> paradaIndices;
    private Map<Integer, Parada> indiceParadas;

    public FloydWarshall(GrafoTransporte grafo, double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) {
        this.grafo = grafo;
        ejecutarFloydWarshall(pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);
    }

    private void ejecutarFloydWarshall(double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) {
        double[] pesosNormalizados = GrafoUtils.normalizarPesos(pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);
        pesoTiempo = pesosNormalizados[0];
        pesoDistancia = pesosNormalizados[1];
        pesoTransbordos = pesosNormalizados[2];
        pesoCosto = pesosNormalizados[3];

        paradaIndices = new HashMap<>();
        indiceParadas = new HashMap<>();
        int index = 0;
        for (Parada parada : grafo.obtenerParadas()) {
            paradaIndices.put(parada, index);
            indiceParadas.put(index, parada);
            index++;
        }

        int V = paradaIndices.size();
        distancias = new double[V][V];
        predecesores = new int[V][V];
        double INF = Double.POSITIVE_INFINITY;

        // Inicializar matrices
        for (int i = 0; i < V; i++) {
            Arrays.fill(distancias[i], INF);
            distancias[i][i] = 0;
            Arrays.fill(predecesores[i], -1);
        }

        // Llenar distancias directas y predecesores
        for (Parada origenParada : grafo.obtenerParadas()) {
            int i = paradaIndices.get(origenParada);
            for (Ruta ruta : grafo.obtenerRutasDesde(origenParada)) {
                Parada destinoParada = ruta.getDestino();
                int j = paradaIndices.get(destinoParada);

                double pesoRuta = GrafoUtils.calcularPesoRuta(ruta, pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);

                if (pesoRuta < distancias[i][j]) {
                    distancias[i][j] = pesoRuta;
                    predecesores[i][j] = i;
                }
            }
        }

        // Ejecutar Floyd-Warshall
        for (int k = 0; k < V; k++) {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    if (distancias[i][k] + distancias[k][j] < distancias[i][j]) {
                        distancias[i][j] = distancias[i][k] + distancias[k][j];
                        predecesores[i][j] = predecesores[k][j];
                    }
                }
            }
        }
    }

    @Override
    public ResultadoRuta obtenerRutaEntreParadas(Parada origen, Parada destino) throws ParadaInexistenteException, RutaInexistenteException {
        if (!paradaIndices.containsKey(origen) || !paradaIndices.containsKey(destino)) {
            throw new ParadaInexistenteException("Una o ambas paradas no existen en el grafo.");
        }

        int origenIdx = paradaIndices.get(origen);
        int destinoIdx = paradaIndices.get(destino);

        List<Parada> camino = reconstruirCamino(origenIdx, destinoIdx);

        int distanciaTotal = 0;
        double costoTotal = 0.0;
        int transbordosTotal = 0;
        int tiempoTotal = 0;

        for (int i = 0; i < camino.size() - 1; i++) {
            Parada actual = camino.get(i);
            Parada siguiente = camino.get(i + 1);

            Ruta ruta = grafo.buscarRuta(actual, siguiente);
            if (ruta != null) {
                distanciaTotal += ruta.getDistancia();
                costoTotal += ruta.getCosto();
                transbordosTotal += ruta.getTransbordos();
                tiempoTotal += ruta.getTiempo();
            } else {
                throw new RutaInexistenteException("No existe ruta directa entre " + actual.getNombre() + " y " + siguiente.getNombre());
            }
        }

        return new ResultadoRuta(camino, distanciaTotal, costoTotal, transbordosTotal, tiempoTotal);
    }

    public double obtenerPesoDistanciasParadas(Parada origen, Parada destino) throws ParadaInexistenteException {
        if (!paradaIndices.containsKey(origen) || !paradaIndices.containsKey(destino)) {
            throw new ParadaInexistenteException("Una o ambas paradas no existen en el grafo.");
        }

        int origenIdx = paradaIndices.get(origen);
        int destinoIdx = paradaIndices.get(destino);

        return distancias[origenIdx][destinoIdx];
    }

    private List<Parada> reconstruirCamino(int origenIdx, int destinoIdx) {
        List<Parada> camino = new ArrayList<>();
        if (predecesores[origenIdx][destinoIdx] == -1) {
            return camino; // No hay camino
        }
        int actual = destinoIdx;
        while (actual != origenIdx) {
            camino.add(indiceParadas.get(actual));
            actual = predecesores[origenIdx][actual];
        }
        camino.add(indiceParadas.get(origenIdx));
        Collections.reverse(camino);
        return camino;
    }
}
