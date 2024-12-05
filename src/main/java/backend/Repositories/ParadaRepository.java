package backend.Repositories;

import backend.Models.Parada;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import Database.FirebaseInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ParadaRepository {
    private final Firestore db;
    private final String COLLECTION_NAME = "Parada";

    public ParadaRepository() {
        this.db = FirebaseInitializer.getInstance().getFirestore();
    }

    /**
     * Crea una nueva parada en la colección "Parada" de Firestore.
     *
     * @param parada El objeto Parada a almacenar.
     */
    public boolean create(Parada parada) {
        if (parada.getId() == null || parada.getId().isEmpty()) {
            System.err.println("El ID de la parada no puede estar vacío.");
            return false;
        }

        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(parada.getId())
                .set(parada);

        try {
            WriteResult result = future.get();
            System.out.println("Parada creada con ID: " + parada.getId());
            System.out.println("Update time : " + result.getUpdateTime());
            return true;
        } catch (InterruptedException e) {
            System.err.println("La operación fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            System.err.println("Error al crear la parada: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza una parada existente en la colección "Parada" de Firestore.
     *
     * @param parada El objeto Parada con los datos actualizados.
     */
    public  void update(Parada parada) {
        if (parada.getId() == null || parada.getId().isEmpty()) {
            System.err.println("El ID de la parada no puede estar vacío.");
            return;
        }

        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(parada.getId())
                .set(parada, SetOptions.merge());

        try {
            WriteResult result = future.get();
            System.out.println("Parada actualizada con ID: " + parada.getId());
            System.out.println("Update time : " + result.getUpdateTime());
        } catch (InterruptedException e) {
            System.err.println("La operación fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("Error al actualizar la parada: " + e.getMessage());
        }
    }

    /**
     * Actualiza campos específicos de una parada existente en la colección "Parada" de Firestore.
     *
     * @param paradaId     El ID de la parada a actualizar.
     * @param nuevosCampos Un mapa de los campos y sus nuevos valores.
     */
    public  void updateFields(String paradaId, Map<String, Object> nuevosCampos) {
        if (paradaId == null || paradaId.isEmpty()) {
            System.err.println("El ID de la parada no puede estar vacío.");
            return;
        }

        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(paradaId)
                .update(nuevosCampos);

        try {
            WriteResult result = future.get();
            System.out.println("Campos de la parada con ID " + paradaId + " actualizados.");
            System.out.println("Update time : " + result.getUpdateTime());
        } catch (InterruptedException e) {
            System.err.println("La operación fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("Error al actualizar los campos de la parada: " + e.getMessage());
        }
    }

    /**
     * Elimina una parada existente de la colección "Parada" de Firestore.
     *
     * @param paradaId El ID de la parada a eliminar.
     */
    public  void delete(String paradaId) {
        if (paradaId == null || paradaId.isEmpty()) {
            System.err.println("El ID de la parada no puede estar vacío.");
            return;
        }

        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(paradaId)
                .delete();

        try {
            WriteResult result = future.get();
            System.out.println("Parada eliminada con ID: " + paradaId);
            System.out.println("Update time : " + result.getUpdateTime());
        } catch (InterruptedException e) {
            System.err.println("La operación fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("Error al eliminar la parada: " + e.getMessage());
        }
    }

    /**
     * Obtiene una parada existente de la colección "Parada" de Firestore.
     *
     * @param paradaId El ID de la parada a obtener.
     * @return Una instancia de Parada si se encuentra, o null si no existe.
     */
    public  Parada get(String paradaId) {
        if (paradaId == null || paradaId.isEmpty()) {
            System.err.println("El ID de la parada no puede estar vacío.");
            return null;
        }

        ApiFuture<DocumentSnapshot> future = db.collection(COLLECTION_NAME)
                .document(paradaId)
                .get();

        try {
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Parada parada = document.toObject(Parada.class);
                System.out.println("Parada obtenida con ID: " + paradaId);
                System.out.println(parada);
                return parada;
            } else {
                System.out.println("No se encontró la parada con ID: " + paradaId);
                return null;
            }
        } catch (InterruptedException e) {
            System.err.println("La operación fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            System.err.println("Error al obtener la parada: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene todas las paradas de la colección "Parada".
     *
     * @return Una lista de instancias de Parada.
     */
    public  List<Parada> getAll() {
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<Parada> paradas = new ArrayList<>();
        try {
            QuerySnapshot querySnapshot = future.get();
            querySnapshot.getDocuments().forEach(document -> {
                Parada parada = document.toObject(Parada.class);
                paradas.add(parada);
            });
        } catch (InterruptedException e) {
            System.err.println("La operación fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("Error al obtener todas las paradas: " + e.getMessage());
        }
        return paradas;
    }
}
