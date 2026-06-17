package ec.ucacue.xelqa.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // Activamos la configuración de CORS global
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Público para todos
                
                // --- REGLAS DE LA BILLETERA (VALLET) ---
                .requestMatchers("/api/wallet/recarga").hasAnyRole("ADMIN_GENERAL", "ADMIN_SERVICIO") // Solo admins recargan dinero
                .requestMatchers("/api/wallet/cobro").hasAnyRole("ADMIN_GENERAL", "ADMIN_SERVICIO")   // Solo el bar puede cobrar
                .requestMatchers("/api/wallet/saldo/**").hasAnyRole("USUARIO", "ADMIN_GENERAL")        // El estudiante ve su propio saldo
                
                // --- REGLAS DE LA CAFETERÍA (PRODUCTOS) ---
                .requestMatchers(HttpMethod.GET, "/api/productos/**").authenticated() // Cualquier usuario autenticado puede ver el menú
                .requestMatchers(HttpMethod.POST, "/api/productos/**").hasAnyRole("ADMIN_GENERAL", "ADMIN_SERVICIO") // Solo admins crean
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasAnyRole("ADMIN_GENERAL", "ADMIN_SERVICIO")  // Solo admins editan
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasAnyRole("ADMIN_GENERAL", "ADMIN_SERVICIO") // Solo admins ocultan

                // --- REGLAS DE LA CREDENCIAL ---
                .requestMatchers("/api/credencial/**").authenticated() // Cualquier usuario validado ve la credencial
                
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configuración de CORS profesional para evitar dolores de cabeza al conectar Frontend y Móvil
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Permite peticiones desde cualquier IP o dominio (Next.js, Android)
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}