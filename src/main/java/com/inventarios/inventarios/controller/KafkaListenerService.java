package com.inventarios.inventarios.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventarios.inventarios.constants.StringsKafkaConstants;
import com.inventarios.inventarios.services.WorkInventoryService;

@Controller
public class KafkaListenerService {
    @Autowired
    private WorkInventoryService workInventoryService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(KafkaListener.class);
    /**
     * 
     * La entrada de datos puede recibir un mensaje para mandar el objeto que sea la
     * logica del servicio o tambien una cadena de texto que corresponda a un error
     * 
     */
     @KafkaListener(topics=StringsKafkaConstants.TOPIC_INVENTORY, groupId=StringsKafkaConstants.TOPIC_INVENTORY)
      public void listen(@Payload String message) {
                logger.info("Mensaje recibido de kafka: {}",message);
                
                try{
                    JsonNode node = this.mapper.readTree(message);
                    this.workInventoryService.workInventoryLogic(node);
                }catch(JsonProcessingException e){
                    this.logger.info("Ocurrio un error drante la operacion: {}",e.getMessage());
                }
                //queda pendiente agregar un header, ese header debe de contener el status si es error o una inservion
      
      }
}
