package frontend.Controllers;

import backend.Models.Excepciones.*;
import backend.Models.*;
import frontend.Controllers.enums.Mode;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.*;
import org.graphstream.ui.fx_viewer.util.FxMouseManager;
import org.graphstream.ui.geom.*;
import org.graphstream.ui.graphicGraph.*;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.view.*;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.util.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.UnaryOperator;

import backend.Controllers.GrafoController;

public class FrontendGrafoController implements ViewerListener {

    @FXML
    private StackPane graphContainer;
    @FXML
    private VBox sliderBar;
    @FXML
    private VBox sideBar;
    @FXML
    private Button toggleMenuButton;
    @FXML
    private Button guardarParadaButton;
    @FXML
    private Button salirButton;
    @FXML
    private Button toggleClickable;
    @FXML
    private Button toggleRemove;
    @FXML
    private Button toggleAdd;
    @FXML
    private ChoiceBox<String> addChoiceBox;
    @FXML
    private Pane panelActualizarParada;
    @FXML
    private Pane panelCrearRuta;
    @FXML
    private Pane panelActualizarRuta;
    @FXML
    private Button crearRutaButton;
    @FXML
    private TextField textFieldNombreParada;
    @FXML
    private TextField textFieldIdParada;
    @FXML
    private Button eliminarParadaButton;
    @FXML
    private TextField textFieldIdRuta;
    @FXML
    private Spinner<Integer> spinnerTiempo;
    @FXML
    private Spinner<Double> spinnerCosto;
    @FXML
    private Spinner<Integer> spinnerTranbordos;
    @FXML
    private Spinner<Integer> spinnerDistancia;
    @FXML
    private Button actualizarRutaButton;
    @FXML
    private Button eliminarRutaButton;


    @FXML
    public Button toggleCaminoMasCorto;
    @FXML
    public Pane panelBuscarCaminoMasCorto;
    @FXML
    public Button buscarButton;
    @FXML
    public Button verInformacionButton;
    @FXML
    public TextField textFieldNombreOrigen;
    @FXML
    public TextField textFieldNombreDestino;
    @FXML
    public CheckBox checkBoxTiempo;
    @FXML
    public CheckBox checkBoxDistania;
    @FXML
    public CheckBox checkBoxCosto;
    @FXML
    public CheckBox checkBoxTransbordos;

    private boolean isSliderBarVisible = false;
    private Graph graph;
    private FxViewer viewer;
    private FxViewPanel view;
    private ViewerPipe fromViewer;
    private ExecutorService executorService;
    private Parada origenSeleccionado = null;
    private Parada destinoSeleccionado = null;
    private Parada paradaSeleccionada = null;
    private Ruta rutaSeleccionada = null;
    private Mode currentMode = Mode.ADD;
    private final String CHOICE_CREAR_PARADA = "Crear Parada";
    private final String CHOICE_CREAR_RUTA = "Crear Ruta";
    ResultadoRuta resultadoDelCaminoMasCorto = null;
    private final GrafoController backendGrafoController = GrafoController.getInstance();

    /**
     * Método de inicialización que se ejecuta después de cargar el FXML.
     */
    @FXML
    public void initialize() {
        setupUIComponents();
        setupGraphStream();
        setupViewerPipe();
        setupEventHandlers();
        loadData();
    }

    /**
     * Configura los componentes de la interfaz de usuario.
     */
    private void setupUIComponents() {
        showSideBar();
        sliderBar.setTranslateX(-sliderBar.getPrefWidth());

        addChoiceBox.getItems().addAll(CHOICE_CREAR_PARADA, CHOICE_CREAR_RUTA);
        addChoiceBox.setValue(CHOICE_CREAR_PARADA);
        setActiveToggle(Mode.ADD);

        handleShowPane(panelActualizarParada);
        initializeSpinners();
    }

    /**
     * Configura GraphStream, incluyendo el grafo y la vista.
     */
    private void setupGraphStream() {
        graph = new SingleGraph("Grafo de Transporte");
        graph.setStrict(false);
        graph.setAutoCreate(true);

        loadCSS();

        viewer = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        view = (FxViewPanel) viewer.addDefaultView(false);
        viewer.getDefaultView().enableMouseOptions();

        view.setMouseManager(new CustomMouseManager());
        graphContainer.getChildren().add(view);
    }

