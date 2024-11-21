package backend.Controllers;

import backend.Models.Ruta;
import backend.Services.RutaService;

import java.util.List;
import java.util.Map;

/**
 * Controlador para manejar operaciones relacionadas con las rutas.
 */
public class RutaController {

    private final RutaService rutaService;

    /**
     * Constructor de RutaController que inicializa la instancia de RutaService.
     */
    public RutaController() {
        this.rutaService = new RutaService();
    }

    /**
     * Crea una nueva ruta después de validar los datos.
     *
     * @param ruta El objeto Ruta a crear.
     * @return true si la creación fue exitosa, false en caso contrario.
     */
    public boolean create(Ruta ruta) {
        boolean creada = rutaService.create(ruta);
        if (creada) {
            System.out.println("Ruta creada exitosamente.");
        } else {
            System.err.println("Error al crear la ruta.");
        }
        return creada;
    }

    /**
     * Obtiene una ruta por su ID.
     *
     * @param rutaId El ID de la ruta a obtener.
     * @return La instancia de Ruta si se encuentra, o null si no existe.
     */
    public Ruta get(String rutaId) {
        Ruta ruta = rutaService.get(rutaId);
        if (ruta != null) {
            System.out.println("Ruta obtenida: " + ruta);
        } else {
            System.out.println("No se encontró la ruta con ID: " + rutaId);
        }
        return ruta;
    }

    /**
     * Obtiene todas las rutas existentes.
     *
     * @return Una lista de instancias de Ruta.
     */
    public List<Ruta> getAll() {
        List<Ruta> rutas = rutaService.getAll();
        if (rutas.isEmpty()) {
            System.out.println("No hay rutas registradas.");
        } else {
            System.out.println("Lista de rutas:");
            rutas.forEach(System.out::println);
        }
        return rutas;
    }

    /**
     * Actualiza campos específicos de una ruta existente.
     *
     * @param rutaId       El ID de la ruta a actualizar.
     * @param nuevosCampos Un mapa de los campos y sus nuevos valores.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean updateFields(String rutaId, Map<String, Object> nuevosCampos) {
        boolean actualizada = rutaService.updateFields(rutaId, nuevosCampos);
        if (actualizada) {
            System.out.println("Ruta actualizada exitosamente.");
        } else {
            System.err.println("Error al actualizar los campos de la ruta.");
        }
        return actualizada;
    }

    /**
     * Elimina una ruta existente por su ID.
     *
     * @param rutaId El ID de la ruta a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean delete(String rutaId) {
        boolean eliminada = rutaService.delete(rutaId);
        if (eliminada) {
            System.out.println("Ruta eliminada exitosamente.");
        } else {
            System.err.println("Error al eliminar la ruta.");
        }
        return eliminada;
    }
}
