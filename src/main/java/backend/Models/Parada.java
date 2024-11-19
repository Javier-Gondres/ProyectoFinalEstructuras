package backend.Models;

import backend.Utils.IDGenerator;

public class Parada implements Comparable<Parada>{
    private final String id;
    private String nombre;

    public Parada(String nombre) {
        this.id = IDGenerator.generateId(8);
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

    @Override
    public int compareTo(Parada otraParada) {
        return this.nombre.compareTo(otraParada.getNombre());
    }

    public String getId() {
        return id;
    }
}