    /**
     * Carga el estilo CSS para el grafo.
     */
    private void loadCSS() {
        String css = """
                /* Estilo base para todos los nodos */
                node {
                    size: 35px;
                    shape: circle;
                    stroke-mode: plain;
                    stroke-color: black;
                    stroke-width: 1px;
                    fill-color: #ddd622;
                    text-alignment: center;
                    text-size: 12px;
                    text-color: #00171f;
                    text-style: bold;
                    text-padding: 2px;
                    text-offset: 0px, 30px;
                    shadow-mode: plain;
                    shadow-color: #999;
                    shadow-width: 0px;
                    shadow-offset: 3px, -3px;
                }

                /* Estilos específicos por clase */
                node.importante {
                    fill-color: rgb(231, 76, 60);
                    size: 45px;
                    text-size: 14px;
                    text-offset: 0px, 35px;
                    stroke-width: 2px;
                    stroke-color: rgb(192, 57, 43);
                }

                node.warning {
                    fill-color: rgb(241, 196, 15);
                    shape: diamond;
                    text-color: black;
                    text-size: 12px;
                    text-offset: 0px, 35px;
                }

                node.destacado {
                    fill-color: #2980b9;
                    size: 40px;
                    text-color: #2980b9;
                    text-offset: 0px, 35px;
                    stroke-mode: dots;
                    stroke-width: 1px;
                }

                node.origen {
                    fill-color: #00171f;
                    text-color: #00171f;
                    size: 40px;
                    text-offset: 0px, 35px;
                }

                node.destino {
                    fill-color: #adebff;
                    text-color: #adebff;
                    size: 40px;
                    text-offset: 0px, 35px;
                }

                /* Estilo para las aristas */
                edge {
                    fill-color: #666;
                    size: 2px;
                    arrow-shape: arrow;
                    arrow-size: 10px, 4px;
                }

                /* Estilo para aristas específicas */
                edge.importante {
                    fill-color: red;
                    size: 3px;
                }

                /* Estados de hover */
                node:hover {
                    stroke-mode: plain;
                    stroke-width: 3px;
                    stroke-color: yellow;
                }

                edge:hover {
                    fill-color: yellow;
                    size: 3px;
                }
                """;
        graph.setAttribute("ui.stylesheet", css);
    }

