package com.apr.Morosidad_Service.dto;

import com.apr.Morosidad_Service.model.EstadoMoroso;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MorosoResponseDTO {

    private Long id;
    private Long socioId;
    private Integer mesesDeuda;
    private BigDecimal montoTotalDeuda;
    private EstadoMoroso estado;
    private LocalDate fechaDeteccion;
    private LocalDate fechaCorte;

    public MorosoResponseDTO() {
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
