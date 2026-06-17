package ec.ucacue.xelqa.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extraemos el encabezado de autorización de la petición HTTP
        String authHeader = request.getHeader("Authorization");

        // 2. Verificamos que exista y empiece con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Cortamos la palabra "Bearer "

            // 3. Si el token es válido y no hay nadie autenticado aún en este hilo
            if (jwtUtil.validarToken(token) && SecurityContextHolder.getContext().getAuthentication() == null) {

                String correo = jwtUtil.extraerCorreo(token);
                String rol = jwtUtil.extraerRol(token); // NUEVO: Extraemos el rol del token

                // 4. Spring Security exige que los roles tengan el prefijo "ROLE_"
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rol);

                // 5. Guardamos el usuario con su respectivo rol para que el SecurityConfig lo valide
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        correo, null, List.of(authority)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 6. Continuar con el flujo normal de la petición
        filterChain.doFilter(request, response);
    }
}
