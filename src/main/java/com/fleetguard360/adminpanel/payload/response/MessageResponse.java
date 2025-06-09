package com.fleetguard360.adminpanel.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con un mensaje informativo")
public class MessageResponse {

    @Schema(description = "Mensaje informativo", example = "Operación completada exitosamente")
    private String message;

    // Constructor sin argumentos
    public MessageResponse() {}

    // Constructor con argumento
    public MessageResponse(String message) {
        this.message = message;
    }

    // Getter
    public String getMessage() {
        return message;
    }

    // Setter
    public void setMessage(String message) {
        this.message = message;
    }
}
