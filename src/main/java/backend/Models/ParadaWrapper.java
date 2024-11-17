package backend.Models;

import java.util.Objects;

public class ParadaWrapper implements Comparable<ParadaWrapper> {
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
