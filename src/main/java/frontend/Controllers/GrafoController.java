package frontend.Controllers;

import backend.Models.Excepciones.ParadaDuplicadaException;
import backend.Models.GrafoTransporte;
import backend.Models.Interfaces.Grafo;
import backend.Models.Parada;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextInputDialog;
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
    private Button guardarRutaButton;

    @FXML
    private Button crearRutaButton;

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

    private boolean isSliderBarVisible = false;

    private Graph graph;
    private FxViewer viewer;
    private FxViewPanel view;

    private final Grafo grafoTransporte = new GrafoTransporte();
    private ViewerPipe fromViewer;

    private ExecutorService executorService;

    private Parada origenSeleccionado = null;
    private Parada destinoSeleccionado = null;

    private Mode currentMode = Mode.ADD;

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

        addChoiceBox.getItems().addAll("Crear Parada", "Crear Ruta");
        addChoiceBox.setValue("Crear Parada");
        setActiveToggle(toggleAdd);
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
                    fill-color: #3498db;
                    text-alignment: center;
                    text-size: 12px; /* Reducido de 14px */
                    text-color: white;
                    text-style: bold;
                    text-background-mode: rounded-box;
                    text-background-color: rgba(0, 0, 0, 0.5);
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
                    text-size: 12px; /* Asegurado que no sea demasiado grande */
                    text-offset: 0px, 35px; /* Ajustado para evitar cortes */
                }
                                
                node.destacado {
                    fill-color: rgb(46, 204, 113);
                    size: 40px; /* Aumentado de 30px */
                    text-offset: 0px, 35px;  /* Ajustado para nodos más grandes */
                    stroke-mode: dots;
                    stroke-width: 2px;
                }
                                
                node.inactivo {
                    fill-color: #95a5a6;
                    size: 25px; /* Aumentado de 20px */
                    text-color: #666;
                    text-style: italic;
                    text-offset: 0px, 25px;  /* Ajustado para nodos más pequeños */
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

        guardarRutaButton.setOnAction(event -> guardarRuta());
        crearRutaButton.setOnAction(event -> crearRuta());
        cargarRutaButton.setOnAction(event -> cargarRuta());
        eliminarRutaButton.setOnAction(event -> eliminarRuta());
        salirButton.setOnAction(event -> salir());

        view.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                double x = event.getX();
                double y = event.getY();

                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Agregar Nueva Parada");
                dialog.setHeaderText("Ingrese el nombre de la nueva parada");
                dialog.setContentText("Nombre:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(nombre -> {
                    if (!nombre.trim().isEmpty()) {
                        addNode(x, y, nombre.trim());
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Entrada Inválida");
                        alert.setHeaderText(null);
                        alert.setContentText("El nombre de la parada no puede estar vacío.");
                        alert.showAndWait();
                    }
                });
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
    private void guardarRuta() {
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

    /**
     * Agrega un nuevo nodo en la posición especificada con el nombre dado.
     *
     * @param x      La coordenada x del clic
     * @param y      La coordenada y del clic
     * @param nombre El nombre de la parada
     */
    private void addNode(double x, double y, String nombre) {
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

        // Asegurarse de que el ID del nodo sea único
        String nodeId = nuevaParada.getId();
        if (graph.getNode(nodeId) != null) {
            nodeId = nodeId + "_" + (graph.getNodeCount() + 1);
        }

        // Agregar el nodo al grafo de GraphStream
        Node nodo = graph.addNode(nodeId);
        nodo.setAttribute("parada", nuevaParada);
        nodo.setAttribute("ui.label", nuevaParada.getNombre());
        nodo.setAttribute("ui.class", "destacado"); // Cambia a la clase que desees
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
        javafx.application.Platform.runLater(() -> {
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Información de la Parada");
            infoAlert.setHeaderText(parada.getNombre());
            infoAlert.setContentText("ID: " + parada.getId());
            infoAlert.showAndWait();
        });
    }

    @Override
    public void viewClosed(String viewName) {}

    @Override
    public void buttonReleased(String id) {}

    @Override
    public void mouseOver(String id) {}

    @Override
    public void mouseLeft(String id) {}
}