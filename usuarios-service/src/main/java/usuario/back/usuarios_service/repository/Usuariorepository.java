package usuario.back.usuarios_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usuario.back.usuarios_service.model.Usuario;

import java.util.Optional;

public interface Usuariorepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByRun(String run);
}