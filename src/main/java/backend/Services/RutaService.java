package backend.Services;

import backend.Controllers.ParadaController;
import backend.Models.Ruta;
import backend.Repositories.ParadaRepository;
import backend.Repositories.RutaRepository;

import java.util.List;
import java.util.Map;

/**
 * Servicio para manejar operaciones relacionadas con las rutas.
 */
public class RutaService {
    private final RutaRepository rutaRepository;
    private final ParadaRepository paradaRepository;

    /**
     * Constructor que inicializa la instancia de RutaRepository.
     */
    public RutaService() {
        this.rutaRepository = new RutaRepository();
        this.paradaRepository = new ParadaRepository();
    }

    /**
     * Crea una nueva ruta después de validar los datos.
     *
     * @param ruta      El objeto Parada de origen.
     * @return true si la creación fue exitosa, false en caso contrario.
     */
    public boolean create(Ruta ruta) {
        if (ruta.getOrigen() == null || ruta.getOrigen().getId().isEmpty()) {
            System.err.println("Error: La ruta debe tener una parada de origen válida.");
            return false;
        }

        if (ruta.getDestino() == null || ruta.getDestino().getId().isEmpty()) {
            System.err.println("Error: La ruta debe tener una parada de destino válida.");
            return false;
        }

        if(paradaRepository.get(ruta.getOrigen().getId()) == null){
            System.err.println("Error: El origen no se encontro en la base de datos.");
            return false;
        }

        if(paradaRepository.get(ruta.getDestino().getId()) == null){
            System.err.println("Error: El destino no se encontro en la base de datos.");
            return false;
        }

        if (ruta.getTiempo() <= 0) {
            System.err.println("Error: El tiempo debe ser un valor positivo.");
            return false;
        }

        if (ruta.getDistancia() <= 0) {
            System.err.println("Error: La distancia debe ser un valor positivo.");
            return false;
        }

        if (ruta.getCosto() < 0) {
            System.err.println("Error: El costo no puede ser negativo.");
            return false;
        }

        if (ruta.getTransbordos() < 0) {
            System.err.println("Error: El número de transbordos no puede ser negativo.");
            return false;
        }

        return rutaRepository.create(ruta);
    }

    /**
     * Obtiene todas las rutas existentes.
     *
     * @return Una lista de instancias de Ruta.
     */
    public List<Ruta> getAll() {
        return rutaRepository.getAll();
    }

    /**
     * Obtiene una ruta por su ID.
     *
     * @param rutaId El ID de la ruta a obtener.
     * @return La instancia de Ruta si se encuentra, o null si no existe.
     */
    public Ruta get(String rutaId) {
        if (rutaId == null || rutaId.trim().isEmpty()) {
            System.err.println("Error: El ID de la ruta no puede estar vacío.");
            return null;
        }
        return rutaRepository.get(rutaId);
    }


    /**
     * Actualiza campos específicos de una ruta existente.
     *
     * @param rutaId      El ID de la ruta a actualizar.
     * @param nuevosCampos Un mapa de los campos y sus nuevos valores.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean updateFields(String rutaId, Map<String, Object> nuevosCampos) {
        if (rutaId == null || rutaId.trim().isEmpty()) {
            System.err.println("Error: El ID de la ruta no puede estar vacío.");
            return false;
        }

        if (nuevosCampos == null || nuevosCampos.isEmpty()) {
            System.err.println("Error: Debe proporcionar al menos un campo para actualizar.");
            return false;
        }

        return rutaRepository.updateFields(rutaId, nuevosCampos);
    }

    /**
     * Elimina una ruta existente por su ID.
     *
     * @param rutaId El ID de la ruta a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean delete(String rutaId) {
        if (rutaId == null || rutaId.trim().isEmpty()) {
            System.err.println("Error: El ID de la ruta no puede estar vacío.");
            return false;
        }

        Ruta rutaExistente = rutaRepository.get(rutaId);
        if (rutaExistente == null) {
            System.err.println("Error: No se encontró la ruta con ID: " + rutaId);
            return false;
        }

        return rutaRepository.delete(rutaId);
    }
}
