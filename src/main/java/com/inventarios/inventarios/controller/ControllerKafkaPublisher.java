package com.inventarios.inventarios.controller;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ControllerKafkaPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ControllerKafkaPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publish(String message,String topic) {
        kafkaTemplate.send(topic, message);
    }

    
   
    
}

