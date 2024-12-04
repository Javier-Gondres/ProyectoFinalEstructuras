package backend.Models;

import backend.Models.Excepciones.ParadaDuplicadaException;
import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.Excepciones.RutaDuplicadaException;
import backend.Models.Excepciones.RutaInexistenteException;
import backend.Models.GraphAlgorithms.Dijkstra;
import backend.Models.GraphAlgorithms.FloydWarshall;
import backend.Models.Interfaces.Grafo;
import backend.Utils.GrafoUtils;

import java.util.*;

public class GrafoTransporte implements Grafo {
    private static final GrafoTransporte INSTANCE = new GrafoTransporte();

    private final Map<Parada, List<Ruta>> listaAdyacencia;
    private final Set<String> nombresExistentes;
    private final Map<String, Parada> paradas;

    private GrafoTransporte() {
        listaAdyacencia = new HashMap<>();
        nombresExistentes = new HashSet<>();
        paradas = new HashMap<>();
    }

    public static GrafoTransporte getInstance() {
        return INSTANCE;
    }

    public void agregarParada(Parada parada) throws ParadaDuplicadaException {
        if (parada == null) {
            throw new IllegalArgumentException("Error: La parada no puede ser nula.");
        }
        if (nombresExistentes.contains(parada.getNombre())) {
            throw new ParadaDuplicadaException("Error: La parada '" + parada.getNombre() + "' ya existe.");
        }
        if (paradas.containsKey(parada.getId())) {
            throw new ParadaDuplicadaException("Error: La parada con ID '" + parada.getId() + "' ya existe.");
        }

        nombresExistentes.add(parada.getNombre());
        listaAdyacencia.put(parada, new ArrayList<>());
        paradas.put(parada.getId(), parada);
    }

    public void modificarParada(String id, String nuevoNombre)
            throws ParadaInexistenteException, ParadaDuplicadaException {

        Parada paradaExistente = obtenerParada(id);

        if(paradaExistente == null){
            throw new ParadaInexistenteException("Error: La parada con ID '" + id + "' no existe.");
        }

        if (!listaAdyacencia.containsKey(paradaExistente)) {
            throw new ParadaInexistenteException("Error: La parada '" + paradaExistente.getNombre() + "' no existe.");
        }

        if (nombresExistentes.contains(nuevoNombre) && !paradaExistente.getNombre().equals(nuevoNombre)) {
            throw new ParadaDuplicadaException("Error: La parada con el nombre '" + nuevoNombre + "' ya existe.");
        }

        nombresExistentes.remove(paradaExistente.getNombre());
        nombresExistentes.add(nuevoNombre);

        paradaExistente.setNombre(nuevoNombre);
    }

    public void eliminarParada(String id) throws ParadaInexistenteException {
        Parada parada = obtenerParada(id);

        if(parada == null){
            throw new ParadaInexistenteException("Error: La parada con ID '" + id + "' no existe.");
        }

        if (!listaAdyacencia.containsKey(parada)) {
            throw new ParadaInexistenteException("Error: La parada '" + parada.getNombre() + "' no existe.");
        }

        listaAdyacencia.remove(parada);

        nombresExistentes.remove(parada.getNombre());

        for (List<Ruta> rutas : listaAdyacencia.values()) {
            rutas.removeIf(ruta -> ruta.getDestinoId().equals(parada.getId()));
        }

        paradas.remove(id);
    }

    public Parada obtenerParada(String id) {
        return paradas.get(id);
    }

    public void agregarRuta(Ruta ruta)
            throws ParadaInexistenteException, RutaDuplicadaException {

        Parada origen = obtenerParada(ruta.getOrigenId());
        Parada destino = obtenerParada(ruta.getDestinoId());

        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Las paradas no pueden ser nulas");
        }
        if (ruta.getTiempo() < 0 || ruta.getDistancia() < 0 || ruta.getCosto() < 0 || ruta.getTransbordos() < 0) {
            throw new IllegalArgumentException("Los valores no pueden ser negativos");
        }
        if (!listaAdyacencia.containsKey(origen)) {
            throw new ParadaInexistenteException("Error: La parada de origen '" + origen.getNombre() + "' no existe.");
        }
        if (!listaAdyacencia.containsKey(destino)) {
            throw new ParadaInexistenteException("Error: La parada de destino '" + destino.getNombre() + "' no existe.");
        }

        for (Ruta rutaBuscada : listaAdyacencia.get(origen)) {
            if (rutaBuscada.getDestinoId().equals(destino.getId())) {
                throw new RutaDuplicadaException("Error: Ya existe una ruta entre " + origen.getNombre() + " y " + destino.getNombre());
            }
        }

