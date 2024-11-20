package frontend.Controllers;

import backend.Models.Excepciones.ParadaDuplicadaException;
import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.Excepciones.RutaInexistenteException;
import backend.Models.GrafoTransporte;
import backend.Models.Interfaces.Grafo;
import backend.Models.Parada;
import backend.Models.Ruta;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.fx_viewer.util.FxMouseManager;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.util.GraphMetrics;
import org.graphstream.ui.view.util.InteractiveElement;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;


public class GrafoController implements ViewerListener {

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
    private Button crearGrafoButton;

    @FXML
    private Button cargarRutaButton;

    @FXML
    private Button salirButton;

    @FXML
    public Button toggleClickable;

    @FXML
    public Button toggleRemove;

    @FXML
    public Button toggleAdd;

    @FXML
    private ChoiceBox<String> addChoiceBox;

    @FXML
    public Pane panelActualizarParada;

    @FXML
    public Pane panelCrearRuta;

    @FXML
    public Pane panelActualizarRuta;

    @FXML
    public Button crearRutaButton;

    @FXML
    public TextField textFieldNombreParada;
    @FXML
    public TextField textFieldIdParada;
    @FXML
    public Button eliminarParadaButton;

    //TextFields del panel para actualizar ruta
    @FXML
    public TextField textFieldIdRuta;
    @FXML
    public Spinner<Integer> spinnerTiempo;
    @FXML
    public Spinner<Double> spinnerCosto;
    @FXML
    public Spinner<Integer> spinnerTranbordos;
    @FXML
    public Spinner<Integer> spinnerDistancia;
    @FXML
    public Button actualizarRutaButton;
    @FXML
    private Button eliminarRutaButton;

    private boolean isSliderBarVisible = false;

    private Graph graph;
    private FxViewer viewer;
    private FxViewPanel view;

    private final Grafo grafoTransporte = new GrafoTransporte();
    private ViewerPipe fromViewer;

    private ExecutorService executorService;

    private Parada origenSeleccionado = null;
    private Parada destinoSeleccionado = null;
    private Parada paradaSeleccionada = null;
    private Ruta rutaSeleccionada = null;

    private Mode currentMode = Mode.ADD;
    private final String CHOICE_CREAR_PARADA = "Crear Parada";
    private final String CHOICE_CREAR_RUTA = "Crear Ruta";


    /**
     * Método de inicialización que se ejecuta después de cargar el FXML.
     */
    @FXML
    public void initialize() {
        setupUIComponents();
        setupGraphStream();
        setupViewerPipe();
        setupEventHandlers();
    }

    /**
     * Configura los componentes de la interfaz de usuario.
     */
    private void setupUIComponents() {
        showSideBar();
        sliderBar.setTranslateX(-sliderBar.getPrefWidth());

        addChoiceBox.getItems().addAll(CHOICE_CREAR_PARADA, CHOICE_CREAR_RUTA);
        addChoiceBox.setValue(CHOICE_CREAR_PARADA);
        setActiveToggle(toggleAdd);

        //Descativar paneles
        handleShowPane(panelActualizarParada);

        initializeSpinners();
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

        showElement(pane);
    }

