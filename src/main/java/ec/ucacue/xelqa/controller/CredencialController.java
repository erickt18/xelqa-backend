package ec.ucacue.xelqa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.ucacue.xelqa.model.Credencial;
import ec.ucacue.xelqa.repository.CredencialRepository;

@RestController
@RequestMapping("/api/credencial")
@CrossOrigin(origins = "*")
public class CredencialController {

    private final CredencialRepository credencialRepository;

    public CredencialController(CredencialRepository credencialRepository) {
        this.credencialRepository = credencialRepository;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Credencial> obtenerCredencial(@PathVariable Long usuarioId) {
        return credencialRepository.findByUsuarioId(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}