package backend.Models;

public class Ruta {
    private final String id;
    private Parada destino;
    private int tiempo;
    private int distancia;
    private double costo;
    private int transbordos;

    public Ruta(Parada destino, int tiempo, int distancia, double costo, int transbordos) {
        this.id = java.util.UUID.randomUUID().toString();
        this.destino = destino;
        this.tiempo = tiempo;
        this.distancia = distancia;
        this.costo = costo;
        this.transbordos = transbordos;
    }

    public Parada getDestino() {
        return destino;
    }

    public void setDestino(Parada destino) {
        this.destino = destino;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public int getTransbordos() {
        return transbordos;
    }

    public void setTransbordos(int transbordos) {
        this.transbordos = transbordos;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Destino: " + destino.getNombre() +
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
        return id.equals(ruta.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}