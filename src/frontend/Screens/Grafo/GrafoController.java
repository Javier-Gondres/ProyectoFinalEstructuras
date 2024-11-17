package frontend.Screens.Grafo;

import backend.Models.GrafoTransporte;
import backend.Models.Parada;
import backend.Models.ResultadoRuta;
import backend.Models.Excepciones.ParadaDuplicadaException;
import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.Excepciones.RutaDuplicadaException;
import backend.Models.Excepciones.RutaInexistenteException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class GrafoController {
    @FXML
    private TextField txtNombreParada;

    @FXML
    private ComboBox<Parada> cmbOrigenRuta;

    @FXML
    private ComboBox<Parada> cmbDestinoRuta;

    @FXML
    private TextField txtTiempoRuta;

    @FXML
    private TextField txtDistanciaRuta;

    @FXML
    private TextField txtCostoRuta;

    @FXML
    private TextField txtTransbordosRuta;

    @FXML
    private ComboBox<Parada> cmbEliminarParada;

    @FXML
    private ComboBox<Parada> cmbEliminarOrigenRuta;

    @FXML
    private ComboBox<Parada> cmbEliminarDestinoRuta;

    @FXML
    private ComboBox<Parada> cmbBuscarOrigen;

    @FXML
    private ComboBox<Parada> cmbBuscarDestino;

    @FXML
    private TextArea txtResultadoRuta;

    private GrafoTransporte grafo;

    private ObservableList<Parada> paradasObservable;

    public GrafoController() {
        grafo = new GrafoTransporte();
        paradasObservable = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        actualizarComboBoxs();
    }

    /**
     * Maneja la acción de agregar una nueva parada.
     */
    @FXML
    private void handleAgregarParada() {
        String nombre = txtNombreParada.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El nombre de la parada no puede estar vacío.");
            return;
        }

        Parada nuevaParada = new Parada(nombre);

        try {
            grafo.agregarParada(nuevaParada);
            paradasObservable.add(nuevaParada);
            actualizarComboBoxs();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Parada agregada exitosamente.");
            txtNombreParada.clear();
        } catch (ParadaDuplicadaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    /**
     * Maneja la acción de agregar una nueva ruta.
     */
    @FXML
    private void handleAgregarRuta() {
        Parada origen = cmbOrigenRuta.getValue();
        Parada destino = cmbDestinoRuta.getValue();
        String tiempoStr = txtTiempoRuta.getText().trim();
        String distanciaStr = txtDistanciaRuta.getText().trim();
        String costoStr = txtCostoRuta.getText().trim();
        String transbordosStr = txtTransbordosRuta.getText().trim();

        // Validación de campos
        if (origen == null || destino == null || tiempoStr.isEmpty() ||
                distanciaStr.isEmpty() || costoStr.isEmpty() || transbordosStr.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Todos los campos deben estar completos.");
            return;
        }

        if (origen.equals(destino)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "La parada de origen y destino no pueden ser las mismas.");
            return;
        }

        int tiempo, distancia, transbordos;
        double costo;

        try {
            tiempo = Integer.parseInt(tiempoStr);
            distancia = Integer.parseInt(distanciaStr);
            costo = Double.parseDouble(costoStr);
            transbordos = Integer.parseInt(transbordosStr);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Los campos de tiempo, distancia, costo y transbordos deben ser numéricos.");
            return;
        }

        try {
            grafo.agregarRuta(origen, destino, tiempo, distancia, costo, transbordos);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Ruta agregada exitosamente.");


            cmbOrigenRuta.setValue(null);
            cmbDestinoRuta.setValue(null);
            txtTiempoRuta.clear();
            txtDistanciaRuta.clear();
            txtCostoRuta.clear();
            txtTransbordosRuta.clear();

            actualizarComboBoxs();

        } catch (ParadaInexistenteException | RutaDuplicadaException | IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    /**
     * Maneja la acción de eliminar una parada seleccionada.
     */
    @FXML
    private void handleEliminarParada() {
        Parada paradaSeleccionada = cmbEliminarParada.getValue();

        if (paradaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debe seleccionar una parada para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar la parada '" + paradaSeleccionada.getNombre() + "'?");
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    grafo.eliminarParada(paradaSeleccionada);
                    paradasObservable.remove(paradaSeleccionada);
                    actualizarComboBoxs();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Parada eliminada exitosamente.");
                } catch (ParadaInexistenteException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            }
        });
    }

    /**
     * Maneja la acción de eliminar una ruta seleccionada.
     */
    @FXML
    private void handleEliminarRuta() {
        Parada origen = cmbEliminarOrigenRuta.getValue();
        Parada destino = cmbEliminarDestinoRuta.getValue();

        if (origen == null || destino == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debe seleccionar tanto el origen como el destino de la ruta a eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar la ruta de '" + origen.getNombre() + "' a '" + destino.getNombre() + "'?");
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    grafo.eliminarRuta(origen, destino);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Ruta eliminada exitosamente.");
                    cmbEliminarOrigenRuta.setValue(null);
                    cmbEliminarDestinoRuta.setValue(null);
                } catch (ParadaInexistenteException | RutaInexistenteException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            }
        });
    }

    /**
     * Maneja la acción de buscar una ruta entre dos paradas utilizando Dijkstra.
     */
    @FXML
    private void handleBuscarRuta() {
        Parada origen = cmbBuscarOrigen.getValue();
        Parada destino = cmbBuscarDestino.getValue();

        if (origen == null || destino == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debe seleccionar tanto el origen como el destino.");
            return;
        }

        if (origen.equals(destino)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "La parada de origen y destino no pueden ser las mismas.");
            return;
        }

        double pesoTiempo = 1.0;
        double pesoDistancia = 1.0;
        double pesoTransbordos = 1.0;
        double pesoCosto = 1.0;

        try {
            ResultadoRuta resultado = grafo.obtenerRutaEntreParadasConDijkstra(origen, destino, pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);

            if (resultado != null) {
                txtResultadoRuta.setText(resultado.toString());
            } else {
                txtResultadoRuta.setText("No se encontró una ruta entre las paradas seleccionadas.");
            }

        } catch (ParadaInexistenteException | IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Actualiza los ComboBox con las paradas existentes en el grafo.
     */
    private void actualizarComboBoxs() {
        ObservableList<Parada> paradas = FXCollections.observableArrayList(grafo.obtenerParadas());

        cmbOrigenRuta.setItems(paradas);
        cmbDestinoRuta.setItems(paradas);
        cmbEliminarParada.setItems(paradas);
        cmbEliminarOrigenRuta.setItems(paradas);
        cmbEliminarDestinoRuta.setItems(paradas);
        cmbBuscarOrigen.setItems(paradas);
        cmbBuscarDestino.setItems(paradas);
    }
}

