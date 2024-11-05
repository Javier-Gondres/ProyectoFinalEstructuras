package backend.Models;

public class Parada {
    private final String id;
    private String nombre;

    public Parada(String nombre) {
        this.id = java.util.UUID.randomUUID().toString();
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nuevoNombre) {
        this.nombre = nuevoNombre;
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
        return id.equals(parada.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
