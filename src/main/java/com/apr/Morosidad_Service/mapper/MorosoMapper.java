package com.apr.Morosidad_Service.mapper;

import com.apr.Morosidad_Service.dto.MorosoDTO;
import com.apr.Morosidad_Service.dto.MorosoResponseDTO;
import com.apr.Morosidad_Service.model.Moroso;

public class MorosoMapper {

    public static Moroso toEntity(MorosoDTO dto) {
        if (dto == null) return null;
        Moroso moroso = new Moroso();
        moroso.setSocioId(dto.getSocioId());
        moroso.setMesesDeuda(dto.getMesesDeuda());
        moroso.setMontoTotalDeuda(dto.getMontoTotalDeuda());
        if (dto.getEstado() != null) moroso.setEstado(dto.getEstado());
        if (dto.getFechaDeteccion() != null) moroso.setFechaDeteccion(dto.getFechaDeteccion());
        if (dto.getFechaCorte() != null) moroso.setFechaCorte(dto.getFechaCorte());
        return moroso;
    }

    public static MorosoResponseDTO toResponseDTO(Moroso moroso) {
        if (moroso == null) return null;
        MorosoResponseDTO dto = new MorosoResponseDTO();
        dto.setId(moroso.getId());
        dto.setSocioId(moroso.getSocioId());
        dto.setMesesDeuda(moroso.getMesesDeuda());
        dto.setMontoTotalDeuda(moroso.getMontoTotalDeuda());
        dto.setEstado(moroso.getEstado());
        dto.setFechaDeteccion(moroso.getFechaDeteccion());
        dto.setFechaCorte(moroso.getFechaCorte());
        return dto;
    }
}
