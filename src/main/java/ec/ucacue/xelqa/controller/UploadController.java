package ec.ucacue.xelqa.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ec.ucacue.xelqa.model.Usuario;
import ec.ucacue.xelqa.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    private final UsuarioRepository usuarioRepository;

    public UploadController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping(value = "/foto-perfil/{usuarioId}", consumes = "multipart/form-data")
    public ResponseEntity<?> subirFotoPerfil(@PathVariable Long usuarioId, @RequestParam("archivo") MultipartFile archivo) {
        
        if (archivo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "El archivo está vacío."));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Usuario no encontrado."));
        }

        try {
            // 1. Crear la carpeta si no existe
            String nombreCarpeta = "uploads/";
            Path rutaCarpeta = Paths.get(nombreCarpeta);
            if (!Files.exists(rutaCarpeta)) {
                Files.createDirectories(rutaCarpeta);
            }

            // 2. Limpiar el nombre del archivo (para evitar nombres raros) y armar la ruta
            String nombreArchivo = usuarioId + "_" + archivo.getOriginalFilename().replace(" ", "_");
            Path rutaCompleta = Paths.get(nombreCarpeta + nombreArchivo);

            // 3. Escribir físicamente el archivo en tu disco duro
            Files.write(rutaCompleta, archivo.getBytes());

            // 4. Armar la URL pública y guardarla en el usuario
            String urlImagen = "http://localhost:8080/uploads/" + nombreArchivo;
            
            Usuario usuario = usuarioOpt.get();
            usuario.setFotoUrl(urlImagen);
            usuarioRepository.save(usuario);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Foto subida exitosamente.",
                    "url", urlImagen
            ));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al guardar la imagen."));
        }
    }
}