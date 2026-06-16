package ec.ucacue.xelqa.controller;

import ec.ucacue.xelqa.model.Billetera;
import ec.ucacue.xelqa.service.BilleteraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*") // Fundamental para que Next.js y Android puedan conectarse sin error de CORS
public class WalletController {

    private final BilleteraService billeteraService;

    public WalletController(BilleteraService billeteraService) {
        this.billeteraService = billeteraService;
    }

    @GetMapping("/saldo/{usuarioId}")
    public ResponseEntity<Billetera> consultarSaldo(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(billeteraService.obtenerBilleteraPorUsuario(usuarioId));
    }

    @PostMapping("/recarga")
    public ResponseEntity<Billetera> realizarRecarga(@RequestBody Map<String, Object> payload) {
        Long usuarioId = Long.valueOf(payload.get("usuarioId").toString());
        BigDecimal monto = new BigDecimal(payload.get("monto").toString());
        String descripcion = payload.getOrDefault("descripcion", "Recarga en caja").toString();
        
        return ResponseEntity.ok(billeteraService.procesarRecarga(usuarioId, monto, descripcion));
    }

    @PostMapping("/cobro")
    public ResponseEntity<Billetera> realizarCobro(@RequestBody Map<String, Object> payload) {
        Long usuarioId = Long.valueOf(payload.get("usuarioId").toString());
        BigDecimal monto = new BigDecimal(payload.get("monto").toString());
        String descripcion = payload.getOrDefault("descripcion", "Consumo en cafetería").toString();
        
        return ResponseEntity.ok(billeteraService.procesarCobro(usuarioId, monto, descripcion));
    }
}