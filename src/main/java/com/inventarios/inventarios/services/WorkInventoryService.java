package com.inventarios.inventarios.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventarios.inventarios.controller.ControllerKafkaPublisher;
import com.inventarios.inventarios.enums.TypeMessage;
import com.inventarios.inventarios.exceptions.WorkInventoryLogicException;
import com.inventarios.inventarios.models.ItemEntity;
import com.inventarios.inventarios.repositories.ItemEntityRepository;
import com.inventarios.inventarios.utils.CreateStringStatusResponse;

import jakarta.transaction.Transactional;

@Service
public class WorkInventoryService {
    Logger logger = LoggerFactory.getLogger(WorkInventoryService.class);
    ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private ItemEntityRepository itemEntityRepository;
    @Autowired
    private HistorticDiscountServcie historticDiscountServcie;

        @Autowired
    private ControllerKafkaPublisher controllerKafkaPublisher;
    @Autowired
    private CreateStringStatusResponse createStringStatusResponse;

    private final String  topic = "confirmaciones";

    public void workInventoryLogic(JsonNode node, String idStep) throws WorkInventoryLogicException,IOException{
        logger.info("Iniciando la validacion para el grabado del inventario y el historico");


            List<ItemEntity> items = this.convertJsonNodeToItemModel(node);
            String correlationId = node.get("correlationId").asText();
        
            if(this.existenciasDebug(items)){
                this.discountInventory(items);
                this.historticDiscountServcie.createHistory(items,correlationId);
                String numeroOperacion = node.get("correlationId").asText();
             
                  logger.info("Procedimeinto completado para la operacion:{} procediendo a la confirmacion de la transaccion",numeroOperacion);
                this.sendMessageConfirm(numeroOperacion,idStep);
               
            
                

            }else{
                logger.info("No hay existencias para satisfacer la compra");
                throw new WorkInventoryLogicException("No hay existencias para satisfacer la compra");
            }
        
    }
   private List<ItemEntity> convertJsonNodeToItemModel(JsonNode node) throws IOException {
    mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    JsonNode itemsNode = node.get("items");
    logger.info("Node mapeado");

    List<ItemEntity>  nodos = mapper.readerForListOf(ItemEntity.class).readValue(itemsNode);

    nodos.forEach(c-> System.out.println(c));
    return nodos;
}

 
    private boolean existenciasDebug(List<ItemEntity> items) {
    return items.stream()
        .peek(item -> logger.info("Verificando item: " + item.getLineId()))
        .allMatch(item -> {
            return itemEntityRepository.findById(item.getLineId())
                .map(stock -> {
                    logger.info("Comparando → Stock: " + stock.getQty() + ", Solicitado: " + item.getQty());
                    return stock.getQty() >= item.getQty();
                })
                .orElseGet(() -> {
                    logger.info("No se encontró en inventario → " + item.getLineId());
                    return false;
                });
        });
}


    @Transactional
    private void discountInventory(List<ItemEntity> items) {


        List<String> lineIds = items.stream()
            .map(ItemEntity::getLineId)
            .toList();

        List<ItemEntity> inventario = itemEntityRepository.findAllById(lineIds);

        Map<String, ItemEntity> inventarioMap = inventario.stream()
            .collect(Collectors.toMap(ItemEntity::getLineId, Function.identity()));

        items.forEach(item -> {
            ItemEntity stock = inventarioMap.get(item.getLineId());
            if (stock != null) {
                int nuevaCantidad = stock.getQty() - item.getQty();
                stock.setQty(Math.max(nuevaCantidad, 0));
            }
        });

        itemEntityRepository.saveAll(inventarioMap.values());
    }

    private void sendMessageConfirm(String numeroOperacion,String idPaso){
            String exitMessage = this.createStringStatusResponse.buildResponse(TypeMessage.COMPLETED, numeroOperacion, idPaso, "");
            this.controllerKafkaPublisher.publish(exitMessage, topic);

    }


    
    
    //TODO implementacion de la compensacion
    public void deleteOperationWithCorrelationId(String errorMessage){
        
    }
}
