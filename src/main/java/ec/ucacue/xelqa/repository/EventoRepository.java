package ec.ucacue.xelqa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.ucacue.xelqa.model.Evento;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    // Para mostrarle a los estudiantes solo los eventos que aún no han pasado
    List<Evento> findByActivoTrueOrderByFechaEventoAsc();
}