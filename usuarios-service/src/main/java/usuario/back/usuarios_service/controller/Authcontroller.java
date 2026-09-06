package usuario.back.usuarios_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usuario.back.usuarios_service.model.Usuario;
import usuario.back.usuarios_service.security.JwtUtil;
import usuario.back.usuarios_service.service.UsuarioService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        String password = credenciales.get("password");

       
        Usuario usuario = usuarioService.validarCredenciales(correo, password);

        String token = jwtUtil.generarToken(usuario.getId(), usuario.getCorreo(), usuario.getTipo().name());

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("token", token);
        respuesta.put("id", usuario.getId());
        respuesta.put("nombre", usuario.getNombre());
        respuesta.put("apellidos", usuario.getApellidos());
        respuesta.put("correo", usuario.getCorreo());
        respuesta.put("tipo", usuario.getTipo());
        return respuesta;
    }
}