package ec.ucacue.xelqa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "credenciales")
public class Credencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación directa con el ID del usuario/estudiante
    @Column(name = "usuario_id", unique = true, nullable = false)
    private Long usuarioId;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(unique = true, nullable = false, length = 10)
    private String cedula;

    @Column(length = 100)
    private String institucion = "Universidad Católica de Cuenca";
    
    @Column(nullable = false, length = 50)
    private String campus;

    @Column(nullable = false, length = 100)
    private String facultad;

    @Column(nullable = false, length = 100)
    private String carrera;

    @Column(name = "foto_url", columnDefinition = "TEXT")
    private String fotoUrl;

    public Credencial() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getInstitucion() { return institucion; }
    public void setInstitucion(String institucion) { this.institucion = institucion; }

    public String getFacultad() { return facultad; }
    public void setFacultad(String facultad) { this.facultad = facultad; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }
}