package backend.Repositories;

import backend.Models.Ruta;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import main.Database.FirebaseInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Repositorio para manejar operaciones de persistencia relacionadas con las rutas.
 */
public class RutaRepository {
    private final Firestore db;
    private static final String COLLECTION_NAME = "Ruta";

    /**
     * Constructor que inicializa la instancia de Firestore.
     */
    public RutaRepository() {
        this.db = FirebaseInitializer.getInstance().getFirestore();
    }

    /**
     * Crea una nueva ruta en la colección "Ruta" de Firestore.
     *
     * @param ruta El objeto Ruta a almacenar.
     * @return true si la creación fue exitosa, false en caso contrario.
     */
    public boolean create(Ruta ruta) {
        if (ruta.getId() == null || ruta.getId().isEmpty()) {
            System.err.println("Error: El ID de la ruta no puede estar vacío.");
            return false;
        }

        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(ruta.getId())
                .set(ruta);

        try {
            WriteResult result = future.get();
            System.out.println("Ruta creada con ID: " + ruta.getId());
            System.out.println("Update time : " + result.getUpdateTime());
            return true;
        } catch (InterruptedException e) {
            System.err.println("Error: La operación fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            System.err.println("Error al crear la ruta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene una ruta existente de la colección "Ruta" de Firestore.
     *
     * @param rutaId El ID de la ruta a obtener.
     * @return Una instancia de Ruta si se encuentra, o null si no existe.
     */
    public Ruta get(String rutaId) {
        ApiFuture<DocumentSnapshot> future = db.collection(COLLECTION_NAME)
                .document(rutaId)
                .get();

        try {
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Ruta ruta = document.toObject(Ruta.class);
                return ruta;
            } else {
                return null;
            }
        } catch (InterruptedException e) {
            System.err.println("Error: La operación fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            System.err.println("Error al obtener la ruta: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene todas las rutas de la colección "Ruta".
     *
     * @return Una lista de instancias de Ruta.
     */
    public List<Ruta> getAll() {
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<Ruta> rutas = new ArrayList<>();
        try {
            QuerySnapshot querySnapshot = future.get();
            querySnapshot.getDocuments().forEach(document -> {
                Ruta ruta = document.toObject(Ruta.class);
                rutas.add(ruta);
            });
        } catch (InterruptedException e) {
            System.err.println("Error: La operación fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("Error al obtener todas las rutas: " + e.getMessage());
        }
        return rutas;
    }

    /**
     * Actualiza una ruta existente en la colección "Ruta" de Firestore.
     *
     * @param ruta El objeto Ruta con los datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean update(Ruta ruta) {
        if (ruta.getId() == null || ruta.getId().isEmpty()) {
            System.err.println("Error: El ID de la ruta no puede estar vacío.");
            return false;
        }

        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(ruta.getId())
                .set(ruta, SetOptions.merge());

        try {
            WriteResult result = future.get();
            System.out.println("Ruta actualizada con ID: " + ruta.getId());
            System.out.println("Update time : " + result.getUpdateTime());
            return true;
        } catch (InterruptedException e) {
            System.err.println("Error: La operación fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            System.err.println("Error al actualizar la ruta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza campos específicos de una ruta existente en la colección "Ruta" de Firestore.
     *
     * @param rutaId El ID de la ruta a actualizar.
     * @param nuevosCampos Un mapa de los campos y sus nuevos valores.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean updateFields(String rutaId, Map<String, Object> nuevosCampos) {
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(rutaId)
                .update(nuevosCampos);

        try {
            WriteResult result = future.get();
            System.out.println("Ruta con ID " + rutaId + " actualizada.");
            System.out.println("Update time : " + result.getUpdateTime());
            return true;
        } catch (InterruptedException e) {
            System.err.println("Error: La operación fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            System.err.println("Error al actualizar los campos de la ruta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina una ruta existente de la colección "Ruta" de Firestore.
     *
     * @param rutaId El ID de la ruta a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean delete(String rutaId) {
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(rutaId)
                .delete();

        try {
            WriteResult result = future.get();
            System.out.println("Ruta eliminada con ID: " + rutaId);
            System.out.println("Update time : " + result.getUpdateTime());
            return true;
        } catch (InterruptedException e) {
            System.err.println("Error: La operación fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            System.err.println("Error al eliminar la ruta: " + e.getMessage());
            return false;
        }
    }
}
