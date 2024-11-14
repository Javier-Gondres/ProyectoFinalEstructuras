package backend.Models.Interfaces;

import backend.Models.Excepciones.ParadaDuplicadaException;
import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.Excepciones.RutaDuplicadaException;
import backend.Models.GrafoTransporte;
import backend.Models.Parada;
import backend.Models.Ruta;

import java.util.List;

public interface Grafo {
    void agregarParada(Parada parada) throws ParadaDuplicadaException;
    void agregarRuta(Parada origen, Parada destino, int tiempo, int distancia, double costo, int transbordos) throws ParadaInexistenteException, RutaDuplicadaException;
    List<Parada> obtenerParadas();
    List<Ruta> obtenerRutasDesde(Parada parada);
    boolean contieneParada(Parada parada);
    Ruta buscarRuta(Parada origen, Parada destino) throws ParadaInexistenteException;
}