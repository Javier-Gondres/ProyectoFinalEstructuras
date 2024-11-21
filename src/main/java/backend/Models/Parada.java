package backend.Models;

import backend.Utils.IDGenerator;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import java.util.Date;
import java.util.Objects;

public class Parada {
    private String id;
    private String nombre;
    @ServerTimestamp
    private Date timestamp;

    public Parada() {
        this.id = IDGenerator.generateId(8);
    }

    /**
     * Constructor con nombre.
     *
     * @param nombre El nombre de la parada.
     */
    public Parada(String nombre) {
        this.id = IDGenerator.generateId(8);
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nuevoNombre) {
        this.nombre = nuevoNombre;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Parada{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Parada parada = (Parada) obj;
        return Objects.equals(id, parada.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
