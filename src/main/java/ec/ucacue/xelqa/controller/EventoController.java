package ec.ucacue.xelqa.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.ucacue.xelqa.model.AsistenciaEvento;
import ec.ucacue.xelqa.model.Evento;
import ec.ucacue.xelqa.model.Usuario;
import ec.ucacue.xelqa.repository.AsistenciaEventoRepository;
import ec.ucacue.xelqa.repository.EventoRepository;
import ec.ucacue.xelqa.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "*")
public class EventoController {

    private final EventoRepository eventoRepository;
    private final AsistenciaEventoRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public EventoController(EventoRepository eventoRepository, AsistenciaEventoRepository asistenciaRepository, UsuarioRepository usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // 1. Ver todos los eventos activos (Para la cartelera de la App)
    @GetMapping("/activos")
    public List<Evento> listarEventosActivos() {
        return eventoRepository.findByActivoTrueOrderByFechaEventoAsc();
    }

    // 2. Crear un nuevo evento (Para el panel Web de Administración)
    @PostMapping
    public ResponseEntity<Evento> crearEvento(@RequestBody Evento evento) {
        return ResponseEntity.ok(eventoRepository.save(evento));
    }

    // 3. Registrar asistencia (Cuando se escanea el ID Digital)
    @PostMapping("/asistencia")
    public ResponseEntity<?> registrarAsistencia(@RequestBody Map<String, Long> payload) {
        Long eventoId = payload.get("eventoId");
        Long usuarioId = payload.get("usuarioId");

        // Validar si ya se registró antes
        if (asistenciaRepository.existsByEventoIdAndUsuarioId(eventoId, usuarioId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El estudiante ya tiene la asistencia registrada en este evento."));
        }

        Evento evento = eventoRepository.findById(eventoId).orElse(null);
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (evento == null || usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Evento o Usuario no encontrados."));
        }

        AsistenciaEvento asistencia = new AsistenciaEvento();
        asistencia.setEvento(evento);
        asistencia.setUsuario(usuario);
        
        asistenciaRepository.save(asistencia);

        return ResponseEntity.ok(Map.of("mensaje", "Asistencia registrada correctamente."));
    }
}