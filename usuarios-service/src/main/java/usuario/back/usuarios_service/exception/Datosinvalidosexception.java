package usuario.back.usuarios_service.exception;

public class DatosInvalidosException extends RuntimeException {
    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}