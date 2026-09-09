package usuario.back.usuarios_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Sin esta clase, Spring Security bloquea TODAS las rutas por defecto y
 * redirige a una pantalla de login propia (/login) -- por eso el registro
 * y el login fallaban con un error de CORS que en realidad era un
 * redirect no autorizado.
 *
 * Esta configuración:
 * 1. Desactiva el formulario de login por defecto de Spring (no lo usamos, usamos JWT).
 * 2. Deja login y registro abiertos a cualquiera (públicos).
 * 3. Deja el resto de rutas abiertas por ahora también (sin filtro JWT
 *    todavía) -- el siguiente paso pendiente es agregar el filtro que
 *    valide el token y restrinja por rol.
 * 4. Configura CORS para que el frontend (localhost:5173) pueda conectarse.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // TODO: restringir por rol cuando se agregue el filtro JWT
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}