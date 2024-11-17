package backend.Models;

import backend.Models.Excepciones.ParadaDuplicadaException;
import backend.Models.Excepciones.ParadaInexistenteException;
import backend.Models.Excepciones.RutaDuplicadaException;
import backend.Models.Excepciones.RutaInexistenteException;
import backend.Models.GraphAlgorithms.Dijkstra;
import backend.Models.GraphAlgorithms.FloydWarshall;
import backend.Models.Interfaces.Grafo;
import backend.Utils.GrafoUtils;

import java.util.*;

public class GrafoTransporte implements Grafo {
    private Map<Parada, List<Ruta>> listaAdyacencia;
    private Set<String> nombresExistentes;

    public GrafoTransporte() {
        listaAdyacencia = new HashMap<>();
        nombresExistentes = new HashSet<>();
    }

    public void agregarParada(Parada parada) throws ParadaDuplicadaException {
        if (parada == null) {
            throw new IllegalArgumentException("Error: La parada no puede ser nula.");
        }
        if (nombresExistentes.contains(parada.getNombre())) {
            throw new ParadaDuplicadaException("Error: La parada '" + parada.getNombre() + "' ya existe.");
        }

        nombresExistentes.add(parada.getNombre());
        listaAdyacencia.put(parada, new ArrayList<>());
    }

    public void modificarParada(Parada paradaExistente, String nuevoNombre)
            throws ParadaInexistenteException, ParadaDuplicadaException {

        if (!listaAdyacencia.containsKey(paradaExistente)) {
            throw new ParadaInexistenteException("Error: La parada '" + paradaExistente.getNombre() + "' no existe.");
        }

        if (nombresExistentes.contains(nuevoNombre) && !paradaExistente.getNombre().equals(nuevoNombre)) {
            throw new ParadaDuplicadaException("Error: La parada con el nombre '" + nuevoNombre + "' ya existe.");
        }

        nombresExistentes.remove(paradaExistente.getNombre());
        nombresExistentes.add(nuevoNombre);

        paradaExistente.setNombre(nuevoNombre);
    }

    public void eliminarParada(Parada parada) throws ParadaInexistenteException {
        if (!listaAdyacencia.containsKey(parada)) {
            throw new ParadaInexistenteException("Error: La parada '" + parada.getNombre() + "' no existe.");
        }

        listaAdyacencia.remove(parada);

        nombresExistentes.remove(parada.getNombre());

        for (List<Ruta> rutas : listaAdyacencia.values()) {
            rutas.removeIf(ruta -> ruta.getDestino().equals(parada));
        }
    }

    public void agregarRuta(Parada origen, Parada destino, int tiempo, int distancia, double costo, int transbordos)
            throws ParadaInexistenteException, RutaDuplicadaException {


        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Las paradas no pueden ser nulas");
        }
        if (tiempo < 0 || distancia < 0 || costo < 0 || transbordos < 0) {
            throw new IllegalArgumentException("Los valores no pueden ser negativos");
        }
        if (!listaAdyacencia.containsKey(origen)) {
            throw new ParadaInexistenteException("Error: La parada de origen '" + origen.getNombre() + "' no existe.");
        }
        if (!listaAdyacencia.containsKey(destino)) {
            throw new ParadaInexistenteException("Error: La parada de destino '" + destino.getNombre() + "' no existe.");
        }

        List<Ruta> rutasDesdeOrigen = listaAdyacencia.get(origen);

        for (Ruta ruta : rutasDesdeOrigen) {
            if (ruta.getDestino().equals(destino)) {
                throw new RutaDuplicadaException("Error: Ya existe una ruta entre " + origen.getNombre() + " y " + destino.getNombre());
            }
        }

