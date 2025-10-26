package com.inventarios.inventarios.controller;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventarios.inventarios.constants.StringsKafkaConstants;
import com.inventarios.inventarios.enums.TypeMessage;
import com.inventarios.inventarios.exceptions.WorkInventoryLogicException;
import com.inventarios.inventarios.services.WorkInventoryService;
import com.inventarios.inventarios.utils.CreateStringStatusResponse;

@Controller
public class KafkaListenerService {
    @Autowired
    private WorkInventoryService workInventoryService;
    @Autowired
    private ControllerKafkaPublisher controllerKafkaPublisher;
    @Autowired
    private CreateStringStatusResponse createStringStatusResponse;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String topicError = "errores";
    private final String topicConfirmation = "confirmaciones";
    
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(KafkaListener.class);

    /**
     * 
     * La entrada de datos puede recibir un mensaje para mandar el objeto que sea la
     * logica del servicio o tambien una cadena de texto que corresponda a un error
     * 
     */
    @KafkaListener(topics = StringsKafkaConstants.TOPIC_INVENTORY, groupId = StringsKafkaConstants.TOPIC_INVENTORY)
    public void listen(@Payload String message) {
              // queda pendiente agregar un header, ese header debe de contener el status si
        // es error o una inservion
        logger.info("Mensaje recibido de kafka: {}", message);
        // TODO hay demaciados string magicos que aparecen de la nada
        String numeroOperacion = "desconocido";
        String idStep = "desconocido";

        try {
            JsonNode node = mapper.readTree(message);
             numeroOperacion = node.get("correlationId").asText();
             idStep = node.get("idStep").asText();
            workInventoryService.workInventoryLogic(node);
             //todo hay que buscar la manera de reducirt este try catch
        } catch (IOException e) {
            logger.error("Ocurrio un error durante la operacion: {}", e.getMessage(), e);
            String messageExtracted = e.getMessage();
            String messageError = this.createStringStatusResponse.buildResponse(TypeMessage.FAILED, numeroOperacion, idStep, messageExtracted);
            this.controllerKafkaPublisher.publish(messageError, topicError);
            
        }catch (WorkInventoryLogicException e){
            logger.error("Ocurrio un error durante la operacion: {}", e.getMessage(), e);
            String messageExtracted = e.getMessage();
            String messageError = this.createStringStatusResponse.buildResponse(TypeMessage.FAILED, numeroOperacion, idStep, messageExtracted);
            this.controllerKafkaPublisher.publish(messageError, topicError);

        }

    }


    

}

