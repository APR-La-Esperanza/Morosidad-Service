package com.apr.Morosidad_Service.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "morosos")
public class Moroso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "socio_id", nullable = false)
    private Long socioId;

    @Column(name = "meses_deuda", nullable = false)
    private Integer mesesDeuda;

    @Column(name = "monto_total_deuda", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotalDeuda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMoroso estado;

    @Column(name = "fecha_deteccion")
    private LocalDate fechaDeteccion;

    @Column(name = "fecha_corte")
    private LocalDate fechaCorte;

    @PrePersist
    protected void onCreate() {
        if (this.fechaDeteccion == null) {
            this.fechaDeteccion = LocalDate.now();
        }
        if (this.estado == null) {
            this.estado = EstadoMoroso.MOROSO;
        }
    }

    public Moroso() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSocioId() { return socioId; }
    public void setSocioId(Long socioId) { this.socioId = socioId; }
    public Integer getMesesDeuda() { return mesesDeuda; }
    public void setMesesDeuda(Integer mesesDeuda) { this.mesesDeuda = mesesDeuda; }
    public BigDecimal getMontoTotalDeuda() { return montoTotalDeuda; }
    public void setMontoTotalDeuda(BigDecimal montoTotalDeuda) { this.montoTotalDeuda = montoTotalDeuda; }
    public EstadoMoroso getEstado() { return estado; }
    public void setEstado(EstadoMoroso estado) { this.estado = estado; }
    public LocalDate getFechaDeteccion() { return fechaDeteccion; }
    public void setFechaDeteccion(LocalDate fechaDeteccion) { this.fechaDeteccion = fechaDeteccion; }
    public LocalDate getFechaCorte() { return fechaCorte; }
    public void setFechaCorte(LocalDate fechaCorte) { this.fechaCorte = fechaCorte; }
}
