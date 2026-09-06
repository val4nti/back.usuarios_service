package usuario.back.usuarios_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usuario.back.usuarios_service.model.TipoUsuario;
import usuario.back.usuarios_service.model.Usuario;
import usuario.back.usuarios_service.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario registrar(@RequestBody Usuario usuario) {
        return usuarioService.registrar(usuario, usuario.getPassword());
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable Long id, @RequestBody Usuario datos) {
        return usuarioService.actualizar(id, datos);
    }

    @PatchMapping("/{id}/tipo")
    public Usuario cambiarTipo(@PathVariable Long id, @RequestParam TipoUsuario tipo) {
        return usuarioService.cambiarTipo(id, tipo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}