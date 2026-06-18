package ec.ucacue.xelqa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.ucacue.xelqa.model.Noticia;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Long> {
    // Trae las noticias activas ordenadas por fecha (las más nuevas primero)
    List<Noticia> findByActivoTrueOrderByFechaPublicacionDesc();
}