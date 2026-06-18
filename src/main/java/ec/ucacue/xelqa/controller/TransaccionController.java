package ec.ucacue.xelqa.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.ucacue.xelqa.model.Transaccion;
import ec.ucacue.xelqa.repository.TransaccionRepository;

@RestController
@RequestMapping("/api/transacciones")
@CrossOrigin(origins = "*")
public class TransaccionController {

    private final TransaccionRepository transaccionRepository;

    public TransaccionController(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    // 1. ENDPOINT PARA EL ESTUDIANTE (App Móvil)
    @GetMapping("/mis-transacciones/{usuarioId}")
    public ResponseEntity<List<Transaccion>> verMiHistorial(@PathVariable Long usuarioId) {

        // Llamamos a la consulta personalizada que creamos
        List<Transaccion> historial = transaccionRepository.obtenerHistorialEstudiante(usuarioId);

        return ResponseEntity.ok(historial);
    }

    // 2. ENDPOINT PARA EL ADMINISTRADOR (Panel Web Next.js)
    @GetMapping
    public ResponseEntity<List<Transaccion>> verTodasLasTransacciones() {
        return ResponseEntity.ok(transaccionRepository.findAllByOrderByFechaDesc());
    }
}
