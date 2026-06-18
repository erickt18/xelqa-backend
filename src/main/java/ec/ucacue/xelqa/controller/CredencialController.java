package ec.ucacue.xelqa.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.ucacue.xelqa.model.Credencial;
import ec.ucacue.xelqa.model.Usuario;
import ec.ucacue.xelqa.repository.CredencialRepository;
import ec.ucacue.xelqa.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/credencial")
@CrossOrigin(origins = "*")
public class CredencialController {

    private final CredencialRepository credencialRepository;
    private final UsuarioRepository usuarioRepository; // NUEVO: Traemos el repositorio de usuarios

    public CredencialController(CredencialRepository credencialRepository, UsuarioRepository usuarioRepository) {
        this.credencialRepository = credencialRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> verMiCredencial(@PathVariable Long usuarioId) {

        // 1. Buscamos la credencial
        Optional<Credencial> credencialOpt = credencialRepository.findByUsuarioId(usuarioId);

        if (credencialOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Credencial no encontrada para este estudiante."));
        }

        Credencial credencial = credencialOpt.get();

        // 2. Buscamos al estudiante dueño de esa credencial
        Usuario estudiante = usuarioRepository.findById(usuarioId).orElse(null);

        // 3. Armamos el JSON perfecto
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", credencial.getId());
        respuesta.put("cedula", credencial.getCedula());
        respuesta.put("institucion", "Universidad Católica de Cuenca");
        respuesta.put("campus", credencial.getCampus());
        respuesta.put("facultad", credencial.getFacultad());
        respuesta.put("carrera", credencial.getCarrera());

        // LA MAGIA REAL: Ahora sí le sacamos los datos al objeto 'estudiante' que acabamos de buscar
        if (estudiante != null) {
            respuesta.put("fotoUrl", estudiante.getFotoUrl());
            respuesta.put("correo", estudiante.getCorreoInstitucional());
        } else {
            respuesta.put("fotoUrl", null);
            respuesta.put("correo", null);
        }

        return ResponseEntity.ok(respuesta);
    }
}