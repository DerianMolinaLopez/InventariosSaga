package com.inventarios.inventarios.services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventarios.inventarios.models.HistoricDiscount;
import com.inventarios.inventarios.models.ItemEntity;
import com.inventarios.inventarios.repositories.HistoricDiscountRepository;

import jakarta.transaction.Transactional;

@Service
public class HistorticDiscountServcie {
    private final Logger logger = LoggerFactory.getLogger(HistorticDiscountServcie.class);
   @Autowired
   private HistoricDiscountRepository historicDiscountRepository;


   @Transactional
public void createHistory(List<ItemEntity> items, String correlationId) {
    logger.info("Iniciando con la creacion del descuento historico: {} items, correlationId={}", items.size(), correlationId);

    LocalDateTime now = LocalDateTime.now();
    List<HistoricDiscount> historicos = items.stream()
        .map(item -> buildHistoricDiscount(item, correlationId, now))
        .toList();
    logger.info("lARGO DE LA LISTA: {}",historicos.size());



    historicDiscountRepository.saveAll(historicos);
}

private HistoricDiscount buildHistoricDiscount(ItemEntity itemEntity, String correlationId, LocalDateTime createdAt) {
    HistoricDiscount historic = new HistoricDiscount();
    historic.setCorrelationId(correlationId);
    historic.setLineId(itemEntity.getLineId());
    historic.setName(itemEntity.getName());
    historic.setSku(itemEntity.getSku());
    historic.setQty(itemEntity.getQty());
    historic.setVendor(itemEntity.getVendor());
    historic.setCreateAt(createdAt); 
    return historic;
}

}
