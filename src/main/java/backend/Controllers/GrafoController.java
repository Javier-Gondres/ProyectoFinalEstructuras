package backend.Controllers;

import backend.Models.ObtenerRutaParam;
import backend.Models.Parada;
import backend.Models.ResultadoRuta;
import backend.Models.Ruta;
import backend.Models.Excepciones.ParadaDuplicadaException;
import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.Excepciones.RutaDuplicadaException;
import backend.Models.Excepciones.RutaInexistenteException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrafoController {

    private static GrafoController instance;
    private final ParadaController paradaController;
    private final RutaController rutaController;

    private final Map<String, Ruta> rutas = new HashMap<>();
    private final Map<String, Parada> paradas = new HashMap<>();
    private final GrafoTransporte grafo = GrafoTransporte.getInstance();

    private GrafoController() {
        this.paradaController = new ParadaController();
        this.rutaController = new RutaController();

        initParadas();
        initRutas();
    }

    public static synchronized GrafoController getInstance() {
        if (instance == null) {
            instance = new GrafoController();
        }
        return instance;
    }

    private void initParadas() {
        List<Parada> listaParadas = paradaController.getAll();
        for (Parada parada : listaParadas) {
            try {
                validarParadaParaAgregar(parada);
                grafo.agregarParada(parada);
                paradas.put(parada.getId(), parada);
            } catch (ParadaDuplicadaException e) {
                System.err.println("Error al agregar la parada con ID " + parada.getId() + ": " + e.getMessage());
            }
        }
    }

    private void initRutas() {
        List<Ruta> listaRutas = rutaController.getAll();
        for (Ruta ruta : listaRutas) {
            try {
                validarRutaParaAgregar(ruta);
                grafo.agregarRuta(ruta);
                rutas.put(ruta.getId(), ruta);
            } catch (ParadaInexistenteException | RutaDuplicadaException e) {
                System.err.println("Error al agregar la ruta con ID " + ruta.getId() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Valida una parada antes de agregarla al grafo.
     *
     * @param parada La parada a validar.
     * @throws ParadaDuplicadaException Si la parada ya existe.
     */
    private void validarParadaParaAgregar(Parada parada) throws ParadaDuplicadaException {
        if (parada == null) {
            throw new IllegalArgumentException("La parada no puede ser nula.");
        }

        if (grafo.existeParadaPorNombre(parada.getNombre())) {
            throw new ParadaDuplicadaException("La parada con nombre '" + parada.getNombre() + "' ya existe.");
        }

        if (paradas.containsKey(parada.getId())) {
            throw new ParadaDuplicadaException("La parada con ID '" + parada.getId() + "' ya existe.");
        }
    }

    /**
     * Valida una ruta antes de agregarla al grafo.
     *
     * @param ruta La ruta a validar.
     * @throws ParadaInexistenteException Si alguna de las paradas no existe.
     * @throws RutaDuplicadaException     Si la ruta ya existe.
     */
    private void validarRutaParaAgregar(Ruta ruta) throws ParadaInexistenteException, RutaDuplicadaException {
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser nula.");
        }

        if (!grafo.existeParada(ruta.getOrigenId())) {
            throw new ParadaInexistenteException("La parada de origen con ID '" + ruta.getOrigenId() + "' no existe.");
        }
        if (!grafo.existeParada(ruta.getDestinoId())) {
            throw new ParadaInexistenteException("La parada de destino con ID '" + ruta.getDestinoId() + "' no existe.");
        }

        if (grafo.existeRuta(ruta.getOrigenId(), ruta.getDestinoId())) {
            throw new RutaDuplicadaException("Ya existe una ruta entre las paradas con IDs '" + ruta.getOrigenId() + "' y '" + ruta.getDestinoId() + "'.");
        }

        if (ruta.getTiempo() < 0 || ruta.getDistancia() < 0 || ruta.getCosto() < 0 || ruta.getTransbordos() < 0) {
            throw new IllegalArgumentException("Los valores de tiempo, distancia, costo y transbordos deben ser positivos.");
        }
    }

    /**
     * Crea una nueva parada y la agrega al grafo.
     *
     * @param nombre El nombre de la nueva parada.
     * @return La instancia de Parada creada o null si falló.
     * @throws ParadaDuplicadaException Si la parada ya existe.
     */
    public Parada crearParada(String nombre) throws ParadaDuplicadaException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la parada no puede estar vacío.");
        }

        if (grafo.existeParadaPorNombre(nombre.trim())) {
            throw new ParadaDuplicadaException("Ya existe una parada con el nombre '" + nombre.trim() + "'.");
        }

        Parada nuevaParada = paradaController.create(nombre.trim());
        if (nuevaParada != null) {
            grafo.agregarParada(nuevaParada);
            paradas.put(nuevaParada.getId(), nuevaParada);
            System.out.println("Parada '" + nombre + "' agregada al grafo.");
        }
        return nuevaParada;
    }

    /**
     * Actualiza el nombre de una parada existente.
     *
     * @param paradaId    El ID de la parada a actualizar.
     * @param nuevoNombre El nuevo nombre para la parada.
     * @return true si la actualización fue exitosa, false en caso contrario.
     * @throws ParadaDuplicadaException   Si el nuevo nombre ya está en uso.
     * @throws ParadaInexistenteException Si la parada no existe.
     */
    public boolean actualizarParada(String paradaId, String nuevoNombre) throws ParadaDuplicadaException, ParadaInexistenteException {
        if (paradaId == null || paradaId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la parada no puede estar vacío.");
        }
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo nombre de la parada no puede estar vacío.");
        }

        Parada parada = paradas.get(paradaId);
        if (parada == null) {
            throw new ParadaInexistenteException("La parada con ID '" + paradaId + "' no existe.");
        }

        if (grafo.existeParadaPorNombre(nuevoNombre.trim()) && !parada.getNombre().equals(nuevoNombre.trim())) {
            throw new ParadaDuplicadaException("Ya existe una parada con el nombre '" + nuevoNombre.trim() + "'.");
        }

        boolean actualizado = paradaController.updateFields(paradaId, Map.of("nombre", nuevoNombre.trim()));
        if (actualizado) {
            grafo.modificarParada(paradaId, nuevoNombre.trim());
            parada.setNombre(nuevoNombre.trim());
            System.out.println("Parada con ID " + paradaId + " actualizada en el grafo.");
        }
        return actualizado;
    }

    /**
     * Elimina una parada existente y todas sus rutas asociadas del grafo.
     *
     * @param paradaId El ID de la parada a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     * @throws ParadaInexistenteException Si la parada no existe.
     */
    public boolean eliminarParada(String paradaId) throws ParadaInexistenteException {
        if (paradaId == null || paradaId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la parada no puede estar vacío.");
        }

        Parada parada = paradas.get(paradaId);
        if (parada == null) {
            throw new ParadaInexistenteException("La parada con ID '" + paradaId + "' no existe.");
        }

        grafo.eliminarParada(paradaId);
        boolean eliminada = paradaController.delete(paradaId);
        if (eliminada) {
            paradas.remove(paradaId);
            rutas.values().removeIf(ruta -> ruta.getOrigenId().equals(paradaId) || ruta.getDestinoId().equals(paradaId));
            System.out.println("Parada con ID " + paradaId + " eliminada del grafo.");
            return true;
        } else {
            System.err.println("No se pudo eliminar la parada con ID " + paradaId + " desde el controlador de paradas.");
            return false;
        }
    }

    /**
     * Crea una nueva ruta y la agrega al grafo.
     *
     * @param ruta La instancia de Ruta a crear.
     * @return true si la creación fue exitosa, false en caso contrario.
     * @throws ParadaInexistenteException Si alguna de las paradas no existe.
     * @throws RutaDuplicadaException     Si la ruta ya existe.
     */
    public boolean crearRuta(Ruta ruta) throws ParadaInexistenteException, RutaDuplicadaException {
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser nula.");
        }

        validarRutaParaAgregar(ruta);

        grafo.agregarRuta(ruta);
        boolean creada = rutaController.create(ruta);
        if (creada) {
            rutas.put(ruta.getId(), ruta);
            System.out.println("Ruta con ID " + ruta.getId() + " agregada al grafo.");
            return true;
        } else {
            grafo.eliminarRuta(ruta.getOrigenId(), ruta.getDestinoId());
            System.err.println("No se pudo crear la ruta en el controlador de rutas. Revertiendo cambios en el grafo.");
            return false;
        }
    }

    /**
     * Actualiza una ruta existente con nuevos datos.
     *
     * @param rutaId      El ID de la ruta a actualizar.
     * @param nuevosDatos Un objeto Ruta con los nuevos datos.
     * @return true si la actualización fue exitosa, false en caso contrario.
     * @throws RutaInexistenteException   Si la ruta no existe.
     * @throws ParadaInexistenteException Si alguna de las paradas no existe.
     * @throws RutaDuplicadaException     Si la nueva configuración de la ruta causa duplicación.
     */
    public boolean actualizarRuta(String rutaId, Ruta nuevosDatos) throws RutaInexistenteException, ParadaInexistenteException, RutaDuplicadaException {
        if (rutaId == null || rutaId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la ruta no puede estar vacío.");
        }
        if (nuevosDatos == null) {
            throw new IllegalArgumentException("Los nuevos datos de la ruta no pueden ser nulos.");
        }

        Ruta rutaExistente = rutas.get(rutaId);
        if (rutaExistente == null) {
            throw new RutaInexistenteException("La ruta con ID '" + rutaId + "' no existe.");
        }

        if (!rutaExistente.getOrigenId().equals(nuevosDatos.getOrigenId()) ||
                !rutaExistente.getDestinoId().equals(nuevosDatos.getDestinoId())) {
            if (grafo.existeRuta(nuevosDatos.getOrigenId(), nuevosDatos.getDestinoId())) {
                throw new RutaDuplicadaException("Ya existe una ruta entre las paradas con IDs '" + nuevosDatos.getOrigenId() + "' y '" + nuevosDatos.getDestinoId() + "'.");
            }
        }

        if (!grafo.existeParada(nuevosDatos.getOrigenId())) {
            throw new ParadaInexistenteException("La parada de origen con ID '" + nuevosDatos.getOrigenId() + "' no existe.");
        }
        if (!grafo.existeParada(nuevosDatos.getDestinoId())) {
            throw new ParadaInexistenteException("La parada de destino con ID '" + nuevosDatos.getDestinoId() + "' no existe.");
        }

        if (nuevosDatos.getTiempo() < 0 || nuevosDatos.getDistancia() < 0 || nuevosDatos.getCosto() < 0 || nuevosDatos.getTransbordos() < 0) {
            throw new IllegalArgumentException("Los valores de tiempo, distancia, costo y transbordos deben ser positivos.");
        }

        grafo.modificarRuta(rutaExistente, nuevosDatos.getTiempo(), nuevosDatos.getDistancia(), nuevosDatos.getCosto(), nuevosDatos.getTransbordos());
        boolean actualizado = rutaController.updateFields(rutaId, Map.of(
                "tiempo", nuevosDatos.getTiempo(),
                "distancia", nuevosDatos.getDistancia(),
                "costo", nuevosDatos.getCosto(),
                "transbordos", nuevosDatos.getTransbordos()
        ));

        if (actualizado) {
            rutas.put(rutaId, nuevosDatos);
            System.out.println("Ruta con ID " + rutaId + " actualizada en el grafo.");
            return true;
        } else {
            System.err.println("No se pudo actualizar la ruta en el controlador de rutas.");
            return false;
        }
    }

    /**
     * Elimina una ruta existente del grafo.
     *
     * @param rutaId El ID de la ruta a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     * @throws RutaInexistenteException   Si la ruta no existe.
     * @throws ParadaInexistenteException Si alguna de las paradas no existe.
     */
    public boolean eliminarRuta(String rutaId) throws RutaInexistenteException, ParadaInexistenteException {
        if (rutaId == null || rutaId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la ruta no puede estar vacío.");
        }

        Ruta ruta = rutas.get(rutaId);
        if (ruta == null) {
            throw new RutaInexistenteException("La ruta con ID '" + rutaId + "' no existe.");
        }

        grafo.eliminarRuta(ruta.getOrigenId(), ruta.getDestinoId()); // Sin validaciones internas
        boolean eliminada = rutaController.delete(rutaId);
        if (eliminada) {
            rutas.remove(rutaId);
            System.out.println("Ruta con ID " + rutaId + " eliminada del grafo.");
            return true;
        } else {
            System.err.println("No se pudo eliminar la ruta con ID " + rutaId + " desde el controlador de rutas.");
            return false;
        }
    }

    /**
     * Obtiene todas las paradas existentes.
     *
     * @return Una lista de todas las paradas.
     */
    public List<Parada> getAllParadas() {
        return new ArrayList<>(paradas.values());
    }

    /**
     * Obtiene todas las rutas existentes.
     *
     * @return Una lista de todas las rutas.
     */
    public List<Ruta> getAllRutas() {
        return new ArrayList<>(rutas.values());
    }

    /**
     * Obtiene las paradas por su ID.
     *
     * @param paradaId El ID de la parada.
     * @return La parada correspondiente o null si no existe.
     */
    public Parada getParadaPorId(String paradaId) {
        return paradas.get(paradaId);
    }

    /**
     * Obtiene la ruta por su ID.
     *
     * @param rutaId El ID de la ruta.
     * @return La ruta correspondiente o null si no existe.
     */
    public Ruta getRutaPorId(String rutaId) {
        return rutas.get(rutaId);
    }


    /**
     * Obtiene la ruta más corta entre dos paradas utilizando el algoritmo de Dijkstra.
     *
     * @param props Objeto con los parametros de la funcion.
     * @return El resultado de la ruta más corta.
     * @throws ParadaInexistenteException Si alguna de las paradas no existe.
     */
    public ResultadoRuta obtenerRutaEntreParadasConDijkstra(ObtenerRutaParam props)
            throws ParadaInexistenteException {

        return grafo.obtenerRutaEntreParadasConDijkstra(props);
    }

    /**
     * Obtiene la ruta más corta entre dos paradas utilizando el algoritmo de Floyd-Warshall.
     *
     * @param props Objeto con los parametros de la funcion.
     * @return El resultado de la ruta más corta.
     * @throws ParadaInexistenteException Si alguna de las paradas no existe.
     * @throws RutaInexistenteException   Si la ruta no existe.
     */
    public ResultadoRuta obtenerRutaEntreParadasConFloyd(ObtenerRutaParam props)
            throws ParadaInexistenteException, RutaInexistenteException {

        return grafo.obtenerRutaEntreParadasConFloyd(props);
    }

    /**
     * Imprime el grafo completo.
     */
    public synchronized void imprimirGrafo() {
        grafo.imprimirGrafo();
    }
}
