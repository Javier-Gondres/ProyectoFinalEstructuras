package backend.Models.Excepciones;

public class ParadaDuplicadaException extends Exception {
    public ParadaDuplicadaException(String mensaje) {
        super(mensaje);
    }
}