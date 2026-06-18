package ec.ucacue.xelqa.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.ucacue.xelqa.model.Noticia;
import ec.ucacue.xelqa.repository.NoticiaRepository;

@RestController
@RequestMapping("/api/noticias")
@CrossOrigin(origins = "*")
public class NoticiaController {

    private final NoticiaRepository noticiaRepository;

    public NoticiaController(NoticiaRepository noticiaRepository) {
        this.noticiaRepository = noticiaRepository;
    }

    // 1. Endpoint para que la App Móvil consuma el feed de noticias
    @GetMapping
    public List<Noticia> listarNoticias() {
        return noticiaRepository.findByActivoTrueOrderByFechaPublicacionDesc();
    }

    // 2. Endpoint para que el Panel de Administración publique nuevas noticias
    @PostMapping
    public ResponseEntity<Noticia> crearNoticia(@RequestBody Noticia noticia) {
        Noticia nuevaNoticia = noticiaRepository.save(noticia);
        return ResponseEntity.ok(nuevaNoticia);
    }
}
