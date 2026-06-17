package ec.ucacue.xelqa.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.ucacue.xelqa.config.JwtUtil;
import ec.ucacue.xelqa.model.Usuario;
import ec.ucacue.xelqa.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Inyectamos las nuevas dependencias
    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo_institucional");
        String contrasena = credenciales.get("contrasena");

        if (correo == null || (!correo.endsWith("@ucacue.edu.ec") && !correo.endsWith("@est.ucacue.edu.ec"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Acceso denegado. Use su correo institucional."));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreoInstitucional(correo);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no encontrado."));
        }

        Usuario usuario = usuarioOpt.get();

        // NUEVO: Comparamos usando el PasswordEncoder de Spring Security
        if (!passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Contraseña incorrecta."));
        }

        if (!usuario.getActivo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Usuario inactivo."));
        }

        // NUEVO: Generamos el Token JWT
        String token = jwtUtil.generarToken(usuario.getCorreoInstitucional(), usuario.getRol().getNombre());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Login exitoso",
                "token", token,
                "usuarioId", usuario.getId(),
                "rol", usuario.getRol().getNombre()
        ));
    }
}
