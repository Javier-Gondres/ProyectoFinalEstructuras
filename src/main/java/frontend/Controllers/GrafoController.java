package frontend.Controllers;

import backend.Models.Excepciones.ParadaDuplicadaException;
import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.GrafoTransporte;
import backend.Models.Interfaces.Grafo;
import backend.Models.Parada;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private Button eliminarRutaButton;

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
    }

    private void disablePane(Pane pane) {
        pane.setVisible(false);
        pane.setManaged(false);
    }

    private void enablePane(Pane pane) {
        pane.setVisible(true);
        pane.setManaged(true);
    }

    private void handleShowPane(Pane pane) {
        disablePane(panelCrearRuta);
        disablePane(panelActualizarRuta);
        disablePane(panelActualizarParada);

        enablePane(pane);
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
            setActiveToggle(toggleAdd);
            currentMode = Mode.ADD;
            addChoiceBox.setValue("Crear Parada");
        });

        toggleRemove.setOnAction(event -> {
            setActiveToggle(toggleRemove);
            currentMode = Mode.REMOVE;
        });

        toggleClickable.setOnAction(event -> {
            setActiveToggle(toggleClickable);
            currentMode = Mode.CLICKABLE;
        });

        toggleMenuButton.setOnAction(event -> toggleSliderBar());

        guardarParadaButton.setOnAction(event -> guardarParada());
        crearGrafoButton.setOnAction(event -> crearRuta());
        cargarRutaButton.setOnAction(event -> cargarRuta());
        eliminarRutaButton.setOnAction(event -> eliminarRuta());
        salirButton.setOnAction(event -> salir());

        guardarParadaButton.setOnAction(event -> updateParada());

        view.setOnMouseClicked(event -> {
            if (event.getClickCount() != 1) {
                return;
            }
            handleAddNodeOnMouseClicked();
        });

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

    public void resetUI(){
        if(destinoSeleccionado != null){
            graph.getNode(destinoSeleccionado.getId()).removeAttribute("ui.class");
        }

        if(origenSeleccionado != null){
            graph.getNode(origenSeleccionado.getId()).removeAttribute("ui.class");
        }

        if(paradaSeleccionada != null){
            graph.getNode(paradaSeleccionada.getId()).removeAttribute("ui.class");
        }

        paradaSeleccionada = null;
        origenSeleccionado = null;
        destinoSeleccionado = null;
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
        toggleRemove.getStyleClass().remove("active-button");
        toggleClickable.getStyleClass().remove("active-button");

        if (!activeButton.getStyleClass().contains("active-button")) {
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
    }

    @Override
    public void buttonPushed(String id) {
        System.out.println(id);
        Node clickedNode = graph.getNode(id);
        if (clickedNode == null) {
            return;
        }
        Parada parada = (Parada) clickedNode.getAttribute("parada");
        if (parada == null) {
            return;
        }

        if (currentMode == Mode.ADD && addChoiceBox.getValue().equals(CHOICE_CREAR_RUTA)) {
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
        } else {
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

    public void updateParada() {
        try {
            String nuevoNombre = textFieldNombreParada.getText();

            if (paradaSeleccionada == null) {
                System.err.println("La parada seleccionada para modificar es NULL");
                return;
            }
            if (paradaSeleccionada.getNombre().equals(nuevoNombre)) {
                System.err.println("No hay nada que actualizar en la parada");
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
}