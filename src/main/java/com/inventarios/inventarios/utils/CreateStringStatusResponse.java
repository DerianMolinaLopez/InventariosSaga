package com.inventarios.inventarios.utils;

import org.springframework.stereotype.Component;

import com.inventarios.inventarios.enums.TypeMessage;

    /************************************************* */
    // Sintaxis de los headers de los mensajes
    // "COMPLETADO_{componentName}_{step}_{numberOfOperation}_{id_step}" para confirmaciones
    // "FALLO_{componentName}_{step}_{numberOfOperation}_{id_step}" En caso de errores se agrega el mensaje de detalle
    /************************************************* */
@Component
public class CreateStringStatusResponse {

    private final String componentName = "inventarios";
    private final int step = 1;

    public String buildResponse(TypeMessage typeMessage, String numberOfOperation, String idStep, String error) {
        String prefix = (typeMessage == TypeMessage.COMPLETED) ? "COMPLETADO" : "FALLO";

        String base = String.format("%s_%s_%d_%s_%s", prefix, componentName, step, numberOfOperation, idStep);

        if (typeMessage == TypeMessage.FAILED && error != null && !error.isBlank()) {
            // Limpieza del mensaje para que no tenga espacios o saltos de línea
            String cleanError = error.trim().replaceAll("\\s+", "-");
            base = String.format("%s_%s", base, cleanError);
        }

        return base;
    }
}

