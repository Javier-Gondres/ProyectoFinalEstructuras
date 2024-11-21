package backend.Models;

import java.util.List;

public class ResultadoRuta {
    private List<Parada> paradas;
    private List<Ruta> rutas;
    private int distanciaTotal;
    private double costoTotal;
    private int transbordosTotal;
    private int tiempoTotal;

    public ResultadoRuta(List<Parada> paradas, List<Ruta> rutas, int distanciaTotal, double costoTotal, int transbordosTotal, int tiempoTotal) {
        this.paradas = paradas;
        this.rutas = rutas;
        this.distanciaTotal = distanciaTotal;
        this.costoTotal = costoTotal;
        this.transbordosTotal = transbordosTotal;
        this.tiempoTotal = tiempoTotal;
    }

    public List<Parada> getParadas() {
        return paradas;
    }

    public List<Ruta> getRutas() {
        return rutas;
    }

    public int getDistanciaTotal() {
        return distanciaTotal;
    }

    public double getCostoTotal() {
        return costoTotal;
    }

    public int getTransbordosTotal() {
        return transbordosTotal;
    }

    public int getTiempoTotal() {
        return tiempoTotal;
    }

    @Override
    public String toString() {
        return "Ruta: " + paradas +
                "\nDistancia total: " + distanciaTotal +
                " m\nCosto total: $" + costoTotal +
                "\nTransbordos totales: " + transbordosTotal +
                "\nTiempo total: " + tiempoTotal + "min";
    }
}
