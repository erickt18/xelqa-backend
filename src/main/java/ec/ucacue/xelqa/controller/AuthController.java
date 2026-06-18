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
import ec.ucacue.xelqa.model.Rol;
import ec.ucacue.xelqa.model.Usuario;
import ec.ucacue.xelqa.repository.RolRepository;
import ec.ucacue.xelqa.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Actualizamos el constructor para incluir RolRepository
    public AuthController(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // --- 1. ENDPOINT DE REGISTRO ---
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario nuevoUsuario) {

        // 1. Validar que use el correo de la universidad
        String correo = nuevoUsuario.getCorreoInstitucional();
        if (correo == null || (!correo.endsWith("@ucacue.edu.ec") && !correo.endsWith("@est.ucacue.edu.ec"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Debe usar un correo institucional válido."));
        }

        // 2. Verificar que el correo no exista ya en la base de datos
        if (usuarioRepository.findByCorreoInstitucional(correo).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El correo ya está registrado en el sistema."));
        }

        // 3. LA MAGIA: Encriptar la contraseña plana que llega desde la app móvil
        String contrasenaPlana = nuevoUsuario.getContrasena();
        nuevoUsuario.setContrasena(passwordEncoder.encode(contrasenaPlana)); // Esto la convierte en el hash $2a$12$...

        // 4. Asignar rol por defecto (USUARIO) y activarlo
        Rol rolUsuario = rolRepository.findByNombre("USUARIO")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'USUARIO' no encontrado en la base de datos."));

        nuevoUsuario.setRol(rolUsuario);
        nuevoUsuario.setActivo(true);

        // 5. Guardar en PostgreSQL
        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensaje", "Usuario registrado exitosamente. Ya puede iniciar sesión."
        ));
    }

    // --- 2. ENDPOINT DE LOGIN (Se mantiene exactamente igual) ---
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

        if (!passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Contraseña incorrecta."));
        }

        if (!usuario.getActivo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Usuario inactivo."));
        }

        String token = jwtUtil.generarToken(usuario.getCorreoInstitucional(), usuario.getRol().getNombre());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Login exitoso",
                "token", token,
                "usuarioId", usuario.getId(),
                "rol", usuario.getRol().getNombre()
        ));
    }
}
