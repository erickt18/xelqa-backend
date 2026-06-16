package ec.ucacue.xelqa.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación de muchos a uno: Muchas transacciones pertenecen a una billetera
    @ManyToOne
    @JoinColumn(name = "billetera_id", nullable = false)
    private Billetera billetera;

    @Column(nullable = false, length = 20)
    private String tipo; // "RECARGA" o "CONSUMO"

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(length = 255)
    private String descripcion;

    // Constructor vacío exigido por JPA
    public Transaccion() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Billetera getBilletera() { return billetera; }
    public void setBilletera(Billetera billetera) { this.billetera = billetera; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}