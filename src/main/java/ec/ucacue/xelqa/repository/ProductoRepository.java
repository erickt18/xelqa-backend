package ec.ucacue.xelqa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.ucacue.xelqa.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Permite al cajero buscar productos fácilmente ignorando mayúsculas/minúsculas
    List<Producto> findByNombreContainingIgnoreCaseAndDisponibleTrue(String nombre);
}