package frontend.Controllers;

import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.Excepciones.RutaDuplicadaException;
import backend.Models.Interfaces.Grafo;
import backend.Models.Parada;
import backend.Models.Ruta;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CreateRutaDialogController {
    @FXML
    private TextField tiempoField;

    @FXML
    private TextField distanciaField;

    @FXML
    private TextField costoField;

    @FXML
    private TextField transbordosField;

    @FXML
    private Label origenLabel;

    @FXML
    private Label destinoLabel;

    private Parada origen;
    private Parada destino;

    private Grafo grafoTransporte;

    private Stage dialogStage;
    private boolean isConfirmed = false;
    private Ruta ruta;

    /**
     * Establece el Stage del diálogo.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Establece la parada de origen.
     */
    public void setOrigen(Parada origen) {
        this.origen = origen;
        if (origenLabel != null) {
            origenLabel.setText(origen.getNombre());
        }
    }

    /**
     * Establece la parada de destino.
     */
    public void setDestino(Parada destino) {
        this.destino = destino;
        if (destinoLabel != null) {
            destinoLabel.setText(destino.getNombre());
        }
    }

    /**
     * Establece el grafo de transporte.
     */
    public void setGrafoTransporte(Grafo grafoTransporte) {
        this.grafoTransporte = grafoTransporte;
    }

    /**
     * Devuelve si el usuario confirmó la creación.
     */
    public boolean isConfirmed() {
        return isConfirmed;
    }

    /**
     * Devuelve la ruta creada.
     */
    public Ruta getRuta() {
        return ruta;
    }

    /**
     * Maneja la acción del botón "Crear".
     */
    @FXML
    private void handleCrear() {
        if (isInputValid()) {
            int tiempo = Integer.parseInt(tiempoField.getText().trim());
            int distancia = Integer.parseInt(distanciaField.getText().trim());
            double costo = Double.parseDouble(costoField.getText().trim());
            int transbordos = Integer.parseInt(transbordosField.getText().trim());

            ruta = new Ruta(origen, destino, tiempo, distancia, costo, transbordos);
            try {
                grafoTransporte.agregarRuta(ruta);
                isConfirmed = true;
                dialogStage.close();
            } catch (RutaDuplicadaException | ParadaInexistenteException e) {
                mostrarAlertaError("Error al Crear Ruta", e.getMessage());
            }
        }
    }

    /**
     * Maneja la acción del botón "Cancelar".
     */
    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    /**
     * Valida la entrada del usuario.
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (tiempoField.getText() == null || tiempoField.getText().trim().isEmpty()) {
            errorMessage += "El tiempo es obligatorio.\n";
        } else if (!tiempoField.getText().matches("\\d+")) {
            errorMessage += "El tiempo debe ser un número entero válido.\n";
        }

        if (distanciaField.getText() == null || distanciaField.getText().trim().isEmpty()) {
            errorMessage += "La distancia es obligatoria.\n";
        } else if (!distanciaField.getText().matches("\\d+")) {
            errorMessage += "La distancia debe ser un número entero válido.\n";
        }

        if (costoField.getText() == null || costoField.getText().trim().isEmpty()) {
            errorMessage += "El costo es obligatorio.\n";
        } else if (!costoField.getText().matches("\\d+(\\.\\d+)?")) {
            errorMessage += "El costo debe ser un número válido.\n";
        }

        if (transbordosField.getText() == null || transbordosField.getText().trim().isEmpty()) {
            errorMessage += "Los transbordos son obligatorios.\n";
        } else if (!transbordosField.getText().matches("\\d+")) {
            errorMessage += "Los transbordos deben ser un número entero válido.\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            mostrarAlertaError("Entrada Inválida", errorMessage);
            return false;
        }
    }

    /**
     * Muestra una alerta de error.
     */
    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
