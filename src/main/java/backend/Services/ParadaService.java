package backend.Services;

import backend.Models.Parada;
import backend.Repositories.ParadaRepository;
import backend.Repositories.RutaRepository;

import java.util.List;
import java.util.Map;

public class  ParadaService {
    private final ParadaRepository paradaRepository;


    public ParadaService() {
        this.paradaRepository = new ParadaRepository();
    }

    /**
     * Crea una nueva parada después de validar los datos.
     *
     * @param nombre El nombre de la nueva parada.
     * @return Parada si la creación fue exitosa, null en caso contrario.
     */
    public Parada create(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("Error: El nombre de la parada no puede estar vacío.");
            return null;
        }

        Parada nuevaParada = new Parada(nombre);

        boolean creada = paradaRepository.create(nuevaParada);
        if (creada) {
            return nuevaParada;
        } else {
            return null;
        }
    }

    /**
     * Obtiene una parada por su ID.
     *
     * @param paradaId El ID de la parada a obtener.
     * @return La instancia de Parada si se encuentra, o null si no existe.
     */
    public Parada get(String paradaId) {
        if (paradaId == null || paradaId.trim().isEmpty()) {
            System.err.println("El ID de la parada no puede estar vacío.");
            return null;
        }

        return paradaRepository.get(paradaId);
    }

    /**
     * Actualiza campos específicos de una parada existente.
     *
     * @param paradaId El ID de la parada a actualizar.
     * @param nuevosCampos Un mapa de los campos y sus nuevos valores.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean updateFields(String paradaId, Map<String, Object> nuevosCampos) {
        if (paradaId == null || paradaId.trim().isEmpty()) {
            System.err.println("El ID de la parada no puede estar vacío.");
            return false;
        }

        if (nuevosCampos == null || nuevosCampos.isEmpty()) {
            System.err.println("Debe proporcionar al menos un campo para actualizar.");
            return false;
        }

        paradaRepository.updateFields(paradaId, nuevosCampos);

        return true;
    }

    /**
     * Elimina una parada existente por su ID.
     *
     * @param paradaId El ID de la parada a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean delete(String paradaId) {
        if (paradaId == null || paradaId.trim().isEmpty()) {
            System.err.println("El ID de la parada no puede estar vacío.");
            return false;
        }

        Parada paradaExistente = paradaRepository.get(paradaId);
        if (paradaExistente == null) {
            System.err.println("No se encontró la parada con ID: " + paradaId);
            return false;
        }

        paradaRepository.delete(paradaId);

        return true;
    }

    /**
     * Obtiene todas las paradas existentes.
     *
     * @return Una lista de instancias de Parada.
     */
    public List<Parada> getAllParadas() {
        return paradaRepository.getAll();
    }
}
