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

import java.util.List;

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
