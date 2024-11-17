package backend.Models.Excepciones;

public class RutaInexistenteException extends Exception {
    public RutaInexistenteException(String mensaje) {
        super(mensaje);
    }
}