package ec.ucacue.xelqa.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
            String token = authHeader.substring(7); // Cortamos la palabra "Bearer " para quedarnos solo con el token

            // 3. Si el token es válido y no hay nadie autenticado aún en este hilo
            if (jwtUtil.validarToken(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                String correo = jwtUtil.extraerCorreo(token);

                // 4. Le decimos a Spring Security: "Este usuario es legítimo, déjalo pasar"
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        correo, null, Collections.emptyList()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. Continuar con el flujo normal de la petición
        filterChain.doFilter(request, response);
    }
}
