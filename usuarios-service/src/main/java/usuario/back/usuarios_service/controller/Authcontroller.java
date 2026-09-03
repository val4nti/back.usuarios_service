package usuario.back.usuarios_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usuario.back.usuarios_service.dto.AuthResponse;
import usuario.back.usuarios_service.dto.LoginRequest;
import usuario.back.usuarios_service.model.Usuario;
import usuario.back.usuarios_service.security.JwtUtil;
import usuario.back.usuarios_service.service.UsuarioService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        // Si el correo no existe o la contraseña no coincide, esto lanza
        // DatosInvalidosException, que el GlobalExceptionHandler convierte
        // en una respuesta 400 en JSON.
        Usuario usuario = usuarioService.validarCredenciales(request.getCorreo(), request.getPassword());

        String token = jwtUtil.generarToken(usuario.getId(), usuario.getCorreo(), usuario.getTipo().name());

        return new AuthResponse(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                usuario.getTipo()
        );
    }
}