    /**
     * Configura GraphStream, incluyendo el grafo y la vista.
     */
    private void setupGraphStream() {
        graph = new SingleGraph("Grafo de Ejemplo");
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

    private void loadCSS() {
        String css = """
                /* Estilo base para todos los nodos */
                node {
                    size: 35px; /* Aumentado de 25px */
                    shape: circle; /* Opciones: circle, box, rounded-box, diamond */
                    stroke-mode: plain;
                    stroke-color: black;
                    stroke-width: 1px;
                    fill-color: #ddd622;
                    text-alignment: center;
                    text-size: 12px; /* Reducido de 14px */
                    text-color: #00171f;
                    text-style: bold;
                    text-padding: 2px;
                    text-offset: 0px, 30px;  /* Ajustado para mover la etiqueta hacia abajo */
                    shadow-mode: plain;
                    shadow-color: #999;
                    shadow-width: 0px;
                    shadow-offset: 3px, -3px;
                }
                                
                /* Estilos específicos por clase */
                node.importante {
                    fill-color: rgb(231, 76, 60);
                    size: 45px; /* Aumentado de 35px */
                    text-size: 14px; /* Reducido de 16px */
                    text-offset: 0px, 35px;  /* Ajustado para nodos más grandes */
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
                    size: 40px; /* Aumentado de 30px */
                    text-color: #2980b9;
                    text-offset: 0px, 35px;  /* Ajustado para nodos más grandes */
                    stroke-mode: dots;
                    stroke-width: 1px;
                }
                                
                node.origen {
                    fill-color: #00171f;
                    text-color: #00171f;
                    size: 40px;
                    text-offset: 0px, 35px;  /* Ajustado para nodos más grandes */
                }
                             
                node.destino {
                    fill-color: #adebff;
                    text-color: #adebff;
                    size: 40px;
                    text-offset: 0px, 35px;  /* Ajustado para nodos más grandes */
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
                    Thread.sleep(100); // Pausa para no sobrecargar el CPU
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
        toggleAdd.setOnAction(event -> {
            resetUI();
            setActiveToggle(toggleAdd);
            currentMode = Mode.ADD;
            addChoiceBox.setValue("Crear Parada");
        });

        toggleRemove.setOnAction(event -> {
            resetUI();
            setActiveToggle(toggleRemove);
            currentMode = Mode.REMOVE;
        });

        toggleClickable.setOnAction(event -> {
            resetUI();
            setActiveToggle(toggleClickable);
            currentMode = Mode.CLICKABLE;
        });

        toggleMenuButton.setOnAction(event -> toggleSliderBar());

        guardarParadaButton.setOnAction(event -> guardarParada());
        crearGrafoButton.setOnAction(event -> crearRuta());
        cargarRutaButton.setOnAction(event -> cargarRuta());
        salirButton.setOnAction(event -> salir());

        guardarParadaButton.setOnAction(event -> updateParada());
        eliminarParadaButton.setOnAction(event -> removeParada());

        crearRutaButton.setOnAction(event -> createRuta());

        actualizarRutaButton.setOnAction(e -> updateRuta());
        eliminarRutaButton.setOnAction(event -> removeRuta());

        addChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            resetUI();

            if (newValue.equals(CHOICE_CREAR_RUTA)) {
                handleShowPane(panelCrearRuta);
                crearRutaButton.setDisable(true);
            } else if (newValue.equals(CHOICE_CREAR_PARADA)) {
                handleShowPane(panelActualizarParada);
            }
        });
    }

    public void resetUI() {
        resetParadas();
        resetRuta();
    }

    public void resetParadas() {
        if (destinoSeleccionado != null) {
            Node nodo = graph.getNode(destinoSeleccionado.getId());
            if (nodo != null) {
                nodo.removeAttribute("ui.class");
            }
        }

        if (origenSeleccionado != null) {
            Node nodo = graph.getNode(origenSeleccionado.getId());
            if (nodo != null) {
                nodo.removeAttribute("ui.class");
            }
        }

        if (paradaSeleccionada != null) {
            Node nodo = graph.getNode(paradaSeleccionada.getId());
            if (nodo != null) {
                nodo.removeAttribute("ui.class");
            }
        }

        paradaSeleccionada = null;
        origenSeleccionado = null;
        destinoSeleccionado = null;
    }

    public void resetRuta() {
        if (rutaSeleccionada != null) {
            Edge edge = graph.getEdge(rutaSeleccionada.getId());
            if (edge != null) {
                edge.removeAttribute("ui.class");
            }
        }
        rutaSeleccionada = null;
    }

    public void handleAddNodeOnMouseClicked() {
        if (currentMode != Mode.ADD || !addChoiceBox.getValue().equals(CHOICE_CREAR_PARADA))
            return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar Nueva Parada");
        dialog.setHeaderText("Ingrese el nombre de la nueva parada");
        dialog.setContentText("Nombre:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nombre -> {
            if (!nombre.trim().isEmpty()) {
                addNode(nombre.trim());
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Entrada Inválida");
                alert.setHeaderText(null);
                alert.setContentText("El nombre de la parada no puede estar vacío.");
                alert.showAndWait();
            }
        });
    }

    /**
     * Alterna la visibilidad del slider bar con una animación de transición.
     */
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

    private void setActiveToggle(Button activeButton) {
        toggleAdd.getStyleClass().remove("active-button");
        toggleRemove.getStyleClass().remove("active-button-trash");
        toggleClickable.getStyleClass().remove("active-button");

        if (activeButton.equals(toggleRemove)) {
            activeButton.getStyleClass().add("active-button-trash");
        } else if (!activeButton.getStyleClass().contains("active-button")) {
            activeButton.getStyleClass().add("active-button");
        }
    }

    /**
     * Oculta el side bar desplazándolo fuera de la vista.
     */
    private void hideSideBar() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sideBar);
        transition.setToX(sideBar.getPrefWidth());
        transition.play();
    }

    /**
     * Muestra el side bar desplazándolo a su posición original.
     */
    private void showSideBar() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sideBar);
        transition.setToX(0);
        transition.play();
    }

    /**
     * Guarda la ruta actual del grafo.
     * Implementa la lógica según tus necesidades (por ejemplo, guardar en un archivo).
     */
    private void guardarParada() {
        // TODO: Implementar la lógica para guardar la ruta
        System.out.println("Guardar ruta");
    }

    /**
     * Crea una nueva ruta agregando nodos y aristas al grafo.
     */
    private void crearRuta() {
        Parada nuevaParada = new Parada("Nombre");

        try {
            grafoTransporte.agregarParada(nuevaParada);
        } catch (ParadaDuplicadaException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Parada Duplicada");
            alert.setContentText("La parada ya existe.");
            alert.showAndWait();
            return;
        }

        Node nodo = graph.addNode(nuevaParada.getId());
        nodo.setAttribute("parada", nuevaParada);
        nodo.setAttribute("ui.label", nuevaParada.getNombre());
        nodo.setAttribute("ui.class", "destacado");
    }

    /**
     * Carga una ruta existente en el grafo.
     * Implementa la lógica según tus necesidades (por ejemplo, cargar desde un archivo).
     */
    private void cargarRuta() {
        // TODO: Implementar la lógica para cargar una ruta
        System.out.println("Cargar ruta");
    }

    /**
     * Elimina la última ruta del grafo eliminando el último nodo y sus aristas conectadas.
     */
    private void eliminarRuta() {
        // Lógica para eliminar una ruta (eliminar el último nodo y sus aristas)
        if (graph.getNodeCount() > 0) {
            String lastNodeId = "N" + graph.getNodeCount();
            if (graph.getNode(lastNodeId) != null) {
                graph.removeNode(lastNodeId);
                System.out.println("Nodo " + lastNodeId + " eliminado");
            } else {
                System.out.println("Nodo " + lastNodeId + " no existe.");
            }
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
        System.exit(0);
    }

    private void addNode(String nombre) {
        Parada nuevaParada = new Parada(nombre);

        try {
            grafoTransporte.agregarParada(nuevaParada);
        } catch (ParadaDuplicadaException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Parada Duplicada");
            alert.setContentText("La parada ya existe.");
            alert.showAndWait();
            return;
        }

        String nodeId = nuevaParada.getId();
        if (graph.getNode(nodeId) != null) {
            nodeId = nodeId + "_" + (graph.getNodeCount() + 1);
        }

        Node nodo = graph.addNode(nodeId);
        nodo.setAttribute("parada", nuevaParada);
        nodo.setAttribute("ui.label", nuevaParada.getNombre());
        nodo.setAttribute("layout.weight", 1);
    }

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
        if (currentMode == Mode.ADD && addChoiceBox.getValue().equals(CHOICE_CREAR_RUTA)) {
            handleShowPane(panelCrearRuta);
            if (origenSeleccionado == null) {
                origenSeleccionado = parada;
                clickedNode.setAttribute("ui.class", "origen");
            } else if (origenSeleccionado.getId().equals(parada.getId())) {
                if (destinoSeleccionado == null) {
                    clickedNode.removeAttribute("ui.class");
                    origenSeleccionado = null;
                } else {
                    origenSeleccionado = destinoSeleccionado;
                    graph.getNode(origenSeleccionado.getId()).setAttribute("ui.class", "origen");

                    destinoSeleccionado = null;
                    clickedNode.removeAttribute("ui.class");
                }
            } else if (destinoSeleccionado == null) {
                destinoSeleccionado = parada;
                clickedNode.setAttribute("ui.class", "destino");
            } else {
                graph.getNode(destinoSeleccionado.getId()).removeAttribute("ui.class");
                destinoSeleccionado = parada;
                clickedNode.setAttribute("ui.class", "destino");
            }

            crearRutaButton.setDisable(origenSeleccionado == null || destinoSeleccionado == null);
        } else if (currentMode == Mode.REMOVE) {
            if (paradaSeleccionada == null) {
                paradaSeleccionada = parada;
                clickedNode.setAttribute("ui.class", "importante");
            } else if (paradaSeleccionada.getId().equals(nodoId)) {
                paradaSeleccionada = null;
                clickedNode.removeAttribute("ui.class");
            } else {
                graph.getNode(paradaSeleccionada.getId()).removeAttribute("ui.class");
                paradaSeleccionada = parada;
                clickedNode.setAttribute("ui.class", "importante");
            }

            if (paradaSeleccionada != null) {
                handleShowPane(panelActualizarParada);
                paradaSeleccionada = parada;
                textFieldIdParada.setText(parada.getId());
                textFieldNombreParada.setText(parada.getNombre());
                textFieldNombreParada.setDisable(true);

                showElement(eliminarParadaButton);
                hideElement(guardarParadaButton);

                eliminarParadaButton.setDisable(false);
            } else {
                eliminarParadaButton.setDisable(true);
            }

        } else {
            handleShowPane(panelActualizarParada);
            if (paradaSeleccionada != null) {
                graph.getNode(paradaSeleccionada.getId()).removeAttribute("ui.class");
            }

            clickedNode.setAttribute("ui.class", "destacado");
            paradaSeleccionada = parada;
            textFieldIdParada.setText(parada.getId());
            textFieldNombreParada.setText(parada.getNombre());

            textFieldNombreParada.setDisable(false);
        }
    }

    public void removeParada() {
        if (paradaSeleccionada == null) {
            System.err.println("No se pudo eliminar la parada porque es null");
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("No se pudo eliminar la parada. Inténtelo más tarde.");
            errorAlert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de eliminación");
        alert.setHeaderText("");
        alert.setContentText("¿Está seguro que desea eliminar la parada '" + paradaSeleccionada.getNombre() + "' y todas sus rutas asociadas?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                grafoTransporte.eliminarParada(paradaSeleccionada);

                graph.removeNode(paradaSeleccionada.getId());

                resetParadas();
                textFieldIdParada.setText(null);
                textFieldNombreParada.setText(null);
                eliminarParadaButton.setDisable(true);

                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setTitle("Parada eliminada");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("La parada y sus rutas asociadas han sido eliminadas.");
                infoAlert.showAndWait();
            } catch (ParadaInexistenteException e) {
                System.err.println("No se pudo eliminar la parada: " + e);
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("No se pudo eliminar la parada. Inténtelo más tarde.");
                errorAlert.showAndWait();
            }
        }
    }

    public void updateParada() {
        try {
            String nuevoNombre = textFieldNombreParada.getText();

            if (paradaSeleccionada == null) {
                System.err.println("La parada seleccionada para modificar es NULL");
                return;
            }
            if (paradaSeleccionada.getNombre().equals(nuevoNombre)) {
                System.err.println("No hay nada que actualizar en la parada");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sin cambios");
                alert.setContentText("No se han realizado cambios en la parada.");
                alert.showAndWait();
                return;
            }

            grafoTransporte.modificarParada(paradaSeleccionada, nuevoNombre);
            graph.getNode(paradaSeleccionada.getId()).setAttribute("ui.label", nuevoNombre);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Parada actualizada");
            alert.setContentText("La parada se ha actualizado exitosamente.");
            alert.showAndWait();
        } catch (ParadaInexistenteException | ParadaDuplicadaException e) {
            System.err.println("No se pudo actualizar la parada: " + e);
        }
    }

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
            controller.setGrafoTransporte(grafoTransporte);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (controller.isConfirmed()) {
                Ruta nuevaRuta = controller.getRuta();
                if (nuevaRuta != null) {
                    Node origenNode = graph.getNode(nuevaRuta.getOrigen().getId());
                    Node destinoNode = graph.getNode(nuevaRuta.getDestino().getId());

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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateRuta() {
        try {
            int nuevaDistancia = spinnerDistancia.getValue();
            int nuevosTransbordos = spinnerTranbordos.getValue();
            int nuevoTiempo = spinnerTiempo.getValue();
            double nuevoCosto = spinnerCosto.getValue();

            if (rutaSeleccionada == null) {
                System.err.println("La ruta seleccionada para modificar es NULL");
                return;
            }

            boolean areThereChanges = false;

            if (rutaSeleccionada.getDistancia() != nuevaDistancia) {
                areThereChanges = true;
            }
            if (rutaSeleccionada.getTransbordos() != nuevosTransbordos) {
                areThereChanges = true;
            }
            if (rutaSeleccionada.getTiempo() != nuevoTiempo) {
                areThereChanges = true;
            }
            if (Double.compare(rutaSeleccionada.getCosto(), nuevoCosto) != 0) {
                areThereChanges = true;
            }

            if (!areThereChanges) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sin cambios");
                alert.setContentText("No se han realizado cambios en la ruta.");
                alert.showAndWait();
                return;
            }

            grafoTransporte.modificarRuta(rutaSeleccionada, nuevoTiempo, nuevaDistancia, nuevoCosto, nuevosTransbordos);

            String labelText = String.format("Distancia: %d\nTiempo: %d\nCosto: %.2f\nTransbordos: %d",
                    rutaSeleccionada.getDistancia(),
                    rutaSeleccionada.getTiempo(),
                    rutaSeleccionada.getCosto(),
                    rutaSeleccionada.getTransbordos());

            graph.getEdge(rutaSeleccionada.getId()).setAttribute("ui.label", labelText);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ruta actualizada");
            alert.setContentText("La ruta se ha actualizado exitosamente.");
            alert.showAndWait();
        } catch (RutaInexistenteException | ParadaInexistenteException e) {
            System.err.println("No se pudo actualizar la ruta: " + e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Algo salió mal");
            alert.setContentText("No se pudo actualizar la ruta. Inténtelo mas tarde");
            alert.showAndWait();
        }
    }

    public void removeRuta() {
        if (rutaSeleccionada == null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("No se pudo eliminar la ruta. Inténtelo más tarde.");
            errorAlert.showAndWait();
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
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                grafoTransporte.eliminarRuta(rutaSeleccionada.getOrigen(), rutaSeleccionada.getDestino());
                graph.removeEdge(edge);
                resetRuta();
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setTitle("Ruta eliminada");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("La ruta ha sido eliminada.");
                infoAlert.showAndWait();
            } catch (RutaInexistenteException | ParadaInexistenteException e) {
                System.err.println("No se pudo eliminar la ruta: " + e);
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("No se pudo eliminar la ruta. Inténtelo más tarde.");
                errorAlert.showAndWait();
            }
        }
    }

    private void handleEdgeClicked(String edgeId) {
        handleShowPane(panelActualizarRuta);
        resetParadas();

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

        if (rutaSeleccionada == null) {
            rutaSeleccionada = ruta;
            edge.setAttribute("ui.class", "importante");
        } else if (rutaSeleccionada.getId().equals(edgeId)) {
            rutaSeleccionada = null;
            edge.removeAttribute("ui.class");
        } else if (rutaSeleccionada != null) {
            graph.getEdge(rutaSeleccionada.getId()).removeAttribute("ui.class");
            rutaSeleccionada = ruta;
            edge.setAttribute("ui.class", "importante");
        }

        if (rutaSeleccionada != null) {
            if (currentMode == Mode.REMOVE) {
                spinnerDistancia.setDisable(true);
                spinnerTiempo.setDisable(true);
                spinnerCosto.setDisable(true);
                spinnerTranbordos.setDisable(true);
                actualizarRutaButton.setDisable(true);
                textFieldIdRuta.setText(rutaSeleccionada.getId());
                updateSpinners(rutaSeleccionada);
                showElement(eliminarRutaButton);
                eliminarRutaButton.setDisable(false);
                hideElement(actualizarRutaButton);
                return;
            }
            spinnerDistancia.setDisable(false);
            spinnerTiempo.setDisable(false);
            spinnerCosto.setDisable(false);
            spinnerTranbordos.setDisable(false);
            actualizarRutaButton.setDisable(false);
            showElement(actualizarRutaButton);
            textFieldIdRuta.setText(rutaSeleccionada.getId());
            updateSpinners(rutaSeleccionada);
        } else {
            spinnerDistancia.setDisable(true);
            spinnerTiempo.setDisable(true);
            spinnerCosto.setDisable(true);
            spinnerTranbordos.setDisable(true);
            actualizarRutaButton.setDisable(true);
            textFieldIdRuta.setText(null);
            resetSpinners();
            if (currentMode == Mode.REMOVE) {
                eliminarRutaButton.setDisable(true);
            } else {
                hideElement(eliminarRutaButton);
            }
        }
    }

    //Funciones para configurar los spinners
    private void initializeSpinners() {
        SpinnerValueFactory<Integer> distanciaValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0);
        spinnerDistancia.setValueFactory(distanciaValueFactory);
        configureIntegerSpinner(spinnerDistancia);

        SpinnerValueFactory<Integer> tiempoValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0);
        spinnerTiempo.setValueFactory(tiempoValueFactory);
        configureIntegerSpinner(spinnerTiempo);

        SpinnerValueFactory<Integer> transbordosValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0);
        spinnerTranbordos.setValueFactory(transbordosValueFactory);
        configureIntegerSpinner(spinnerTranbordos);

        SpinnerValueFactory<Double> costoValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10000.0, 0.0, 0.1);
        spinnerCosto.setValueFactory(costoValueFactory);
        configureDoubleSpinner(spinnerCosto);
    }

    private void configureIntegerSpinner(Spinner<Integer> spinner) {
        TextField editor = spinner.getEditor();
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        editor.setTextFormatter(new TextFormatter<>(integerFilter));

        spinner.getValueFactory().setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer value) {
                return value != null ? value.toString() : "";
            }

            @Override
            public Integer fromString(String text) {
                if (text == null || text.isEmpty()) {
                    return spinner.getValueFactory().getValue();
                }
                try {
                    return Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    return spinner.getValueFactory().getValue();
                }
            }
        });
    }

    private void configureDoubleSpinner(Spinner<Double> spinner) {
        TextField editor = spinner.getEditor();
        UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9]*\\.?[0-9]*")) {
                return change;
            }
            return null;
        };
        editor.setTextFormatter(new TextFormatter<>(doubleFilter));

        spinner.getValueFactory().setConverter(new StringConverter<Double>() {
            @Override
            public String toString(Double value) {
                return value != null ? String.format("%.2f", value) : "";
            }

            @Override
            public Double fromString(String text) {
                if (text == null || text.isEmpty()) {
                    return spinner.getValueFactory().getValue();
                }
                try {
                    return Double.parseDouble(text);
                } catch (NumberFormatException e) {
                    return spinner.getValueFactory().getValue();
                }
            }
        });
    }

    private void updateSpinners(Ruta rutaSeleccionada) {
        spinnerDistancia.getValueFactory().setValue(rutaSeleccionada.getDistancia());
        spinnerTiempo.getValueFactory().setValue(rutaSeleccionada.getTiempo());
        spinnerTranbordos.getValueFactory().setValue(rutaSeleccionada.getTransbordos());
        spinnerCosto.getValueFactory().setValue(rutaSeleccionada.getCosto());
    }

    private void resetSpinners() {
        spinnerDistancia.getValueFactory().setValue(null);
        spinnerTiempo.getValueFactory().setValue(null);
        spinnerTranbordos.getValueFactory().setValue(null);
        spinnerCosto.getValueFactory().setValue(null);
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

        private final EventHandler<MouseEvent> mouseClicked = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();

                GraphicElement graphicElement = view.findGraphicElementAt(getManagedTypes(), x, y);

                if (graphicElement != null) {
                    System.out.println("ID: " + graphicElement.getId());
                }

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
    }
}

