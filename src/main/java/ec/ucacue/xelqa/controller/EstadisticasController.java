package ec.ucacue.xelqa.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.ucacue.xelqa.repository.TransaccionRepository;
import ec.ucacue.xelqa.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/estadisticas")
@CrossOrigin(origins = "*")
public class EstadisticasController {

    private final TransaccionRepository transaccionRepository;
    private final UsuarioRepository usuarioRepository;

    public EstadisticasController(TransaccionRepository transaccionRepository, UsuarioRepository usuarioRepository) {
        this.transaccionRepository = transaccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // El endpoint de oro para Next.js
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> obtenerDatosDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        // 1. Obtenemos los totales reales desde PostgreSQL
        java.math.BigDecimal totalVentas = transaccionRepository.calcularTotalVentas();
        java.math.BigDecimal totalRecargas = transaccionRepository.calcularTotalRecargas();

        // 2. CÁLCULO DE GANANCIAS (MVP: Asumimos un 30% de rentabilidad neta sobre las ventas)
        java.math.BigDecimal margenGanancia = new java.math.BigDecimal("0.30");
        java.math.BigDecimal gananciasEstimadas = totalVentas.multiply(margenGanancia).setScale(2, java.math.RoundingMode.HALF_UP);

        // 3. Empaquetamos todo para el Frontend
        dashboard.put("totalVentas", totalVentas);
        dashboard.put("totalRecargas", totalRecargas);
        dashboard.put("gananciasEstimadas", gananciasEstimadas); // <-- ¡El dato para el CEO!
        dashboard.put("movimientosTotales", transaccionRepository.contarMovimientosTotales());
        dashboard.put("usuariosRegistrados", usuarioRepository.count());

        return ResponseEntity.ok(dashboard);
    }
}
