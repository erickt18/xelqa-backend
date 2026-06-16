package ec.ucacue.xelqa.repository;

import ec.ucacue.xelqa.model.Billetera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BilleteraRepository extends JpaRepository<Billetera, Long> {
    
    // Spring Boot traduce esto a: SELECT * FROM billeteras WHERE usuario_id = ?
    Optional<Billetera> findByUsuarioId(Long usuarioId);
}