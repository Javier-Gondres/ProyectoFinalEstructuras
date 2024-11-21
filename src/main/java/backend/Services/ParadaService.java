package backend.Services;

import backend.Models.Parada;
import backend.Repositories.ParadaRepository;

import java.util.List;
import java.util.Map;

public class  ParadaService {

    /**
     * Crea una nueva parada después de validar los datos.
     *
     * @param nombre El nombre de la nueva parada.
     * @return true si la creación fue exitosa, false en caso contrario.
     */
    public static boolean create(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("El nombre de la parada no puede estar vacío.");
            return false;
        }

        Parada nuevaParada = new Parada(nombre);

        ParadaRepository.create(nuevaParada);

        return true;
    }

    /**
     * Obtiene una parada por su ID.
     *
     * @param paradaId El ID de la parada a obtener.
     * @return La instancia de Parada si se encuentra, o null si no existe.
     */
    public static Parada get(String paradaId) {
        if (paradaId == null || paradaId.trim().isEmpty()) {
            System.err.println("El ID de la parada no puede estar vacío.");
            return null;
        }

        return ParadaRepository.get(paradaId);
    }

    /**
     * Actualiza una parada existente con nuevos datos.
     *
     * @param paradaId El ID de la parada a actualizar.
     * @param nuevoNombre El nuevo nombre para la parada.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public static boolean update(String paradaId, String nuevoNombre) {
        if (paradaId == null || paradaId.trim().isEmpty()) {
            System.err.println("El ID de la parada no puede estar vacío.");
            return false;
        }

        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            System.err.println("El nuevo nombre de la parada no puede estar vacío.");
            return false;
        }

        Parada paradaExistente = ParadaRepository.get(paradaId);
        if (paradaExistente == null) {
            System.err.println("No se encontró la parada con ID: " + paradaId);
            return false;
        }

        paradaExistente.setNombre(nuevoNombre);

        ParadaRepository.update(paradaExistente);

        return true;
    }

    /**
     * Actualiza campos específicos de una parada existente.
     *
     * @param paradaId El ID de la parada a actualizar.
     * @param nuevosCampos Un mapa de los campos y sus nuevos valores.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public static boolean updateFields(String paradaId, Map<String, Object> nuevosCampos) {
        if (paradaId == null || paradaId.trim().isEmpty()) {
            System.err.println("El ID de la parada no puede estar vacío.");
            return false;
        }

        if (nuevosCampos == null || nuevosCampos.isEmpty()) {
            System.err.println("Debe proporcionar al menos un campo para actualizar.");
            return false;
        }

        ParadaRepository.updateFields(paradaId, nuevosCampos);

        return true;
    }

    /**
     * Elimina una parada existente por su ID.
     *
     * @param paradaId El ID de la parada a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public static boolean delete(String paradaId) {
        if (paradaId == null || paradaId.trim().isEmpty()) {
            System.err.println("El ID de la parada no puede estar vacío.");
            return false;
        }

        Parada paradaExistente = ParadaRepository.get(paradaId);
        if (paradaExistente == null) {
            System.err.println("No se encontró la parada con ID: " + paradaId);
            return false;
        }

        ParadaRepository.delete(paradaId);

        return true;
    }

    /**
     * Obtiene todas las paradas existentes.
     *
     * @return Una lista de instancias de Parada.
     */
    public static List<Parada> getAllParadas() {
        return ParadaRepository.getAll();
    }
}
