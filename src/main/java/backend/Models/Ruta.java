package backend.Models;

import backend.Utils.IDGenerator;
import com.google.cloud.firestore.annotation.ServerTimestamp;

import java.util.Date;
import java.util.Objects;

public class Ruta {
    private final String id;
    private Parada origen;
    private Parada destino;
    private int tiempo;
    private int distancia;
    private double costo;
    private int transbordos;
    @ServerTimestamp
    private Date timestamp;

    public Ruta() {
        this.id = IDGenerator.generateId(8);
    }

    public Ruta(Parada origen, Parada destino, int tiempo, int distancia, double costo, int transbordos) {
        this.id = IDGenerator.generateId(8);
        this.origen = origen;
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

    public Parada getOrigen() {
        return origen;
    }

    public void setOrigen(Parada origen) {
        this.origen = origen;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ID: " + id +
                ", Origen: " + origen.getNombre() +
                ", Destino: " + destino.getNombre() +
                ", Tiempo: " + tiempo + " min" +
                ", Distancia: " + distancia + " m" +
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
        return Objects.hash(this.id);
    }
}
