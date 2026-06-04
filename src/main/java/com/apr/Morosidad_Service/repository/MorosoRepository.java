package com.apr.Morosidad_Service.repository;

import com.apr.Morosidad_Service.model.EstadoMoroso;
import com.apr.Morosidad_Service.model.Moroso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MorosoRepository extends JpaRepository<Moroso, Long> {
    List<Moroso> findBySocioId(Long socioId);
    List<Moroso> findByEstado(EstadoMoroso estado);
}