        Ruta nuevaRuta = new Ruta(origen, destino, tiempo, distancia, costo, transbordos);
        rutasDesdeOrigen.add(nuevaRuta);
    }

    public void modificarRuta(Ruta ruta, int nuevoTiempo, int nuevaDistancia, double nuevoCosto, int nuevosTransbordos)
            throws RutaInexistenteException {

        if (nuevoTiempo < 0 || nuevaDistancia < 0 || nuevoCosto < 0 || nuevosTransbordos < 0) {
            throw new IllegalArgumentException("Los valores no pueden ser negativos");
        }

        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser nula");
        }

        Parada origen = ruta.getOrigen();
        if (!listaAdyacencia.containsKey(origen) || !listaAdyacencia.get(origen).contains(ruta)) {
            throw new RutaInexistenteException("Error: La ruta especificada no existe en el grafo.");
        }

        ruta.setTiempo(nuevoTiempo);
        ruta.setDistancia(nuevaDistancia);
        ruta.setCosto(nuevoCosto);
        ruta.setTransbordos(nuevosTransbordos);
    }

    public void eliminarRuta(Parada origen, Parada destino)
            throws ParadaInexistenteException, RutaInexistenteException {

        if (!listaAdyacencia.containsKey(origen)) {
            throw new ParadaInexistenteException("Error: La parada de origen '" + origen.getNombre() + "' no existe.");
        }
        if (!listaAdyacencia.containsKey(destino)) {
            throw new ParadaInexistenteException("Error: La parada de destino '" + destino.getNombre() + "' no existe.");
        }

        List<Ruta> rutasDesdeOrigen = listaAdyacencia.get(origen);

        boolean rutaEliminada = rutasDesdeOrigen.removeIf(ruta -> ruta.getDestino().equals(destino));

        if (!rutaEliminada) {
            throw new RutaInexistenteException("Error: No existe una ruta entre " +
                    origen.getNombre() + " y " + destino.getNombre());
        }
    }

    public Ruta buscarRuta(Parada origen, Parada destino) throws ParadaInexistenteException {
        if (!listaAdyacencia.containsKey(origen)) {
            throw new ParadaInexistenteException("Error: La parada de origen '" + origen.getNombre() + "' no existe.");
        }
        if (!listaAdyacencia.containsKey(destino)) {
            throw new ParadaInexistenteException("Error: La parada de destino '" + destino.getNombre() + "' no existe.");
        }

        List<Ruta> rutasDesdeOrigen = listaAdyacencia.get(origen);
        for (Ruta ruta : rutasDesdeOrigen) {
            if (ruta.getDestino().equals(destino)) {
                return ruta;
            }
        }
        return null;
    }

    public void imprimirGrafo() {
        for (Parada parada : listaAdyacencia.keySet()) {
            System.out.println("Parada " + parada.getNombre() + " tiene rutas hacia:");
            List<Ruta> rutas = listaAdyacencia.get(parada);

            for (Ruta ruta : rutas) {
                System.out.println("   " + ruta);
            }
        }
    }

    public List<Ruta> obtenerRutasDesde(Parada parada) {
        return listaAdyacencia.getOrDefault(parada, new ArrayList<>());
    }

    public List<Parada> obtenerParadas() {
        return new ArrayList<>(listaAdyacencia.keySet());
    }

    public ResultadoRuta obtenerRutaEntreParadasConDijkstra(Parada origen, Parada destino, double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) throws ParadaInexistenteException, IllegalArgumentException {
        GrafoUtils.verificarParadasExistentes(this, origen, destino);

        Dijkstra dijkstra = new Dijkstra(listaAdyacencia, pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);

        return dijkstra.obtenerRutaEntreParadas(origen, destino);
    }

    public ResultadoRuta obtenerRutaEntreParadasConFloyd(Parada origen, Parada destino, double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) throws ParadaInexistenteException, RutaInexistenteException {
        GrafoUtils.verificarParadasExistentes(this, origen, destino);

        FloydWarshall floydWarshall = new FloydWarshall(this, pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);

        return floydWarshall.obtenerRutaEntreParadas(origen, destino);
    }

    public boolean contieneParada(Parada parada) {
        return listaAdyacencia.containsKey(parada);
    }
}


