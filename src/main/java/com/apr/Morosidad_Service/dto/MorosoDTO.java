package com.apr.Morosidad_Service.dto;

import com.apr.Morosidad_Service.model.EstadoMoroso;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MorosoDTO {

    @NotNull(message = "El ID de socio es obligatorio")
    private Long socioId;

    @NotNull(message = "La cantidad de meses de deuda es obligatoria")
    @Min(value = 0, message = "Los meses de deuda no pueden ser negativos")
    private Integer mesesDeuda;

    @NotNull(message = "El monto total de deuda es obligatorio")
    private BigDecimal montoTotalDeuda;

    private EstadoMoroso estado;
    private LocalDate fechaDeteccion;
    private LocalDate fechaCorte;

    public MorosoDTO() {
    }

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
