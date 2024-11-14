package backend.Models;

import java.util.*;

public class GrafoTransporte {
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

        Ruta nuevaRuta = new Ruta(destino, tiempo, distancia, costo, transbordos);
        rutasDesdeOrigen.add(nuevaRuta);
    }

    /**
     * Busca una ruta específica entre dos paradas
     *
     * @return la ruta encontrada o null si no existe
     */
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

    /**
     * Modifica los atributos de una ruta existente
     */
    public void modificarRuta(Ruta ruta, int nuevoTiempo, int nuevaDistancia, double nuevoCosto, int nuevosTransbordos)
            throws RutaInexistenteException {

        if (nuevoTiempo < 0 || nuevaDistancia < 0 || nuevoCosto < 0 || nuevosTransbordos < 0) {
            throw new IllegalArgumentException("Los valores no pueden ser negativos");
        }

        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser nula");
        }

        ruta.setTiempo(nuevoTiempo);
        ruta.setDistancia(nuevaDistancia);
        ruta.setCosto(nuevoCosto);
        ruta.setTransbordos(nuevosTransbordos);
    }

    /**
     * Elimina una ruta existente entre dos paradas
     */
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

    public List<ParadaWrapper> dijkstra(Parada origen, Parada destino, double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) {
        Map<Parada, ParadaWrapper> paradaWrappers = new HashMap<>();
        PriorityQueue<ParadaWrapper> queue = new PriorityQueue<>();
        Set<Parada> shortestPathFound = new HashSet<>();

        ParadaWrapper sourceWrapper = new ParadaWrapper(origen, 0, null, null);
        paradaWrappers.put(origen, sourceWrapper);
        queue.add(sourceWrapper);

        while (!queue.isEmpty()) {
            ParadaWrapper paradaWrapper = queue.poll();
            Parada parada = paradaWrapper.getParadaNodo();
            if (shortestPathFound.contains(parada)) {
                continue;
            }
            shortestPathFound.add(parada);

            if (parada.equals(destino)) {
                return buildPath(paradaWrapper);
            }

            for (Ruta ruta : listaAdyacencia.get(parada)) {
                Parada vecino = ruta.getDestino();
                if (shortestPathFound.contains(vecino)) {
                    continue;
                }

                double distanciaActual = paradaWrapper.getDistanciaTotal();
                double pesoRuta = calcularPesoRuta(ruta, pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);
                double distanciaAcumulada = distanciaActual + pesoRuta;

                ParadaWrapper vecinoWrapper = paradaWrappers.get(vecino);
                if (vecinoWrapper == null || distanciaAcumulada < vecinoWrapper.getDistanciaTotal()) {
                    vecinoWrapper = new ParadaWrapper(vecino, distanciaAcumulada, paradaWrapper, ruta);
                    paradaWrappers.put(vecino, vecinoWrapper);
                    queue.add(vecinoWrapper);
                }
            }
        }

        return null;
    }

    public ResultadoRuta encontrarRutaMasCorta(Parada origen, Parada destino, double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) throws ParadaInexistenteException, IllegalArgumentException {
        if (!listaAdyacencia.containsKey(origen)) {
            throw new ParadaInexistenteException("Error: La parada de origen '" + origen.getNombre() + "' no existe.");
        }
        if (!listaAdyacencia.containsKey(destino)) {
            throw new ParadaInexistenteException("Error: La parada de destino '" + destino.getNombre() + "' no existe.");
        }
        if (pesoTiempo < 0 || pesoDistancia < 0 || pesoTransbordos < 0 || pesoCosto < 0) {
            throw new IllegalArgumentException("Los pesos deben ser valores positivos.");
        }

        double[] pesosNormalizados = normalizarPesos(pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);
        pesoTiempo = pesosNormalizados[0];
        pesoDistancia = pesosNormalizados[1];
        pesoTransbordos = pesosNormalizados[2];
        pesoCosto = pesosNormalizados[3];

        List<ParadaWrapper> rutaWrappers  = dijkstra(origen, destino, pesoTiempo, pesoDistancia, pesoTransbordos, pesoCosto);

        if (rutaWrappers == null) {
            return new ResultadoRuta(Collections.emptyList(), 0, 0.0, 0, 0);
        }

        List<Parada> paradas = new ArrayList<>();
        int distanciaTotal = 0;
        double costoTotal = 0;
        int transbordosTotal = 0;
        int tiempoTotal = 0;

        for (int i = 0; i < rutaWrappers.size(); i++) {
            ParadaWrapper wrapper = rutaWrappers.get(i);
            paradas.add(wrapper.getParadaNodo());

            if (i > 0) { // El primer nodo no tiene rutaUsada
                Ruta ruta = wrapper.getRutaUsada();
                if (ruta != null) {
                    distanciaTotal += ruta.getDistancia();
                    costoTotal += ruta.getCosto();
                    transbordosTotal += ruta.getTransbordos();
                    tiempoTotal += ruta.getTiempo();
                }
            }
        }

        return new ResultadoRuta(paradas, distanciaTotal, costoTotal, transbordosTotal, tiempoTotal);
    }

    private double[] normalizarPesos(double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) {
        double sumaPesos = pesoTiempo + pesoDistancia + pesoTransbordos + pesoCosto;
        if (sumaPesos == 0) {
            //Se priorizara por defecto la distancia si la suma da 0
            return new double[] { 0.0, 1.0, 0.0, 0.0 };
        }
        return new double[] {
                pesoTiempo / sumaPesos,
                pesoDistancia / sumaPesos,
                pesoTransbordos / sumaPesos,
                pesoCosto / sumaPesos
        };
    }

    private double calcularPesoRuta(Ruta ruta, double pesoTiempo, double pesoDistancia, double pesoTransbordos, double pesoCosto) {
        return (pesoDistancia * ruta.getDistancia()) +
                (pesoTransbordos * ruta.getTransbordos()) +
                (pesoTiempo * ruta.getTiempo()) +
                (pesoCosto * ruta.getCosto());
    }

    private static List<ParadaWrapper> buildPath(ParadaWrapper nodeWrapper) {
        List<ParadaWrapper> path = new ArrayList<>();
        while (nodeWrapper != null) {
            path.add(nodeWrapper);
            nodeWrapper = nodeWrapper.getPredecesor();
        }
        Collections.reverse(path);
        return path;
    }
}

