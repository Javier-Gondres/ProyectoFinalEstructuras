package backend.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrafoTransporte {
    private Map<Parada, List<Ruta>> listaAdyacencia;

    public GrafoTransporte() {
        listaAdyacencia = new HashMap<>();
    }

    public void agregarParada(Parada parada) throws ParadaDuplicadaException, IllegalArgumentException {
        if (parada == null) {
            throw new IllegalArgumentException("Error: La parada no puede ser nula.");
        }
        if (listaAdyacencia.containsKey(parada)) {
            throw new ParadaDuplicadaException("Error: La parada '" + parada.nombre + "' ya existe.");
        } else {
            listaAdyacencia.put(parada, new ArrayList<>());
        }
    }

    public void agregarRuta(Parada origen, Parada destino, int tiempo, int distancia, double costo, int transbordos)
            throws ParadaInexistenteException, RutaDuplicadaException {
        if (!listaAdyacencia.containsKey(origen)) {
            throw new ParadaInexistenteException("Error: La parada de origen '" + origen.nombre + "' no existe.");
        }
        if (!listaAdyacencia.containsKey(destino)) {
            throw new ParadaInexistenteException("Error: La parada de destino '" + destino.nombre + "' no existe.");
        }

        Ruta nuevaRuta = new Ruta(destino, tiempo, distancia, costo, transbordos);
        List<Ruta> rutasDesdeOrigen = listaAdyacencia.get(origen);

        if (rutasDesdeOrigen.contains(nuevaRuta)) {
            throw new RutaDuplicadaException("Error: La ruta ya existe entre " + origen.nombre + " y " + destino.nombre);
        } else {
            rutasDesdeOrigen.add(nuevaRuta);
        }
    }

    public void imprimirGrafo() {
        for (Parada parada : listaAdyacencia.keySet()) {
            System.out.println("Parada " + parada.nombre + " tiene rutas hacia:");
            for (Ruta ruta : listaAdyacencia.get(parada)) {
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

    public static class Parada {
        String nombre;

        public Parada(String nombre) {
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Parada parada = (Parada) obj;
            return nombre.equals(parada.nombre);
        }

        @Override
        public int hashCode() {
            return nombre.hashCode();
        }
    }

    public static class Ruta {
        Parada destino;
        int tiempo;
        int distancia;
        double costo;
        int transbordos;

        public Ruta(Parada destino, int tiempo, int distancia, double costo, int transbordos) {
            this.destino = destino;
            this.tiempo = tiempo;
            this.distancia = distancia;
            this.costo = costo;
            this.transbordos = transbordos;
        }

        @Override
        public String toString() {
            return "Destino: " + destino.nombre +
                    ", Tiempo: " + tiempo + "min" +
                    ", Distancia: " + distancia + "m" +
                    ", Costo: $" + costo +
                    ", Transbordos: " + transbordos;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Ruta ruta = (Ruta) obj;
            return destino.equals(ruta.destino) && tiempo == ruta.tiempo &&
                    distancia == ruta.distancia && costo == ruta.costo && transbordos == ruta.transbordos;
        }

        @Override
        public int hashCode() {
            return destino.hashCode() + tiempo + distancia + (int)costo + transbordos;
        }
    }

    public static class ParadaDuplicadaException extends Exception {
        public ParadaDuplicadaException(String mensaje) {
            super(mensaje);
        }
    }

    public static class RutaDuplicadaException extends Exception {
        public RutaDuplicadaException(String mensaje) {
            super(mensaje);
        }
    }

    public static class ParadaInexistenteException extends Exception {
        public ParadaInexistenteException(String mensaje) {
            super(mensaje);
        }
    }
}
