package backend.Models.Interfaces;

import backend.Models.Excepciones.ParadaDuplicadaException;
import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.Excepciones.RutaDuplicadaException;
import backend.Models.Excepciones.RutaInexistenteException;
import backend.Models.Parada;
import backend.Models.Ruta;

import java.util.List;

public interface Grafo {
    void agregarParada(Parada parada) throws ParadaDuplicadaException;

    void modificarParada(Parada paradaExistente, String nuevoNombre)
            throws ParadaInexistenteException, ParadaDuplicadaException;

    void eliminarParada(Parada parada) throws ParadaInexistenteException;

    void agregarRuta(Ruta ruta) throws ParadaInexistenteException, RutaDuplicadaException;

    void modificarRuta(Ruta ruta, int nuevoTiempo, int nuevaDistancia, double nuevoCosto, int nuevosTransbordos)
            throws RutaInexistenteException;

    void eliminarRuta(Parada origen, Parada destino)
            throws ParadaInexistenteException, RutaInexistenteException;

    List<Parada> obtenerParadas();

    List<Ruta> obtenerRutasDesde(Parada parada);

    boolean contieneParada(Parada parada);

    Ruta buscarRuta(Parada origen, Parada destino) throws ParadaInexistenteException;

    void imprimirGrafo();
}