Sistema de Gestión de Rutas de Transporte Público

El proyecto desarrollado tiene como objetivo gestionar un sistema de transporte utilizando grafos dirigidos.
Permite la creación, actualización y eliminación de paradas y rutas, así como la visualización de las conexiones mediante
una interfaz gráfica interactiva. Además, se implementaron algoritmos de búsqueda de rutas óptimas.

Estructuras de datos utilizadas:

-Parada: Representa una estación o punto de parada en el sistema de transporte. Estas son los nodos del grafo.

-Ruta: Representa una conexión dirigida entre dos paradas. Estas son las aristas entre los nodos.

-GrafoTransporte: Implementa la estructura del grafo dirigido que representa el sistema de transporte.

Excepciones Personalizadas:
Se implementaron excepciones específicas para manejar errores relacionados con las operaciones del grafo.

ParadaDuplicadaException: Lanzada cuando se intenta agregar una parada que ya existe.
ParadaInexistenteException: Lanzada cuando se hace referencia a una parada que no existe.
RutaDuplicadaException: Lanzada cuando se intenta agregar una ruta que ya existe.
RutaInexistenteException: Lanzada cuando se hace referencia a una ruta que no existe.

Algoritmos Implementados:

-Dijkstra: Permite encontrar la ruta más corta entre dos paradas en términos de un peso específico
(tiempo, distancia, costo, transbordos).

-Floyd-Warshall: Permite calcular las rutas más cortas entre todas las parejas de paradas,
considerando todos los posibles caminos intermedios.
(Este algoritmo no esta implementado en la interfaz gráfica. Sólo se utiliza Dijkstra)

Decisiones de Diseño:

-Patrón Singleton: Se utilizó el patrón Singleton para asegurar que solo exista una instancia
de los controladores principales (GrafoController, GrafoTransporte).

-Uso del Patron de diseño MVC

-Uso de controladores, servicios, modelos y repositorios.