class ParadaDuplicadaException extends Exception {
    public ParadaDuplicadaException(String mensaje) {
        super(mensaje);
    }
}

class RutaDuplicadaException extends Exception {
    public RutaDuplicadaException(String mensaje) {
        super(mensaje);
    }
}

class ParadaInexistenteException extends Exception {
    public ParadaInexistenteException(String mensaje) {
        super(mensaje);
    }
}

class RutaInexistenteException extends Exception {
    public RutaInexistenteException(String mensaje) {
        super(mensaje);
    }
}

class ParadaWrapper implements Comparable<ParadaWrapper> {
    private Parada paradaNodo;
    private double distanciaTotal;
    private ParadaWrapper predecesor;
    private Ruta rutaUsada;

    public ParadaWrapper(Parada paradaNodo, double distanciaTotal, ParadaWrapper predecesor, Ruta rutaUsada) {
        this.paradaNodo = paradaNodo;
        this.distanciaTotal = distanciaTotal;
        this.predecesor = predecesor;
        this.rutaUsada = rutaUsada;
    }

    public Parada getParadaNodo() {
        return paradaNodo;
    }

    public ParadaWrapper getPredecesor() {
        return predecesor;
    }

    public double getDistanciaTotal() {
        return distanciaTotal;
    }

    public Ruta getRutaUsada() {
        return rutaUsada;
    }

    public void setParadaNodo(Parada paradaNodo) {
        this.paradaNodo = paradaNodo;
    }

    public void setDistanciaTotal(double distancia) {
        this.distanciaTotal = distancia;
    }

    public void setPredecesor(ParadaWrapper predecesor) {
        this.predecesor = predecesor;
    }

    public void setRutaUsada(Ruta rutaUsada) {
        this.rutaUsada = rutaUsada;
    }

    @Override
    public int compareTo(ParadaWrapper other) {
        return Double.compare(this.distanciaTotal, other.distanciaTotal);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ParadaWrapper that = (ParadaWrapper) other;
        return paradaNodo.equals(that.paradaNodo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paradaNodo);
    }
}
