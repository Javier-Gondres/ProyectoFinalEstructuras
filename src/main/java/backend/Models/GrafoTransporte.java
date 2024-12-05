package backend.Models;

import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.Excepciones.RutaInexistenteException;
import backend.Models.GraphAlgorithms.Dijkstra;
import backend.Models.GraphAlgorithms.FloydWarshall;
import backend.Utils.GrafoUtils;

import java.util.*;

public class GrafoTransporte {
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

    /**
     * Agrega una nueva parada al grafo.
     *
     * @param parada La parada a agregar.
     */
    public synchronized void agregarParada(Parada parada) {
        nombresExistentes.add(parada.getNombre());
        listaAdyacencia.put(parada, new ArrayList<>());
        paradas.put(parada.getId(), parada);
    }

    /**
     * Modifica una parada existente.
     *
     * @param id          El ID de la parada a modificar.
     * @param nuevoNombre El nuevo nombre de la parada.
     */
    public synchronized void modificarParada(String id, String nuevoNombre) {
        Parada paradaExistente = obtenerParada(id);
        if (paradaExistente != null) {
            nombresExistentes.remove(paradaExistente.getNombre());
            paradaExistente.setNombre(nuevoNombre);
            nombresExistentes.add(nuevoNombre);
        }
    }

    /**
     * Elimina una parada del grafo.
     *
     * @param id El ID de la parada a eliminar.
     */
    public synchronized void eliminarParada(String id) {
        Parada parada = obtenerParada(id);
        if (parada != null) {
            listaAdyacencia.remove(parada);
            nombresExistentes.remove(parada.getNombre());
            paradas.remove(id);

            for (List<Ruta> rutas : listaAdyacencia.values()) {
                rutas.removeIf(ruta -> ruta.getDestinoId().equals(id));
            }
        }
    }

    /**
     * Verifica si una parada existe por su nombre.
     *
     * @param nombre El nombre de la parada.
     * @return true si existe, false en caso contrario.
     */
    public synchronized boolean existeParadaPorNombre(String nombre) {
        return nombresExistentes.contains(nombre);
    }

    /**
     * Verifica si una parada existe por su ID.
     *
     * @param id El ID de la parada.
     * @return true si existe, false en caso contrario.
     */
    public synchronized boolean existeParada(String id) {
        return paradas.containsKey(id);
    }

    /**
     * Obtiene una parada por su ID.
     *
     * @param id El ID de la parada.
     * @return La parada correspondiente o null si no existe.
     */
    public synchronized Parada obtenerParada(String id) {
        return paradas.get(id);
    }

    /**
     * Agrega una nueva ruta al grafo.
     */
    public synchronized void agregarRuta(Ruta ruta) {
        Parada origen = obtenerParada(ruta.getOrigenId());
        listaAdyacencia.get(origen).add(ruta);
    }

    /**
     * Modifica una ruta existente.
     *
     * @param ruta              La ruta a modificar.
     * @param nuevoTiempo       El nuevo tiempo de la ruta.
     * @param nuevaDistancia    La nueva distancia de la ruta.
     * @param nuevoCosto        El nuevo costo de la ruta.
     * @param nuevosTransbordos Los nuevos transbordos de la ruta.
     */
    public synchronized void modificarRuta(Ruta ruta, int nuevoTiempo, int nuevaDistancia, double nuevoCosto, int nuevosTransbordos) {
        Ruta rutaExistente = buscarRutaPorId(ruta.getId());
        if (rutaExistente != null) {
            rutaExistente.setTiempo(nuevoTiempo);
            rutaExistente.setDistancia(nuevaDistancia);
            rutaExistente.setCosto(nuevoCosto);
            rutaExistente.setTransbordos(nuevosTransbordos);
        }
    }

    /**
     * Elimina una ruta del grafo.
     *
     * @param origenId  El ID de la parada de origen.
     * @param destinoId El ID de la parada de destino.
     */
    public synchronized void eliminarRuta(String origenId, String destinoId) {
        Parada origen = obtenerParada(origenId);
        if (origen != null) {
            listaAdyacencia.get(origen).removeIf(ruta -> ruta.getDestinoId().equals(destinoId));
        }
    }

    /**
     * Verifica si existe una ruta entre dos paradas.
     *
     * @param origenId  El ID de la parada de origen.
     * @param destinoId El ID de la parada de destino.
     * @return true si existe, false en caso contrario.
     */
    public synchronized boolean existeRuta(String origenId, String destinoId) {
        Parada origen = obtenerParada(origenId);
        if (origen == null) {
            return false;
        }
        return listaAdyacencia.get(origen).stream()
                .anyMatch(ruta -> ruta.getDestinoId().equals(destinoId));
    }

