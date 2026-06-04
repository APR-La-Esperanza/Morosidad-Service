package com.apr.Morosidad_Service.controller;

import com.apr.Morosidad_Service.dto.MorosoDTO;
import com.apr.Morosidad_Service.dto.MorosoResponseDTO;
import com.apr.Morosidad_Service.model.EstadoMoroso;
import com.apr.Morosidad_Service.service.MorosidadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/morosos")
public class MorosidadController {

    private final MorosidadService service;

    public MorosidadController(MorosidadService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MorosoResponseDTO>> listarTodos(
            @RequestParam(required = false) Long socioId,
            @RequestParam(required = false) EstadoMoroso estado) {
        if (socioId != null) {
            return ResponseEntity.ok(service.buscarPorSocioId(socioId));
        }
        if (estado != null) {
            return ResponseEntity.ok(service.buscarPorEstado(estado));
        }
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MorosoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<MorosoResponseDTO> guardar(@Valid @RequestBody MorosoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MorosoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MorosoDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
