package backend.Models.Excepciones;

public class RutaDuplicadaException extends Exception {
    public RutaDuplicadaException(String mensaje) {
        super(mensaje);
    }
}