    /**
     * Obtiene una ruta por su ID.
     *
     * @param rutaId El ID de la ruta.
     * @return La ruta correspondiente o null si no existe.
     */
    private Ruta buscarRutaPorId(String rutaId) {
        for (List<Ruta> rutasDesdeOrigen : listaAdyacencia.values()) {
            for (Ruta ruta : rutasDesdeOrigen) {
                if (ruta.getId().equals(rutaId)) {
                    return ruta;
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todas las paradas existentes.
     *
     * @return Una lista de todas las paradas.
     */
    public synchronized List<Parada> obtenerParadas() {
        return new ArrayList<>(paradas.values());
    }

    /**
     * Obtiene todas las rutas existentes.
     *
     * @return Una lista de todas las rutas.
     */
    public synchronized List<Ruta> obtenerRutas() {
        List<Ruta> todasLasRutas = new ArrayList<>();
        for (List<Ruta> rutasDesdeOrigen : listaAdyacencia.values()) {
            todasLasRutas.addAll(rutasDesdeOrigen);
        }
        return todasLasRutas;
    }

    /**
     * Obtiene la ruta más corta entre dos paradas utilizando el algoritmo de Dijkstra.
     *
     * @param origen           La parada de origen.
     * @param destino          La parada de destino.
     * @param pesoTiempo       Peso asignado al tiempo.
     * @param pesoDistancia    Peso asignado a la distancia.
     * @param pesoTransbordos  Peso asignado a los transbordos.
     * @param pesoCosto        Peso asignado al costo.
     * @return El resultado de la ruta más corta.
     * @throws ParadaInexistenteException Si alguna de las paradas no existe.
     */
    public ResultadoRuta obtenerRutaEntreParadasConDijkstra(Parada origen, Parada destino,
                                                            double pesoTiempo, double pesoDistancia,
                                                            double pesoTransbordos, double pesoCosto)
            throws ParadaInexistenteException {

        GrafoUtils.verificarParadasExistentes(this, origen, destino);

        Dijkstra dijkstra = new Dijkstra(pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);

        return dijkstra.obtenerRutaEntreParadas(origen, destino);
    }

    /**
     * Obtiene la ruta más corta entre dos paradas utilizando el algoritmo de Floyd-Warshall.
     *
     * @param origen           La parada de origen.
     * @param destino          La parada de destino.
     * @param pesoTiempo       Peso asignado al tiempo.
     * @param pesoDistancia    Peso asignado a la distancia.
     * @param pesoTransbordos  Peso asignado a los transbordos.
     * @param pesoCosto        Peso asignado al costo.
     * @return El resultado de la ruta más corta.
     * @throws ParadaInexistenteException Si alguna de las paradas no existe.
     * @throws RutaInexistenteException   Si la ruta no existe.
     */
    public ResultadoRuta obtenerRutaEntreParadasConFloyd(Parada origen, Parada destino,
                                                         double pesoTiempo, double pesoDistancia,
                                                         double pesoTransbordos, double pesoCosto)
            throws ParadaInexistenteException, RutaInexistenteException {

        GrafoUtils.verificarParadasExistentes(this, origen, destino);

        FloydWarshall floydWarshall = new FloydWarshall(this, pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);

        return floydWarshall.obtenerRutaEntreParadas(origen, destino);
    }

    /**
     * Imprime el grafo completo.
     */
    public synchronized void imprimirGrafo() {
        for (Parada parada : listaAdyacencia.keySet()) {
            System.out.println("Parada " + parada.getNombre() + " tiene rutas hacia:");
            List<Ruta> rutas = listaAdyacencia.get(parada);

            for (Ruta ruta : rutas) {
                System.out.println("   " + ruta);
            }
        }
    }

    public synchronized List<Ruta> obtenerRutasDesde(Parada parada) {
        return listaAdyacencia.getOrDefault(parada, new ArrayList<>());
    }

    public synchronized boolean contieneParada(Parada parada) {
        return listaAdyacencia.containsKey(parada);
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

    public synchronized Map<Parada, List<Ruta>> getListaAdyacencia() {
        return new HashMap<>(listaAdyacencia);
    }

    public synchronized Set<String> getNombresExistentes() {
        return new HashSet<>(nombresExistentes);
    }

    public synchronized Map<String, Parada> getParadas() {
        return new HashMap<>(paradas);
    }
}
