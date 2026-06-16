package ec.ucacue.xelqa.repository;

import ec.ucacue.xelqa.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    
    // Traduce a: SELECT * FROM transacciones WHERE billetera_id = ? ORDER BY fecha DESC
    List<Transaccion> findByBilleteraIdOrderByFechaDesc(Long billeteraId);
}