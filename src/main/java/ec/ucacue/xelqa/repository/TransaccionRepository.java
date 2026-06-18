package ec.ucacue.xelqa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ec.ucacue.xelqa.model.Transaccion;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    
    // LA SOLUCIÓN: Ruta directa hacia tu variable usuarioId en la Billetera
    @Query("SELECT t FROM Transaccion t WHERE t.billetera.usuarioId = :usuarioId ORDER BY t.fecha DESC")
    List<Transaccion> obtenerHistorialEstudiante(@Param("usuarioId") Long usuarioId);

    // Para el administrador (Se mantiene igual)
    List<Transaccion> findAllByOrderByFechaDesc();

    // --- ESTADÍSTICAS PARA EL DASHBOARD ---
    
    // 1. Suma todo el dinero que ha entrado por ventas de cafetería
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t WHERE t.tipo = 'CONSUMO'")
    java.math.BigDecimal calcularTotalVentas();

    // 2. Suma todo el dinero que los estudiantes han recargado en sus billeteras
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t WHERE t.tipo = 'RECARGA'")
    java.math.BigDecimal calcularTotalRecargas();

    // 3. Cuenta cuántos movimientos se han hecho en la historia del sistema
    @Query("SELECT COUNT(t) FROM Transaccion t")
    long contarMovimientosTotales();
}