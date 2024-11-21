package backend.Controllers;

import backend.Models.Parada;
import backend.Services.ParadaService;

import java.util.List;
import java.util.Map;

public class ParadaController {

    public ParadaController() {}

    /**
     * Crea una nueva parada después de validar los datos.
     *
     * @param nombre El nombre de la nueva parada.
     * @return true si la creación fue exitosa, false en caso contrario.
     */
    public boolean create(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("Error: El nombre de la parada no puede estar vacío.");
            return false;
        }

        boolean creada = ParadaService.create(nombre);

        if (creada) {
            System.out.println("Parada creada exitosamente.");
        } else {
            System.err.println("Error al crear la parada.");
        }

        return creada;
    }

    /**
     * Obtiene una parada por su ID.
     *
     * @param paradaId El ID de la parada a obtener.
     * @return La instancia de Parada si se encuentra, o null si no existe.
     */
    public Parada get(String paradaId) {
        if (paradaId == null || paradaId.trim().isEmpty()) {
            System.err.println("Error: El ID de la parada no puede estar vacío.");
            return null;
        }

        Parada parada = ParadaService.get(paradaId);

        if (parada != null) {
            System.out.println("Parada obtenida: " + parada);
        } else {
            System.out.println("No se encontró la parada con ID: " + paradaId);
        }

        return parada;
    }

    /**
     * Obtiene todas las paradas existentes.
     *
     * @return Una lista de instancias de Parada.
     */
    public List<Parada> getAll() {
        List<Parada> paradas = ParadaService.getAllParadas();

        if (paradas.isEmpty()) {
            System.out.println("No hay paradas registradas.");
        } else {
            System.out.println("Lista de paradas:");
            paradas.forEach(System.out::println);
        }

        return paradas;
    }

    /**
     * Actualiza una parada existente con nuevos datos.
     *
     * @param paradaId    El ID de la parada a actualizar.
     * @param nuevoNombre El nuevo nombre para la parada.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean update(String paradaId, String nuevoNombre) {
        if (paradaId == null || paradaId.trim().isEmpty()) {
            System.err.println("Error: El ID de la parada no puede estar vacío.");
            return false;
        }

        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            System.err.println("Error: El nuevo nombre de la parada no puede estar vacío.");
            return false;
        }

        boolean actualizada = ParadaService.update(paradaId, nuevoNombre);

        if (actualizada) {
            System.out.println("Parada actualizada exitosamente.");
        } else {
            System.err.println("Error al actualizar la parada.");
        }

        return actualizada;
    }

    /**
     * Actualiza campos específicos de una parada existente.
     *
     * @param paradaId     El ID de la parada a actualizar.
     * @param nuevosCampos Un mapa de los campos y sus nuevos valores.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean updateFields(String paradaId, Map<String, Object> nuevosCampos) {
        if (paradaId == null || paradaId.trim().isEmpty()) {
            System.err.println("Error: El ID de la parada no puede estar vacío.");
            return false;
        }

        if (nuevosCampos == null || nuevosCampos.isEmpty()) {
            System.err.println("Error: Debe proporcionar al menos un campo para actualizar.");
            return false;
        }

        boolean actualizada = ParadaService.updateFields(paradaId, nuevosCampos);

        if (actualizada) {
            System.out.println("Parada actualizada exitosamente.");
        } else {
            System.err.println("Error al actualizar los campos de la parada.");
        }

        return actualizada;
    }

    /**
     * Elimina una parada existente por su ID.
     *
     * @param paradaId El ID de la parada a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean delete(String paradaId) {
        if (paradaId == null || paradaId.trim().isEmpty()) {
            System.err.println("Error: El ID de la parada no puede estar vacío.");
            return false;
        }

        boolean eliminada = ParadaService.delete(paradaId);

        if (eliminada) {
            System.out.println("Parada eliminada exitosamente.");
        } else {
            System.err.println("Error al eliminar la parada.");
        }

        return eliminada;
    }
}
