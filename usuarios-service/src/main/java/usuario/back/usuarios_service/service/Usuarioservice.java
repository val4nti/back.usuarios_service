package usuario.back.usuarios_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import usuario.back.usuarios_service.exception.DatosInvalidosException;
import usuario.back.usuarios_service.exception.RecursoNoEncontradoException;
import usuario.back.usuarios_service.model.Tipousuario;
import usuario.back.usuarios_service.model.Usuario;
import usuario.back.usuarios_service.repository.Usuariorepository;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class Usuarioservice {

    private final Usuariorepository usuarioRepository;

    // BCrypt cifra la contraseña; nunca se guarda en texto plano.
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Dominios de correo permitidos, según la especificación del caso.
    private static final Pattern CORREO_PERMITIDO = Pattern.compile(
            "^[^\\s@]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$", Pattern.CASE_INSENSITIVE);

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
    }

    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ese correo."));
    }

    /**
     * Registra un nuevo usuario (rol CLIENTE por defecto). Valida:
     * - dominio del correo permitido
     * - correo y RUN no repetidos
     * - RUN con dígito verificador válido
     * - cifra la contraseña antes de guardar
     */
    public Usuario registrar(Usuario usuario, String passwordPlano) {
        validarCorreoPermitido(usuario.getCorreo());
        validarRun(usuario.getRun());

        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new DatosInvalidosException("Ya existe una cuenta con ese correo.");
        }
        if (usuarioRepository.existsByRun(usuario.getRun())) {
            throw new DatosInvalidosException("Ya existe una cuenta con ese RUN.");
        }
        if (passwordPlano == null || passwordPlano.length() < 4 || passwordPlano.length() > 10) {
            throw new DatosInvalidosException("La contraseña debe tener entre 4 y 10 caracteres.");
        }

        usuario.setPasswordHash(passwordEncoder.encode(passwordPlano));
        usuario.setTipo(Tipousuario.CLIENTE); // el tipo solo se puede editar desde el admin, nunca al registrarse
        return usuarioRepository.save(usuario);
    }

    /** Usado por el admin para crear usuarios con cualquier rol (Vendedor/Administrador). */
    public Usuario crearComoAdmin(Usuario usuario, String passwordPlano) {
        Usuario creado = registrar(usuario, passwordPlano);
        // registrar() fuerza CLIENTE; si el admin pidió otro rol, se sobreescribe aquí.
        if (usuario.getTipo() != null) {
            creado.setTipo(usuario.getTipo());
            usuarioRepository.save(creado);
        }
        return creado;
    }

    public Usuario actualizar(Long id, Usuario datos) {
        Usuario existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setApellidos(datos.getApellidos());
        existente.setFechaNacimiento(datos.getFechaNacimiento());
        existente.setRegion(datos.getRegion());
        existente.setComuna(datos.getComuna());
        existente.setDireccion(datos.getDireccion());
        // El tipo (rol) solo se cambia explícitamente por el admin, en su propio endpoint.
        return usuarioRepository.save(existente);
    }

    /** Solo el admin puede llamar esto: cambia el rol de un usuario existente. */
    public Usuario cambiarTipo(Long id, Tipousuario nuevoTipo) {
        Usuario existente = buscarPorId(id);
        existente.setTipo(nuevoTipo);
        return usuarioRepository.save(existente);
    }

    public void eliminar(Long id) {
        Usuario existente = buscarPorId(id);
        usuarioRepository.delete(existente);
    }

    /** Verifica correo + contraseña para el login. Se usará desde el AuthController más adelante. */
    public Usuario validarCredenciales(String correo, String passwordPlano) {
        Usuario usuario = buscarPorCorreo(correo);
        if (!passwordEncoder.matches(passwordPlano, usuario.getPasswordHash())) {
            throw new DatosInvalidosException("Correo o contraseña incorrectos.");
        }
        return usuario;
    }

    private void validarCorreoPermitido(String correo) {
        if (correo == null || !CORREO_PERMITIDO.matcher(correo).matches()) {
            throw new DatosInvalidosException(
                    "El correo debe ser de dominio @duoc.cl, @profesor.duoc.cl o @gmail.com.");
        }
    }

    /**
     * Valida el dígito verificador del RUN chileno (algoritmo módulo 11).
     * Formato esperado: sin puntos ni guion, ej. "19011022K".
     */
    private void validarRun(String run) {
        if (run == null || !run.matches("\\d{6,8}[0-9kK]")) {
            throw new DatosInvalidosException("El RUN debe tener entre 7 y 9 caracteres, sin puntos ni guion.");
        }

        String cuerpo = run.substring(0, run.length() - 1);
        char dvIngresado = Character.toUpperCase(run.charAt(run.length() - 1));

        int suma = 0;
        int multiplicador = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplicador;
            multiplicador = (multiplicador == 7) ? 2 : multiplicador + 1;
        }

        int resto = 11 - (suma % 11);
        char dvCalculado;
        if (resto == 11) dvCalculado = '0';
        else if (resto == 10) dvCalculado = 'K';
        else dvCalculado = Character.forDigit(resto, 10);

        if (dvCalculado != dvIngresado) {
            throw new DatosInvalidosException("El RUN ingresado no es válido (dígito verificador incorrecto).");
        }
    }
}