        listaAdyacencia.get(origen).add(ruta);
    }

    public void modificarRuta(Ruta ruta, int nuevoTiempo, int nuevaDistancia, double nuevoCosto, int nuevosTransbordos)
            throws RutaInexistenteException, ParadaInexistenteException {

        if (nuevoTiempo < 0 || nuevaDistancia < 0 || nuevoCosto < 0 || nuevosTransbordos < 0) {
            throw new IllegalArgumentException("Los valores no pueden ser negativos");
        }

        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser nula");
        }

        Parada origen = obtenerParada(ruta.getOrigenId());
        if (!listaAdyacencia.containsKey(origen)) {
            throw new ParadaInexistenteException("Error: La ruta especificada no existe en el grafo.");
        }

        boolean rutaExist = false;

        for (Ruta rutaABuscar : listaAdyacencia.get(origen)) {
            System.out.println("ID DE LA RUTA BUSCADA: " + rutaABuscar.getId());
            if (rutaABuscar.equals(ruta)) {
                rutaExist = true;
                break;
            }
        }

        if (!rutaExist) {
            throw new RutaInexistenteException("Error: La ruta especificada no existe en el grafo.");
        }

        ruta.setTiempo(nuevoTiempo);
        ruta.setDistancia(nuevaDistancia);
        ruta.setCosto(nuevoCosto);
        ruta.setTransbordos(nuevosTransbordos);
    }

    public void eliminarRuta(String origenId, String destinoId)
            throws ParadaInexistenteException, RutaInexistenteException {

        Parada origen = obtenerParada(origenId);
        Parada destino = obtenerParada(destinoId);

        if (!listaAdyacencia.containsKey(origen)) {
            throw new ParadaInexistenteException("Error: La parada de origen '" + origen.getNombre() + "' no existe.");
        }
        if (!listaAdyacencia.containsKey(destino)) {
            throw new ParadaInexistenteException("Error: La parada de destino '" + destino.getNombre() + "' no existe.");
        }

        List<Ruta> rutasDesdeOrigen = listaAdyacencia.get(origen);

        boolean rutaEliminada = rutasDesdeOrigen.removeIf(ruta -> ruta.getDestinoId().equals(destino.getId()));

        if (!rutaEliminada) {
            throw new RutaInexistenteException("Error: No existe una ruta entre " +
                    origen.getNombre() + " y " + destino.getNombre());
        }
    }

    public Ruta buscarRuta(Parada origen, Parada destino) throws ParadaInexistenteException {
        if (!listaAdyacencia.containsKey(origen)) {
            throw new ParadaInexistenteException("Error: La parada de origen '" + origen.getNombre() + "' no existe.");
        }
        if (!listaAdyacencia.containsKey(destino)) {
            throw new ParadaInexistenteException("Error: La parada de destino '" + destino.getNombre() + "' no existe.");
        }

        List<Ruta> rutasDesdeOrigen = listaAdyacencia.get(origen);
        for (Ruta ruta : rutasDesdeOrigen) {
            if (ruta.getDestinoId().equals(destino.getId())) {
                return ruta;
            }
        }
        return null;
    }

    public void imprimirGrafo() {
        for (Parada parada : listaAdyacencia.keySet()) {
            System.out.println("Parada " + parada.getNombre() + " tiene rutas hacia:");
            List<Ruta> rutas = listaAdyacencia.get(parada);

            for (Ruta ruta : rutas) {
                System.out.println("   " + ruta);
            }
        }
    }

    public List<Ruta> obtenerRutasDesde(Parada parada) {
        return listaAdyacencia.getOrDefault(parada, new ArrayList<>());
    }

    public List<Parada> obtenerParadas() {
        return new ArrayList<>(listaAdyacencia.keySet());
    }

    public ResultadoRuta obtenerRutaEntreParadasConDijkstra(Parada origen, Parada destino, double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) throws ParadaInexistenteException, IllegalArgumentException {
        GrafoUtils.verificarParadasExistentes(this, origen, destino);

        Dijkstra dijkstra = new Dijkstra(pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);

        return dijkstra.obtenerRutaEntreParadas(origen, destino);
    }

    public ResultadoRuta obtenerRutaEntreParadasConFloyd(Parada origen, Parada destino, double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) throws ParadaInexistenteException, RutaInexistenteException {
        GrafoUtils.verificarParadasExistentes(this, origen, destino);

        FloydWarshall floydWarshall = new FloydWarshall(this, pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);

        return floydWarshall.obtenerRutaEntreParadas(origen, destino);
    }

    public boolean contieneParada(Parada parada) {
        return listaAdyacencia.containsKey(parada);
    }

    public Map<Parada, List<Ruta>> getListaAdyacencia() {
        return listaAdyacencia;
    }

    public Set<String> getNombresExistentes() {
        return nombresExistentes;
    }

    public Map<String, Parada> getParadas() {
        return paradas;
    }
}
