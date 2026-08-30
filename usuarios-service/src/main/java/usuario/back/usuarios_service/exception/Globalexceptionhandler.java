package usuario.back.usuarios_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Igual que en catalogo-pedidos-service: convierte cualquier excepción en
 * una respuesta JSON con el mismo formato, nunca HTML.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> cuerpo(int status, String error, String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", error);
        body.put("mensaje", mensaje);
        return body;
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(cuerpo(404, "Recurso no encontrado", ex.getMessage()));
    }

    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<Map<String, Object>> manejarDatosInvalidos(DatosInvalidosException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(cuerpo(400, "Datos inválidos", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Datos inválidos.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(cuerpo(400, "Validación fallida", mensaje));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarGenerico(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(cuerpo(500, "Error interno", "Ocurrió un error inesperado en el servidor."));
    }
}