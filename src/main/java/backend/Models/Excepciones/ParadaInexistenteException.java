package backend.Models.Excepciones;

public class ParadaInexistenteException extends Exception {
    public ParadaInexistenteException(String mensaje) {
        super(mensaje);
    }
}
