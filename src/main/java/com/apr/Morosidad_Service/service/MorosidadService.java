package com.apr.Morosidad_Service.service;

import com.apr.Morosidad_Service.dto.MorosoDTO;
import com.apr.Morosidad_Service.dto.MorosoResponseDTO;
import com.apr.Morosidad_Service.exception.ResourceNotFoundException;
import com.apr.Morosidad_Service.mapper.MorosoMapper;
import com.apr.Morosidad_Service.model.EstadoMoroso;
import com.apr.Morosidad_Service.model.Moroso;
import com.apr.Morosidad_Service.repository.MorosoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MorosidadService {

    private final MorosoRepository repository;
    private final WebClient.Builder webClientBuilder;

    public MorosidadService(MorosoRepository repository, WebClient.Builder webClientBuilder) {
        this.repository = repository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<MorosoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(MorosoMapper::toResponseDTO)
                .toList();
    }

    public MorosoResponseDTO buscarPorId(Long id) {
        Moroso moroso = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de morosidad no encontrado con id: " + id));
        return MorosoMapper.toResponseDTO(moroso);
    }

    public List<MorosoResponseDTO> buscarPorSocioId(Long socioId) {
        return repository.findBySocioId(socioId)
                .stream()
                .map(MorosoMapper::toResponseDTO)
                .toList();
    }

    public List<MorosoResponseDTO> buscarPorEstado(EstadoMoroso estado) {
        return repository.findByEstado(estado)
                .stream()
                .map(MorosoMapper::toResponseDTO)
                .toList();
    }

    public MorosoResponseDTO guardar(MorosoDTO dto) {
        // Validar que el socioId exista en Socio-Service
        validarSocioEnSocioService(dto.getSocioId());

        Moroso moroso = MorosoMapper.toEntity(dto);
        Moroso guardado = repository.save(moroso);
        return MorosoMapper.toResponseDTO(guardado);
    }

    public MorosoResponseDTO actualizar(Long id, MorosoDTO dto) {
        Moroso moroso = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de morosidad no encontrado con id: " + id));

        if (!moroso.getSocioId().equals(dto.getSocioId())) {
            validarSocioEnSocioService(dto.getSocioId());
        }

        moroso.setSocioId(dto.getSocioId());
        moroso.setMesesDeuda(dto.getMesesDeuda());
        moroso.setMontoTotalDeuda(dto.getMontoTotalDeuda());
        if (dto.getEstado() != null) moroso.setEstado(dto.getEstado());
        if (dto.getFechaDeteccion() != null) moroso.setFechaDeteccion(dto.getFechaDeteccion());
        if (dto.getFechaCorte() != null) moroso.setFechaCorte(dto.getFechaCorte());

        Moroso actualizado = repository.save(moroso);
        return MorosoMapper.toResponseDTO(actualizado);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> detectarMorosos() {
        int registrosCreados = 0;
        try {
            // Obtener todas las facturas en estado PENDIENTE o VENCIDA de Facturacion-Service
            List<Map> facturas = webClientBuilder.build().get()
                    .uri("http://facturacion-service/facturas")
                    .retrieve()
                    .bodyToFlux(Map.class)
                    .collectList()
                    .block();

            if (facturas != null) {
                // Agrupar deuda por socioId para facturas PENDIENTES o VENCIDAS
                Map<Long, Double> deudaPorSocio = new HashMap<>();
                Map<Long, Integer> facturasImpagasPorSocio = new HashMap<>();

                for (Map fac : facturas) {
                    String estadoStr = (String) fac.get("estado");
                    if ("PENDIENTE".equals(estadoStr) || "VENCIDA".equals(estadoStr)) {
                        Long socioId = ((Number) fac.get("socioId")).longValue();
                        Number monto = (Number) fac.get("montoPesos");
                        double valorMonto = monto != null ? monto.doubleValue() : 0.0;

                        deudaPorSocio.put(socioId, deudaPorSocio.getOrDefault(socioId, 0.0) + valorMonto);
                        facturasImpagasPorSocio.put(socioId, facturasImpagasPorSocio.getOrDefault(socioId, 0) + 1);
                    }
                }

                // Para cada socio con facturas impagas >= 2, considerarlo moroso
                for (Map.Entry<Long, Integer> entry : facturasImpagasPorSocio.entrySet()) {
                    if (entry.getValue() >= 2) {
                        Long socioId = entry.getKey();
                        double deudaTotal = deudaPorSocio.get(socioId);

                        // Si no existe ya un registro moroso activo para este socio, lo creamos
                        List<Moroso> existentes = repository.findBySocioId(socioId);
                        boolean activo = existentes.stream().anyMatch(m -> m.getEstado() == EstadoMoroso.MOROSO);

                        if (!activo) {
                            Moroso m = new Moroso();
                            m.setSocioId(socioId);
                            m.setMesesDeuda(entry.getValue());
                            m.setMontoTotalDeuda(BigDecimal.valueOf(deudaTotal));
                            m.setEstado(EstadoMoroso.MOROSO);
                            m.setFechaDeteccion(LocalDate.now());
                            repository.save(m);
                            registrosCreados++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[WARN] Error al detectar morosos: " + e.getMessage());
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("resultado", "OK");
        respuesta.put("registrosCreados", registrosCreados);
        respuesta.put("mensaje", "Detección automática ejecutada con éxito.");
        return respuesta;
    }

    public Map<String, Object> notificarRecordatorio(Long id) {
        Moroso moroso = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de morosidad no encontrado con id: " + id));

        Map<String, Object> notificacion = new HashMap<>();
        notificacion.put("tipo", "RECORDATORIO_CORTE");
        notificacion.put("socioId", moroso.getSocioId());
        notificacion.put("mesesDeuda", moroso.getMesesDeuda());
        notificacion.put("montoDeuda", moroso.getMontoTotalDeuda());
        notificacion.put("mensaje", String.format(
                "AVISO DE CORTE: Estimado socio %d, registra %d meses de deuda con un total de $%s. Por favor regularice su situación a la brevedad para evitar la suspensión del servicio hídrico.",
                moroso.getSocioId(),
                moroso.getMesesDeuda(),
                moroso.getMontoTotalDeuda() != null ? moroso.getMontoTotalDeuda().toString() : "0.00"));
        notificacion.put("simulado", true);
        return notificacion;
    }

    public void eliminar(Long id) {
        Moroso moroso = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de morosidad no encontrado con id: " + id));
        repository.delete(moroso);
    }

    private void validarSocioEnSocioService(Long socioId) {
        try {
            Boolean existe = webClientBuilder.build().get()
                    .uri("http://socio-service/socios/" + socioId)
                    .retrieve()
                    .toBodilessEntity()
                    .map(response -> response.getStatusCode().is2xxSuccessful())
                    .onErrorReturn(false)
                    .block();

            if (existe == null || !existe) {
                throw new IllegalArgumentException("El Socio con ID " + socioId + " no existe.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al comunicarse con Socio-Service: " + e.getMessage());
        }
    }
}