    /**
     * Configura el ViewerPipe para escuchar eventos de GraphStream.
     */
    private void setupViewerPipe() {
        fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(graph);

        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    fromViewer.pump();
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Configura los manejadores de eventos de la interfaz de usuario.
     */
    private void setupEventHandlers() {
        toggleAdd.setOnAction(_ -> {
            if (addChoiceBox.getValue().equals(CHOICE_CREAR_PARADA)) {
                handleShowPane(panelActualizarParada);
            } else {
                handleShowPane(panelCrearRuta);
            }
            switchMode(Mode.ADD);
        });
        toggleRemove.setOnAction(_ -> {
            if (addChoiceBox.getValue().equals(CHOICE_CREAR_PARADA)) {
                handleShowPane(panelActualizarParada);
            } else {
                handleShowPane(panelActualizarRuta);
            }
            switchMode(Mode.REMOVE);
        });
        toggleClickable.setOnAction(_ -> {
            if (addChoiceBox.getValue().equals(CHOICE_CREAR_PARADA)) {
                handleShowPane(panelActualizarParada);
            } else {
                handleShowPane(panelActualizarRuta);
            }
            switchMode(Mode.CLICKABLE);
        });
        toggleCaminoMasCorto.setOnAction(_ -> {
            switchMode(Mode.SEARCH);
            handleShowPane(panelBuscarCaminoMasCorto);
        });

        toggleMenuButton.setOnAction(_ -> toggleSliderBar());

        guardarParadaButton.setOnAction(_ -> updateParada());
        eliminarParadaButton.setOnAction(_ -> removeParada());

        crearRutaButton.setOnAction(_ -> createRuta());
        actualizarRutaButton.setOnAction(_ -> updateRuta());
        eliminarRutaButton.setOnAction(_ -> removeRuta());

        buscarButton.setOnAction(_ -> handleDjikstra());
        verInformacionButton.setOnAction(_ -> mostrarInformacionDeBusqueda());

        addChoiceBox.getSelectionModel().
                selectedItemProperty().
                addListener((_, _, newValue) ->
                {
                    resetUI();
                    if (newValue.equals(CHOICE_CREAR_RUTA)) {
                        handleShowPane(panelCrearRuta);
                        crearRutaButton.setDisable(true);
                    } else if (newValue.equals(CHOICE_CREAR_PARADA)) {
                        handleShowPane(panelActualizarParada);
                    }
                });

        salirButton.setOnAction(_ -> salir());
    }

    /**
     * Cambia el modo actual y actualiza la interfaz.
     *
     * @param mode El modo al que se cambiará.
     */
    private void switchMode(Mode mode) {
        currentMode = mode;
        resetUI();
        setActiveToggle(mode);
    }

    private void hideElement(javafx.scene.Node element) {
        element.setVisible(false);
        element.setManaged(false);
    }

    private void showElement(javafx.scene.Node element) {
        element.setVisible(true);
        element.setManaged(true);
    }

    private void handleShowPane(Pane pane) {
        hideElement(panelCrearRuta);
        hideElement(panelActualizarRuta);
        hideElement(panelActualizarParada);
        hideElement(panelBuscarCaminoMasCorto);

        showElement(pane);
    }

    private void toggleSliderBar() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sliderBar);
        if (isSliderBarVisible) {
            transition.setToX(-sliderBar.getPrefWidth());
            isSliderBarVisible = false;
            showSideBar();
        } else {
            transition.setToX(0);
            isSliderBarVisible = true;
            hideSideBar();
        }
        transition.play();
    }

    private void setActiveToggle(Mode mode) {
        toggleAdd.getStyleClass().remove("active-button");
        toggleRemove.getStyleClass().remove("active-button-trash");
        toggleClickable.getStyleClass().remove("active-button");
        toggleCaminoMasCorto.getStyleClass().remove("active-button");

        switch (mode) {
            case ADD -> toggleAdd.getStyleClass().add("active-button");
            case REMOVE -> toggleRemove.getStyleClass().add("active-button-trash");
            case CLICKABLE -> toggleClickable.getStyleClass().add("active-button");
            case SEARCH -> toggleCaminoMasCorto.getStyleClass().add("active-button");
        }
    }

    private void hideSideBar() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sideBar);
        transition.setToX(sideBar.getPrefWidth());
        transition.play();
    }

    private void showSideBar() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sideBar);
        transition.setToX(0);
        transition.play();
    }

    /**
     * Maneja el evento cuando se hace clic en un nodo.
     *
     * @param nodoId El ID del nodo que fue clicado.
     */
    public void handleNodeClicked(String nodoId) {
        Node clickedNode = graph.getNode(nodoId);
        if (clickedNode == null) {
            return;
        }
        Parada parada = (Parada) clickedNode.getAttribute("parada");
        if (parada == null) {
            return;
        }

        resetRuta();

        switch (currentMode) {
            case ADD -> handleAddMode(parada, clickedNode);
            case REMOVE -> handleRemoveMode(parada, clickedNode);
            case CLICKABLE -> handleClickableMode(parada, clickedNode);
            case SEARCH -> handleSearchMode(parada, clickedNode);
        }
    }

    private void handleAddMode(Parada parada, Node clickedNode) {
        if (addChoiceBox.getValue().equals(CHOICE_CREAR_RUTA)) {
            handleShowPane(panelCrearRuta);
            selectOrigenDestino(parada, clickedNode);
            crearRutaButton.setDisable(origenSeleccionado == null || destinoSeleccionado == null);
        }
    }

    private void handleRemoveMode(Parada parada, Node clickedNode) {
        if (paradaSeleccionada != null) {
            Node nodoAnterior = graph.getNode(paradaSeleccionada.getId());
            if (nodoAnterior != null) {
                nodoAnterior.removeAttribute("ui.class");
            }
        }

        paradaSeleccionada = parada;
        clickedNode.setAttribute("ui.class", "importante");
        handleShowPane(panelActualizarParada);
        textFieldIdParada.setText(parada.getId());
        textFieldNombreParada.setText(parada.getNombre());
        textFieldNombreParada.setDisable(true);

        showElement(eliminarParadaButton);
        hideElement(guardarParadaButton);
        eliminarParadaButton.setDisable(false);
    }

    private void handleClickableMode(Parada parada, Node clickedNode) {
        if (paradaSeleccionada != null) {
            Node nodoAnterior = graph.getNode(paradaSeleccionada.getId());
            if (nodoAnterior != null) {
                nodoAnterior.removeAttribute("ui.class");
            }
        }

        paradaSeleccionada = parada;
        clickedNode.setAttribute("ui.class", "destacado");
        handleShowPane(panelActualizarParada);
        textFieldIdParada.setText(parada.getId());
        textFieldNombreParada.setText(parada.getNombre());
        textFieldNombreParada.setDisable(false);
        guardarParadaButton.setDisable(false);

        showElement(guardarParadaButton);
        hideElement(eliminarParadaButton);
    }

    private void handleSearchMode(Parada parada, Node clickedNode) {
        handleShowPane(panelBuscarCaminoMasCorto);
        selectOrigenDestino(parada, clickedNode);

        if (origenSeleccionado != null) {
            textFieldNombreOrigen.setText(origenSeleccionado.getNombre());
        } else {
            textFieldNombreOrigen.setText(null);
        }
        if (destinoSeleccionado != null) {
            textFieldNombreDestino.setText(destinoSeleccionado.getNombre());
        } else {
            textFieldNombreDestino.setText(null);
        }

        buscarButton.setDisable(origenSeleccionado == null || destinoSeleccionado == null);
    }

    private void selectOrigenDestino(Parada parada, Node clickedNode) {
        if (origenSeleccionado == null) {
            origenSeleccionado = parada;
            clickedNode.setAttribute("ui.class", "origen");
        } else if (origenSeleccionado.getId().equals(parada.getId())) {
            if (destinoSeleccionado == null) {
                clickedNode.removeAttribute("ui.class");
                origenSeleccionado = null;
            } else {
                origenSeleccionado = destinoSeleccionado;
                Node nodoOrigen = graph.getNode(origenSeleccionado.getId());
                if (nodoOrigen != null) {
                    nodoOrigen.setAttribute("ui.class", "origen");
                }
                destinoSeleccionado = null;
                clickedNode.removeAttribute("ui.class");
            }
        } else if (destinoSeleccionado == null) {
            destinoSeleccionado = parada;
            clickedNode.setAttribute("ui.class", "destino");
        } else if (destinoSeleccionado.getId().equals(parada.getId())) {
            clickedNode.removeAttribute("ui.class");
            destinoSeleccionado = null;
        } else {
            Node nodoDestinoAnterior = graph.getNode(destinoSeleccionado.getId());
            if (nodoDestinoAnterior != null) {
                nodoDestinoAnterior.removeAttribute("ui.class");
            }
            destinoSeleccionado = parada;
            clickedNode.setAttribute("ui.class", "destino");
        }
    }

    private void handleEdgeClicked(String edgeId) {
        handleShowPane(panelActualizarRuta);
        resetParadas();
        resetEdgesFromBusqueda();

        Edge edge = graph.getEdge(edgeId);
        if (edge == null) {
            System.err.println("La arista es null");
            return;
        }

        Ruta ruta = (Ruta) edge.getAttribute("ruta");
        if (ruta == null) {
            System.err.println("La ruta es null");
            return;
        }

        if (rutaSeleccionada != null) {
            Edge edgeAnterior = graph.getEdge(rutaSeleccionada.getId());
            if (edgeAnterior != null) {
                edgeAnterior.removeAttribute("ui.class");
            }
        }

        if (rutaSeleccionada != null && rutaSeleccionada.getId().equals(edgeId)) {
            rutaSeleccionada = null;
        } else {
            rutaSeleccionada = ruta;
            edge.setAttribute("ui.class", "importante");
        }

        if (currentMode == Mode.REMOVE) {
            if (rutaSeleccionada != null) {
                configurarPanelRutaParaEliminar();
            } else {
                desactivarPanelRutaParaEliminar();
            }
        } else {
            if (rutaSeleccionada != null) {
                configurarPanelRutaParaActualizar();
            } else {
                desactivarPanelRutaParaActualizar();
            }
        }
    }

    /**
     * Añade un nuevo nodo (parada) al grafo.
     *
     * @param nombre El nombre de la nueva parada.
     */
    public void addNode(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nombre inválido", null, "El nombre de la parada no puede estar vacío.");
            return;
        }

        try {
            Parada nuevaParada = backendGrafoController.crearParada(nombre.trim());
            if (nuevaParada == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "No se pudo crear la parada.");
                return;
            }
            String nodeId = nuevaParada.getId();
            Node nodo = graph.addNode(nodeId);
            nodo.setAttribute("parada", nuevaParada);
            nodo.setAttribute("ui.label", nuevaParada.getNombre());
            nodo.setAttribute("layout.weight", 1);
        } catch (ParadaDuplicadaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo crear la parada", null, "Ya existe una parada con este nombre");
        }

    }

    /**
     * Actualiza el nombre de una parada seleccionada.
     */
    public void updateParada() {
        if (paradaSeleccionada == null) {
            System.err.println("La parada seleccionada para modificar es NULL");
            return;
        }

        String nuevoNombre = textFieldNombreParada.getText();
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nombre inválido", null, "El nombre no puede estar vacío.");
            return;
        }

        if (paradaSeleccionada.getNombre().equals(nuevoNombre)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sin cambios", null, "No se han realizado cambios en la parada.");
            return;
        }

        try {
            boolean actualizado = backendGrafoController.actualizarParada(paradaSeleccionada.getId(), nuevoNombre.trim());
            if (actualizado) {
                Node nodo = graph.getNode(paradaSeleccionada.getId());
                if (nodo != null) {
                    nodo.setAttribute("ui.label", nuevoNombre);
                }
                mostrarAlerta(Alert.AlertType.INFORMATION, "Parada Actualizada", null, "La parada se ha actualizado exitosamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al actualizar", null, "No se pudo actualizar la parada.");
            }
        } catch (ParadaDuplicadaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al actualizar", null, "Ya existe una parada con ese nombre");
        } catch (ParadaInexistenteException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al actualizar", null, "La parada no existe");
        }

    }

    /**
     * Elimina la parada seleccionada.
     */
    public void removeParada() {
        if (paradaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "No se pudo eliminar la parada. Inténtelo más tarde.");
            System.err.println("No se pudo eliminar la parada porque es null");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de eliminación");
        alert.setHeaderText("");
        alert.setContentText("¿Está seguro que desea eliminar la parada '" + paradaSeleccionada.getNombre() + "' y todas sus rutas asociadas?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            boolean eliminada = backendGrafoController.eliminarParada(paradaSeleccionada.getId());
            if (eliminada) {
                graph.removeNode(paradaSeleccionada.getId());
                resetParadas();
                textFieldIdParada.setText(null);
                textFieldNombreParada.setText(null);
                eliminarParadaButton.setDisable(true);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Parada Eliminada", null, "La parada y sus rutas asociadas han sido eliminadas.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "No se pudo eliminar la parada. Inténtelo más tarde.");
            }
        } catch (ParadaInexistenteException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "No se pudo eliminar la parada. Inténtelo más tarde.");
        }

    }

    /**
     * Crea una nueva ruta entre el origen y destino seleccionados.
     */
    public void createRuta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Screens/CreateRutaDialog/CreateRutaDialog.fxml"));
            Parent root = loader.load();

            CreateRutaDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Crear Ruta");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            controller.setDialogStage(dialogStage);
            controller.setOrigen(origenSeleccionado);
            controller.setDestino(destinoSeleccionado);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (controller.isConfirmed()) {
                Ruta nuevaRuta = controller.getRuta();
                if (nuevaRuta != null) {
                    agregarRutaAlGrafo(nuevaRuta);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void agregarRutaAlGrafo(Ruta nuevaRuta) {
        Node origenNode = graph.getNode(nuevaRuta.getOrigenId());
        Node destinoNode = graph.getNode(nuevaRuta.getDestinoId());

        Edge edge = graph.addEdge(nuevaRuta.getId(), origenNode, destinoNode, true);
        edge.setAttribute("ui.interactive", true);
        edge.setAttribute("ruta", nuevaRuta);
        String labelText = String.format("Distancia: %d\nTiempo: %d\nCosto: %.2f\nTransbordos: %d",
                nuevaRuta.getDistancia(),
                nuevaRuta.getTiempo(),
                nuevaRuta.getCosto(),
                nuevaRuta.getTransbordos());
        edge.setAttribute("ui.label", labelText);
        edge.setAttribute("layout.weight", 5);
    }

    /**
     * Actualiza los datos de la ruta seleccionada.
     */
    public void updateRuta() {
        if (rutaSeleccionada == null) {
            System.err.println("La ruta seleccionada para modificar es NULL");
            return;
        }

        int nuevaDistancia = spinnerDistancia.getValue();
        int nuevosTransbordos = spinnerTranbordos.getValue();
        int nuevoTiempo = spinnerTiempo.getValue();
        double nuevoCosto = spinnerCosto.getValue();

        boolean hayCambios = rutaSeleccionada.getDistancia() != nuevaDistancia ||
                rutaSeleccionada.getTransbordos() != nuevosTransbordos ||
                rutaSeleccionada.getTiempo() != nuevoTiempo ||
                Double.compare(rutaSeleccionada.getCosto(), nuevoCosto) != 0;

        if (!hayCambios) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sin cambios", null, "No se han realizado cambios en la ruta.");
            return;
        }


        Ruta nuevosDatosRuta = new Ruta(rutaSeleccionada.getOrigenId(),
                rutaSeleccionada.getDestinoId(), nuevoTiempo, nuevaDistancia, nuevoCosto, nuevosTransbordos);

        try {
            boolean actualizado = backendGrafoController.actualizarRuta(rutaSeleccionada.getId(), nuevosDatosRuta);
            if (actualizado) {
                String labelText = String.format("Distancia: %d\nTiempo: %d\nCosto: %.2f\nTransbordos: %d",
                        nuevosDatosRuta.getDistancia(),
                        nuevosDatosRuta.getTiempo(),
                        nuevosDatosRuta.getCosto(),
                        nuevosDatosRuta.getTransbordos());

                rutaSeleccionada.setTransbordos(nuevosTransbordos);
                rutaSeleccionada.setCosto(nuevoCosto);
                rutaSeleccionada.setDistancia(nuevaDistancia);
                rutaSeleccionada.setTiempo(nuevoTiempo);

                Edge edge = graph.getEdge(rutaSeleccionada.getId());
                if (edge != null) {
                    edge.setAttribute("ui.label", labelText);
                    edge.setAttribute("ruta", rutaSeleccionada);
                }

                mostrarAlerta(Alert.AlertType.INFORMATION, "Ruta Actualizada", null, "La ruta se ha actualizado exitosamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "No se pudo actualizar la ruta.");
            }
        } catch (RutaInexistenteException | ParadaInexistenteException | RutaDuplicadaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "No se pudo actualizar la ruta.");
        }

    }

    /**
     * Elimina la ruta seleccionada.
     */
    public void removeRuta() {
        if (rutaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "No se pudo eliminar la ruta. Inténtelo más tarde.");
            return;
        }

        Edge edge = graph.getEdge(rutaSeleccionada.getId());
        if (edge == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro que desea eliminar la ruta seleccionada?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            boolean eliminada = backendGrafoController.eliminarRuta(rutaSeleccionada.getId());
            if (eliminada) {
                graph.removeEdge(edge);
                resetRuta();
                desactivarPanelRutaParaEliminar();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Ruta Eliminada", null, "La ruta ha sido eliminada.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "No se pudo eliminar la ruta. Inténtelo más tarde.");
                System.err.println("No se pudo eliminar la ruta.");
            }
        } catch (RutaInexistenteException | ParadaInexistenteException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "No se pudo eliminar la ruta. Inténtelo más tarde.");
        }

    }

    /**
     * Sale de la aplicación cerrando adecuadamente los hilos y recursos.
     */
    private void salir() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
            try {
                if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    System.err.println("El ExecutorService no se cerró correctamente.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (viewer != null) {
            viewer.close();
        }

        System.out.println("Salir de la aplicación");
        Platform.exit();
    }

    private void resetUI() {
        resultadoDelCaminoMasCorto = null;
        resetParadas();
        resetRuta();
        resetResultadoRuta();
    }

    private void resetParadas() {
        if (paradaSeleccionada != null) {
            Node nodo = graph.getNode(paradaSeleccionada.getId());
            if (nodo != null) {
                nodo.removeAttribute("ui.class");
            }
            paradaSeleccionada = null;
        }

        if (origenSeleccionado != null) {
            Node nodo = graph.getNode(origenSeleccionado.getId());
            if (nodo != null) {
                nodo.removeAttribute("ui.class");
            }
            origenSeleccionado = null;
        }

        if (destinoSeleccionado != null) {
            Node nodo = graph.getNode(destinoSeleccionado.getId());
            if (nodo != null) {
                nodo.removeAttribute("ui.class");
            }
            destinoSeleccionado = null;
        }

        textFieldNombreParada.setText(null);
        textFieldIdParada.setText(null);
        textFieldNombreParada.setDisable(true);
        eliminarParadaButton.setDisable(true);
        guardarParadaButton.setDisable(true);

        if (currentMode == Mode.ADD || currentMode == Mode.CLICKABLE) {
            hideElement(eliminarParadaButton);
            showElement(guardarParadaButton);
        } else {
            hideElement(guardarParadaButton);
            showElement(eliminarParadaButton);
        }
    }

    private void resetRuta() {
        if (rutaSeleccionada != null) {
            Edge edge = graph.getEdge(rutaSeleccionada.getId());
            if (edge != null) {
                edge.removeAttribute("ui.class");
            }
            rutaSeleccionada = null;
        }
        resetSpinners();
        textFieldIdRuta.setText(null);
        actualizarRutaButton.setDisable(true);
        eliminarRutaButton.setDisable(true);

        if (currentMode == Mode.ADD || currentMode == Mode.CLICKABLE) {
            desactivarPanelRutaParaActualizar();
        } else {
            desactivarPanelRutaParaEliminar();
        }

    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void initializeSpinners() {
        configureIntegerSpinner(spinnerDistancia);
        configureIntegerSpinner(spinnerTiempo);
        configureIntegerSpinner(spinnerTranbordos);
        configureDoubleSpinner(spinnerCosto);
    }

    private void configureIntegerSpinner(Spinner<Integer> spinner) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0);
        spinner.setValueFactory(valueFactory);
        TextField editor = spinner.getEditor();
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        editor.setTextFormatter(new TextFormatter<>(integerFilter));
        spinner.getValueFactory().setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                return value != null ? value.toString() : "";
            }

            @Override
            public Integer fromString(String text) {
                try {
                    return Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    return spinner.getValue();
                }
            }
        });
    }

    private void configureDoubleSpinner(Spinner<Double> spinner) {
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10000.0, 0.0, 0.1);
        spinner.setValueFactory(valueFactory);
        TextField editor = spinner.getEditor();
        UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9]*\\.?[0-9]*")) {
                return change;
            }
            return null;
        };
        editor.setTextFormatter(new TextFormatter<>(doubleFilter));
        spinner.getValueFactory().setConverter(new StringConverter<>() {
            @Override
            public String toString(Double value) {
                return value != null ? String.format("%.2f", value) : "";
            }

            @Override
            public Double fromString(String text) {
                try {
                    return Double.parseDouble(text);
                } catch (NumberFormatException e) {
                    return spinner.getValue();
                }
            }
        });
    }

    /**
     * Carga todas las paradas y rutas desde el backend y las agrega al grafo visual.
     */
    private void loadData() {
        List<Parada> todasLasParadas = backendGrafoController.getAllParadas();

        for (Parada parada : todasLasParadas) {
            String nodeId = parada.getId();
            if (graph.getNode(nodeId) == null) { // Evitar duplicados
                Node nodo = graph.addNode(nodeId);
                nodo.setAttribute("parada", parada);
                nodo.setAttribute("ui.label", parada.getNombre());
                nodo.setAttribute("layout.weight", 1);
            }
        }

        List<Ruta> todasLasRutas = backendGrafoController.getAllRutas();

        for (Ruta ruta : todasLasRutas) {
            String edgeId = ruta.getId();
            String origenId = ruta.getOrigenId();
            String destinoId = ruta.getDestinoId();

            Node origenNode = graph.getNode(origenId);
            Node destinoNode = graph.getNode(destinoId);

            if (origenNode == null || destinoNode == null) {
                System.err.println("Error: Las paradas de origen o destino de la ruta '" + edgeId + "' no existen en el grafo visual.");
                return;
            }

            if (graph.getEdge(edgeId) == null) {
                try {
                    Edge edge = graph.addEdge(edgeId, origenId, destinoId, true);
                    edge.setAttribute("ui.interactive", true);
                    edge.setAttribute("ruta", ruta);

                    String labelText = String.format("Distancia: %d\nTiempo: %d\nCosto: %.2f\nTransbordos: %d",
                            ruta.getDistancia(),
                            ruta.getTiempo(),
                            ruta.getCosto(),
                            ruta.getTransbordos());
                    edge.setAttribute("ui.label", labelText);
                    edge.setAttribute("layout.weight", 5);
                } catch (EdgeRejectedException e) {
                    System.err.println("Error al agregar la ruta al grafo visual: " + e.getMessage());
                }
            }
        }

        Platform.runLater(() -> {
            viewer.enableAutoLayout();
        });
    }

    private void updateSpinners(Ruta rutaSeleccionada) {
        spinnerDistancia.getValueFactory().setValue(rutaSeleccionada.getDistancia());
        spinnerTiempo.getValueFactory().setValue(rutaSeleccionada.getTiempo());
        spinnerTranbordos.getValueFactory().setValue(rutaSeleccionada.getTransbordos());
        spinnerCosto.getValueFactory().setValue(rutaSeleccionada.getCosto());
    }

    private void resetSpinners() {
        spinnerDistancia.getValueFactory().setValue(0);
        spinnerTiempo.getValueFactory().setValue(0);
        spinnerTranbordos.getValueFactory().setValue(0);
        spinnerCosto.getValueFactory().setValue(0.0);
    }

    private void configurarPanelRutaParaActualizar() {
        spinnerDistancia.setDisable(false);
        spinnerTiempo.setDisable(false);
        spinnerCosto.setDisable(false);
        spinnerTranbordos.setDisable(false);
        actualizarRutaButton.setDisable(false);
        showElement(actualizarRutaButton);
        hideElement(eliminarRutaButton);
        textFieldIdRuta.setText(rutaSeleccionada.getId());
        updateSpinners(rutaSeleccionada);
    }

    private void desactivarPanelRutaParaActualizar() {
        spinnerDistancia.setDisable(true);
        spinnerTiempo.setDisable(true);
        spinnerCosto.setDisable(true);
        spinnerTranbordos.setDisable(true);
        actualizarRutaButton.setDisable(true);
        showElement(actualizarRutaButton);
        hideElement(eliminarRutaButton);
        textFieldIdRuta.setText(null);
        resetSpinners();
    }

    private void configurarPanelRutaParaEliminar() {
        spinnerDistancia.setDisable(true);
        spinnerTiempo.setDisable(true);
        spinnerCosto.setDisable(true);
        spinnerTranbordos.setDisable(true);
        actualizarRutaButton.setDisable(true);
        hideElement(actualizarRutaButton);
        showElement(eliminarRutaButton);
        eliminarRutaButton.setDisable(false);
        textFieldIdRuta.setText(rutaSeleccionada.getId());
        updateSpinners(rutaSeleccionada);
    }

    private void desactivarPanelRutaParaEliminar() {
        spinnerDistancia.setDisable(true);
        spinnerTiempo.setDisable(true);
        spinnerCosto.setDisable(true);
        spinnerTranbordos.setDisable(true);
        eliminarRutaButton.setDisable(true);
        textFieldIdRuta.setText(null);
        hideElement(actualizarRutaButton);
        showElement(eliminarRutaButton);
        resetSpinners();
    }

    public void handleDjikstra() {
        try {
            resetEdgesFromBusqueda();
            Parada origen = origenSeleccionado;
            Parada destino = destinoSeleccionado;
            boolean isTiempoChecked = checkBoxTiempo.isSelected();
            boolean isDistanciaChecked = checkBoxDistania.isSelected();
            boolean isCostoChecked = checkBoxCosto.isSelected();
            boolean isTransbordosChecked = checkBoxTransbordos.isSelected();

            double pesoTiempo = isTiempoChecked ? 1 : 0;
            double pesoDistancia = isDistanciaChecked ? 1 : 0;
            double pesoCosto = isCostoChecked ? 1 : 0;
            double pesoTransbordos = isTransbordosChecked ? 1 : 0;

            if (!isCostoChecked && !isDistanciaChecked && !isTiempoChecked && !isTransbordosChecked) {
                pesoDistancia = 1;
            }

            ResultadoRuta resultado = backendGrafoController.obtenerRutaEntreParadasConDijkstra(origen, destino, pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);

            if (resultado.paradas().isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Camino mas corto", "Ruta no encontrada", "Lamentablemente no fue posible encontrar una ruta.");
                return;
            }

            resetEdgesFromBusqueda();
            resultadoDelCaminoMasCorto = resultado;
            mostrarResultadoRutaEnGrafo(resultado);
            verInformacionButton.setDisable(false);
        } catch (ParadaInexistenteException e) {
            throw new RuntimeException(e);
        }
    }

    public void mostrarResultadoRutaEnGrafo(ResultadoRuta resultado) {
        List<Ruta> rutas = resultado.rutas();

        for (Ruta ruta : rutas) {
            Edge edge = graph.getEdge(ruta.getId());

            if (edge != null) {
                edge.setAttribute("ui.class", "importante");
            }
        }
    }

    public void resetResultadoRuta() {
        textFieldNombreOrigen.setText(null);
        textFieldNombreDestino.setText(null);

        buscarButton.setDisable(true);
        verInformacionButton.setDisable(true);
        resetEdgesFromBusqueda();
        resultadoDelCaminoMasCorto = null;
    }

    public void resetEdgesFromBusqueda() {
        if (resultadoDelCaminoMasCorto == null) {
            return;
        }

        for (Ruta ruta : resultadoDelCaminoMasCorto.rutas()) {
            Edge edge = graph.getEdge(ruta.getId());

            if (edge != null) {
                edge.removeAttribute("ui.class");
            }
        }
    }

    public void mostrarInformacionDeBusqueda() {
        if (resultadoDelCaminoMasCorto == null) {
            return;
        }
        mostrarAlerta(Alert.AlertType.INFORMATION, "Resultado del camino mas corto", "Resultado", resultadoDelCaminoMasCorto.toString());
    }

    @Override
    public void buttonPushed(String id) {
        handleNodeClicked(id);
    }

    @Override
    public void viewClosed(String viewName) {
    }

    @Override
    public void buttonReleased(String id) {
    }

    @Override
    public void mouseOver(String id) {
    }

    @Override
    public void mouseLeft(String id) {
    }

    private class CustomMouseManager extends FxMouseManager {
        public CustomMouseManager() {
            super(EnumSet.of(InteractiveElement.NODE, InteractiveElement.EDGE));
        }

        @Override
        public void init(GraphicGraph graph, View view) {
            super.init(graph, view);
            view.addListener(MouseEvent.MOUSE_CLICKED, mouseClicked);
        }

        @Override
        public void release() {
            super.release();
            view.removeListener(MouseEvent.MOUSE_CLICKED, mouseClicked);
        }

        private final EventHandler<MouseEvent> mouseClicked = event -> {
            double x = event.getX();
            double y = event.getY();

            GraphicElement graphicElement = view.findGraphicElementAt(getManagedTypes(), x, y);

            Edge element = null;

            if (graphicElement == null && getManagedTypes().contains(InteractiveElement.EDGE)) {
                element = findEdgeAt(event.getX(), event.getY());
            } else {
                if (graphicElement != null) {
                    element = graph.getEdge(graphicElement.getId());
                }
            }

            if (element != null && element.getId() != null) {
                if (element instanceof GraphicEdge) {
                    handleEdgeClicked(element.getId());
                }
            } else {
                handleAddNodeOnMouseClicked();
            }
        };

        private Edge findEdgeAt(double x, double y) {
            Camera cam = view.getCamera();
            GraphMetrics metrics = cam.getMetrics();

            double xT = x + metrics.viewport[0];
            double yT = y + metrics.viewport[0];

            Edge edgeFound = null;

            if (getManagedTypes().contains(InteractiveElement.EDGE)) {
                Optional<Edge> edge = graph.edges().filter(e -> edgeContains((GraphicEdge) e, xT, yT)).findFirst();
                if (edge.isPresent()) {
                    if (cam.isVisible((GraphicElement) edge.get())) {
                        edgeFound = edge.get();
                    }
                }
            }

            return edgeFound;
        }

        private boolean edgeContains(GraphicEdge edge, double x, double y) {
            Camera cam = view.getCamera();
            GraphMetrics metrics = cam.getMetrics();

            Values size = edge.getStyle().getSize();
            double deviation = metrics.lengthToPx(size, 0);

            Point3 edgeNode0 = cam.transformGuToPx(edge.from.x, edge.from.y, 0);
            Point3 edgeNode1 = cam.transformGuToPx(edge.to.x, edge.to.y, 0);


            //check of point x,y is between nodes of the edge
            boolean edgeContains = false;
            //check x,y range
            if (x > Math.min(edgeNode0.x, edgeNode1.x) - deviation
                    && x < Math.max(edgeNode0.x, edgeNode1.x) + deviation
                    && y > Math.min(edgeNode0.y, edgeNode1.y) - deviation
                    && y < Math.max(edgeNode0.y, edgeNode1.y) + deviation) {

                //check deviation from edge

                Vector2 vectorNode0To1 = new Vector2(edgeNode0, edgeNode1);
                Point2 point = new Point2(x, y);
                Vector2 vectorNode0ToPoint = new Vector2(edgeNode0, point);
                //cross product of vectorNode0ToPoint and vectorNode0to1
                double crossProduct = vectorNode0ToPoint.x() * vectorNode0To1.y() - vectorNode0To1.x() * vectorNode0ToPoint.y();
                //distance of point to the line extending the edge
                double d = Math.abs(crossProduct) / vectorNode0To1.length();
                if (d <= deviation) {
                    edgeContains = true;
                }
            }

            return edgeContains;
        }

        private void handleAddNodeOnMouseClicked() {
            if (currentMode != Mode.ADD || !addChoiceBox.getValue().equals(CHOICE_CREAR_PARADA))
                return;

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Agregar Nueva Parada");
            dialog.setHeaderText("Ingrese el nombre de la nueva parada");
            dialog.setContentText("Nombre:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(nombre -> addNode(nombre.trim()));
        }
    }
}
