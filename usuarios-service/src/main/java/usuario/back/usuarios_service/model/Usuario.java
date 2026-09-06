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

    // OJO: sin @NotBlank -- el cliente NUNCA envía este campo directamente
    // (lo calcula el backend a partir de "password"). 
    
    @Column(nullable = false)
    private String passwordHash;

    // Campo NO persistido (no crea columna en la tabla). 
    @Transient
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUsuario tipo;

    private LocalDate fechaNacimiento;
    private String region;
    private String comuna;

    @NotBlank
    @Size(max = 300)
    @Column(nullable = false, length = 300)
    private String direccion;
}