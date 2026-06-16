package ec.ucacue.xelqa.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ec.ucacue.xelqa.model.Producto;
import ec.ucacue.xelqa.repository.ProductoRepository;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @GetMapping("/buscar")
    public List<Producto> buscarPorNombre(@RequestParam String q) {
        return productoRepository.findByNombreContainingIgnoreCaseAndDisponibleTrue(q);
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoRepository.save(producto));
    }

    // NUEVO: Método para editar un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<Producto> editarProducto(@PathVariable Long id, @RequestBody Producto detalles) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setNombre(detalles.getNombre());
                    producto.setDescripcion(detalles.getDescripcion());
                    producto.setPrecio(detalles.getPrecio());
                    producto.setStock(detalles.getStock());
                    producto.setCategoria(detalles.getCategoria());
                    producto.setDisponible(detalles.getDisponible());
                    return ResponseEntity.ok(productoRepository.save(producto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // NUEVO: Método para "eliminar" (Soft Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setDisponible(false);
                    productoRepository.save(producto);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}