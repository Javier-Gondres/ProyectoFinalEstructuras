package backend.Models.GraphAlgorithms;

import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.Excepciones.RutaInexistenteException;
import backend.Models.Parada;
import backend.Models.ResultadoRuta;

public abstract class AlgoritmoGrafo {
    public abstract ResultadoRuta obtenerRutaEntreParadas(Parada origen, Parada destino) throws ParadaInexistenteException, RutaInexistenteException;
}