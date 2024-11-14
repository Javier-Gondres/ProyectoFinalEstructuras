package backend.Models.GraphAlgorithms;

import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.Excepciones.RutaInexistenteException;
import backend.Models.GrafoTransporte;
import backend.Models.Interfaces.Grafo;
import backend.Models.Parada;
import backend.Models.ResultadoRuta;

public abstract class AlgoritmoGrafo {
    protected Grafo grafo;

    public AlgoritmoGrafo(Grafo grafo) {
        this.grafo = grafo;
    }

    public abstract ResultadoRuta obtenerRutaEntreParadas(Parada origen, Parada destino) throws ParadaInexistenteException, RutaInexistenteException;
}