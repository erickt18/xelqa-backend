package ec.ucacue.xelqa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.ucacue.xelqa.model.Credencial;

@Repository
public interface CredencialRepository extends JpaRepository<Credencial, Long> {
    Optional<Credencial> findByUsuarioId(Long usuarioId);
}