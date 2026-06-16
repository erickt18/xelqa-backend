package ec.ucacue.xelqa.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.ucacue.xelqa.model.Usuario;
import ec.ucacue.xelqa.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        String contrasena = credenciales.get("contrasena");

        // 1. Validación de seguridad básica (Dominio institucional)
        if (correo == null || (!correo.endsWith("@ucacue.edu.ec") && !correo.endsWith("@est.ucacue.edu.ec"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Acceso denegado. Use su correo institucional."));
        }

        // 2. Buscar usuario en la base de datos
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreoInstitucional(correo);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no encontrado."));
        }

        Usuario usuario = usuarioOpt.get();

        // 3. Validar contraseña (NOTA: En producción esto debe estar encriptado con BCrypt)
        if (!usuario.getContrasena().equals(contrasena)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Contraseña incorrecta."));
        }

        if (!usuario.getActivo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Usuario inactivo. Contacte administración."));
        }

        // 4. Login exitoso: Devolvemos los datos clave para que el Frontend sepa quién entró
        return ResponseEntity.ok(Map.of(
                "mensaje", "Login exitoso",
                "usuarioId", usuario.getId(),
                "rol", usuario.getRol().getNombre()
        ));
    }
}
