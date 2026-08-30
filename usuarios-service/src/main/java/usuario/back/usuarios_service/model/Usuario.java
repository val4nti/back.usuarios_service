package usuario.back.usuarios_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Representa a un usuario del sistema (Administrador, Vendedor o Cliente).
 * Vive en la base de datos propia de usuarios-service (usuarios_db),
 * separada de la base de catalogo-pedidos-service.
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 7, max = 9)
    @Column(nullable = false, unique = true, length = 9)
    private String run;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String apellidos;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    // Nunca se guarda la contraseña en texto plano: aquí va el hash (BCrypt).
    @NotBlank
    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUsuario tipo;

    // Opcional según la especificación.
    private LocalDate fechaNacimiento;

    private String region;
    private String comuna;

    @NotBlank
    @Size(max = 300)
    @Column(nullable = false, length = 300)
    private String direccion;
}