package Database;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;

import java.io.InputStream;

public class FirebaseInitializer {
    private static FirebaseInitializer instance;
    private final Firestore firestore;

    private FirebaseInitializer() {
        try {
            InputStream serviceAccount = getClass().getResourceAsStream("/environment/firebaseCredentials.json");
            if (serviceAccount == null) {
                throw new RuntimeException("No se encontró el archivo de credenciales de Firebase.");
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            firestore = FirestoreClient.getFirestore();
            System.out.println("Firestore inicializado correctamente.");
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar Firebase: " + e.getMessage());
        }
    }

    public static synchronized FirebaseInitializer getInstance() {
        if (instance == null) {
            instance = new FirebaseInitializer();
        }
        return instance;
    }

    public Firestore getFirestore() {
        return firestore;
    }
}
