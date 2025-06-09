package com.fleetguard360.adminpanel.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Respuesta con el token JWT y datos del usuario autenticado")
public class JwtResponse {
    @Schema(description = "Token JWT para autenticación")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer", defaultValue = "Bearer")
    private String type = "Bearer";

    @Schema(description = "ID del usuario")
    private Long id;

    @Schema(description = "Nombre de usuario")
    private String username;

    @Schema(description = "Correo electrónico del usuario")
    private String email;

    @Schema(description = "Roles del usuario")
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
