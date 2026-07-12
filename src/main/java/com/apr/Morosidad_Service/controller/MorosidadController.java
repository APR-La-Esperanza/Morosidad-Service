package com.apr.Morosidad_Service.controller;

import com.apr.Morosidad_Service.dto.MorosoDTO;
import com.apr.Morosidad_Service.dto.MorosoResponseDTO;
import com.apr.Morosidad_Service.model.EstadoMoroso;
import com.apr.Morosidad_Service.service.MorosidadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/morosos")
@Tag(name = "Morosidad", description = "Endpoints para el registro y gestión de socios morosos, y autorización de cortes de servicio.")
@SecurityRequirement(name = "bearerAuth")
public class MorosidadController {

    private final MorosidadService service;

    public MorosidadController(MorosidadService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar socios morosos", description = "Retorna la lista de morosos registrados. Permite filtrar por socioId o por estado (MOROSO, REGULARIZADO, CORTE_AUTORIZADO).")
    @ApiResponse(responseCode = "200", description = "Lista de morosos obtenida correctamente.")
    public ResponseEntity<List<MorosoResponseDTO>> listarTodos(
            @Parameter(description = "ID del socio para filtrar") @RequestParam(required = false) Long socioId,
            @Parameter(description = "Estado de morosidad para filtrar") @RequestParam(required = false) EstadoMoroso estado) {
        if (socioId != null) {
            return ResponseEntity.ok(service.buscarPorSocioId(socioId));
        }
        if (estado != null) {
            return ResponseEntity.ok(service.buscarPorEstado(estado));
        }
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar registro de moroso por ID", description = "Obtiene los detalles del registro de morosidad correspondiente al ID.")
    @ApiResponse(responseCode = "200", description = "Registro de moroso encontrado.")
    @ApiResponse(responseCode = "404", description = "El registro de morosidad no existe.")
    public ResponseEntity<MorosoResponseDTO> buscarPorId(
            @Parameter(description = "ID del registro de moroso", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar socio moroso", description = "Crea un nuevo registro de morosidad para un socio.")
    @ApiResponse(responseCode = "201", description = "Socio moroso registrado exitosamente.")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o inconsistentes.")
    public ResponseEntity<MorosoResponseDTO> guardar(
            @RequestBody(description = "Datos de morosidad del socio", required = true,
                         content = @Content(schema = @Schema(implementation = MorosoDTO.class),
                                            examples = @ExampleObject(value = "{\n  \"socioId\": 1,\n  \"mesesDeuda\": 3,\n  \"montoTotalDeuda\": 45000.0,\n  \"estado\": \"MOROSO\",\n  \"fechaDeteccion\": \"2025-06-15\"\n}")))
            @Valid @org.springframework.web.bind.annotation.RequestBody MorosoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar estado de moroso", description = "Modifica los datos del registro de moroso (por ejemplo, autorizar corte cambiando estado a CORTE_AUTORIZADO).")
    @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente.")
    @ApiResponse(responseCode = "400", description = "Datos del cuerpo de la petición incorrectos.")
    @ApiResponse(responseCode = "404", description = "No se halló el registro de morosidad especificado.")
    public ResponseEntity<MorosoResponseDTO> actualizar(
            @Parameter(description = "ID del registro de morosidad a actualizar", required = true) @PathVariable Long id,
            @RequestBody(description = "Nuevos datos del registro de morosidad", required = true,
                         content = @Content(schema = @Schema(implementation = MorosoDTO.class),
                                            examples = @ExampleObject(value = "{\n  \"socioId\": 1,\n  \"mesesDeuda\": 3,\n  \"montoTotalDeuda\": 45000.0,\n  \"estado\": \"CORTE_AUTORIZADO\",\n  \"fechaDeteccion\": \"2025-06-15\",\n  \"fechaCorte\": \"2025-07-01\"\n}")))
            @Valid @org.springframework.web.bind.annotation.RequestBody MorosoDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PostMapping("/detectar")
    @Operation(summary = "Detección automática de morosidad", description = "Ejecuta un proceso que consulta facturas impagas y registra deudores en morosidad automáticamente.")
    @ApiResponse(responseCode = "200", description = "Proceso ejecutado con éxito.")
    public ResponseEntity<java.util.Map<String, Object>> detectar() {
        return ResponseEntity.ok(service.detectarMorosos());
    }

    @PostMapping("/{id}/recordatorio")
    @Operation(summary = "Enviar recordatorio de corte", description = "Genera un recordatorio simulado previo al corte para un socio en mora.")
    @ApiResponse(responseCode = "200", description = "Recordatorio generado correctamente.")
    @ApiResponse(responseCode = "404", description = "No existe el registro de morosidad.")
    public ResponseEntity<java.util.Map<String, Object>> recordatorio(
            @Parameter(description = "ID del registro de morosidad", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(service.notificarRecordatorio(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro de moroso", description = "Borra el registro de morosidad del sistema.")
    @ApiResponse(responseCode = "204", description = "Registro eliminado con éxito.")
    @ApiResponse(responseCode = "404", description = "El registro de morosidad no existe.")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del registro a eliminar", required = true) @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
