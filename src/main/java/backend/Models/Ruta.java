package backend.Models;

import backend.Utils.IDGenerator;
import com.google.cloud.firestore.annotation.ServerTimestamp;

import java.util.Date;
import java.util.Objects;

public class Ruta {
    private final String id;
    private String origenId;
    private String destinoId;
    private int tiempo;
    private int distancia;
    private double costo;
    private int transbordos;
    @ServerTimestamp
    private Date timestamp;

    public Ruta() {
        this.id = IDGenerator.generateId(8);
    }

    public Ruta(String origenId, String destinoId, int tiempo, int distancia, double costo, int transbordos) {
        this.id = IDGenerator.generateId(8);
        this.origenId = origenId;
        this.destinoId = destinoId;
        this.tiempo = tiempo;
        this.distancia = distancia;
        this.costo = costo;
        this.transbordos = transbordos;
    }


    public String getOrigenId() {
        return origenId;
    }

    public void setOrigenId(String origenId) {
        this.origenId = origenId;
    }

    public String getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(String destinoId) {
        this.destinoId = destinoId;
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

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Ruta{" +
                "id='" + id + '\'' +
                ", origenId='" + origenId + '\'' +
                ", destinoId='" + destinoId + '\'' +
                ", tiempo=" + tiempo +
                ", distancia=" + distancia +
                ", costo=" + costo +
                ", transbordos=" + transbordos +
                '}';
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
