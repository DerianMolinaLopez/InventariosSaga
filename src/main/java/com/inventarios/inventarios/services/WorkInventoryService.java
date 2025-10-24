package com.inventarios.inventarios.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventarios.inventarios.models.ItemEntity;
import com.inventarios.inventarios.repositories.ItemEntityRepository;
import com.mysql.cj.xdevapi.JsonArray;

import jakarta.transaction.Transactional;

@Service
public class WorkInventoryService {
    Logger logger = LoggerFactory.getLogger(WorkInventoryService.class);
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private ItemEntityRepository itemEntityRepository;

    public void workInventoryLogic(JsonNode node){
        try {

            List<ItemEntity> items = this.convertJsonNodeToItemModel(node);
            if(this.existencias(items)){
                this.discountInventory(items);

            }
            
        } catch (IOException e) {
            logger.info("Ocurrio un error durante la validacion del inventario: {}",e.getMessage());
        
        }
        
        
    }
    private List<ItemEntity> convertJsonNodeToItemModel(JsonNode node) throws IOException{
        JsonNode itemsNode = node.get("items");
        List<ItemEntity> items = mapper.readerForListOf(ItemEntity.class).readValue(itemsNode);
        return items;
    }
    private boolean existencias(List<ItemEntity> items) {
        return items.stream().allMatch(item -> {
            return itemEntityRepository.findById(item.getId())
                    .map(inventario -> inventario.getQty() >= item.getQty())
                    .orElse(false);
        });
    }
    @Transactional
    private void discountInventory(List<ItemEntity> items) {

        List<Long> ids = items.stream()
                .map(ItemEntity::getId)
                .toList();

        List<ItemEntity> inventario = itemEntityRepository.findAllById(ids);


        Map<Long, ItemEntity> inventarioMap = inventario.stream()
                .collect(Collectors.toMap(ItemEntity::getId, Function.identity()));


        items.forEach(item -> {
            ItemEntity stock = inventarioMap.get(item.getId());
            if (stock != null) {
                int nuevaCantidad = stock.getQty() - item.getQty();
                stock.setQty(Math.max(nuevaCantidad, 0)); 
            }
        });


        itemEntityRepository.saveAll(inventarioMap.values());
    }

    
    
    //TODO implementacion de la compensacion
    public void deleteOperationWithCorrelationId(String errorMessage){
        
    }
}
