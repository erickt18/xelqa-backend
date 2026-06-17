package ec.ucacue.xelqa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.ucacue.xelqa.model.AsistenciaEvento;

@Repository
public interface AsistenciaEventoRepository extends JpaRepository<AsistenciaEvento, Long> {
    // Fundamental para que un estudiante no se registre dos veces al mismo evento
    boolean existsByEventoIdAndUsuarioId(Long eventoId, Long usuarioId);
}