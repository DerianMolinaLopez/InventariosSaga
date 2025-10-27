package com.inventarios.inventarios.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventarios.inventarios.models.HistoricDiscount;
@Repository
public interface HistoricDiscountRepository extends JpaRepository<HistoricDiscount, Long> {

    List<HistoricDiscount> findByCorrelationId(String correlationId);
    
}
