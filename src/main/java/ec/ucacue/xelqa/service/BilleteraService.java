package ec.ucacue.xelqa.service;

import ec.ucacue.xelqa.model.Billetera;
import ec.ucacue.xelqa.model.Transaccion;
import ec.ucacue.xelqa.repository.BilleteraRepository;
import ec.ucacue.xelqa.repository.TransaccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BilleteraService {

    private final BilleteraRepository billeteraRepository;
    private final TransaccionRepository transaccionRepository;

    public BilleteraService(BilleteraRepository billeteraRepository, TransaccionRepository transaccionRepository) {
        this.billeteraRepository = billeteraRepository;
        this.transaccionRepository = transaccionRepository;
    }

    public Billetera obtenerBilleteraPorUsuario(Long usuarioId) {
        return billeteraRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Billetera no encontrada para el usuario indicado."));
    }

    @Transactional
    public Billetera procesarRecarga(Long usuarioId, BigDecimal monto, String descripcion) {
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a recargar debe ser mayor a 0.");
        }

        // 1. Actualizar Saldo
        Billetera billetera = obtenerBilleteraPorUsuario(usuarioId);
        billetera.setSaldo(billetera.getSaldo().add(monto));
        billetera.setUltimaActualizacion(LocalDateTime.now());
        Billetera billeteraActualizada = billeteraRepository.save(billetera);

        // 2. Registrar en el historial contable
        registrarTransaccion(billeteraActualizada, "RECARGA", monto, descripcion);

        return billeteraActualizada;
    }

    @Transactional
    public Billetera procesarCobro(Long usuarioId, BigDecimal monto, String descripcion) {
        Billetera billetera = obtenerBilleteraPorUsuario(usuarioId);

        if (billetera.getSaldo().compareTo(monto) < 0) {
            throw new IllegalStateException("Saldo insuficiente en la Wallet.");
        }

        // 1. Descontar Saldo
        billetera.setSaldo(billetera.getSaldo().subtract(monto));
        billetera.setUltimaActualizacion(LocalDateTime.now());
        Billetera billeteraActualizada = billeteraRepository.save(billetera);

        // 2. Registrar en el historial contable
        registrarTransaccion(billeteraActualizada, "CONSUMO", monto, descripcion);

        return billeteraActualizada;
    }

    private void registrarTransaccion(Billetera billetera, String tipo, BigDecimal monto, String descripcion) {
        Transaccion transaccion = new Transaccion();
        transaccion.setBilletera(billetera);
        transaccion.setTipo(tipo);
        transaccion.setMonto(monto);
        transaccion.setFecha(LocalDateTime.now());
        transaccion.setDescripcion(descripcion);
        transaccionRepository.save(transaccion);
    }
}