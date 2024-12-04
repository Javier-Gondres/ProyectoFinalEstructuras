package backend.Models.Interfaces;

import backend.Models.Excepciones.ParadaDuplicadaException;
import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.Excepciones.RutaDuplicadaException;
import backend.Models.Excepciones.RutaInexistenteException;
import backend.Models.Parada;
import backend.Models.ResultadoRuta;
import backend.Models.Ruta;

import java.util.List;

public interface Grafo {
    void agregarParada(Parada parada) throws ParadaDuplicadaException;

    void modificarParada(String id, String nuevoNombre)
            throws ParadaInexistenteException, ParadaDuplicadaException;

    void eliminarParada(String id) throws ParadaInexistenteException;

    void agregarRuta(Ruta ruta) throws ParadaInexistenteException, RutaDuplicadaException;

    void modificarRuta(Ruta ruta, int nuevoTiempo, int nuevaDistancia, double nuevoCosto, int nuevosTransbordos)
            throws RutaInexistenteException, ParadaInexistenteException;

    void eliminarRuta(String origenId, String  destinoId)
            throws ParadaInexistenteException, RutaInexistenteException;

    List<Parada> obtenerParadas();

    List<Ruta> obtenerRutasDesde(Parada parada);

    boolean contieneParada(Parada parada);

    Ruta buscarRuta(Parada origen, Parada destino) throws ParadaInexistenteException;

    void imprimirGrafo();

    ResultadoRuta obtenerRutaEntreParadasConDijkstra(Parada origen, Parada destino, double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) throws ParadaInexistenteException, IllegalArgumentException;
}