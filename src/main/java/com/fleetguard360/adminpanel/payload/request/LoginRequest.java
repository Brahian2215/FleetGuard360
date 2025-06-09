package com.fleetguard360.adminpanel.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud de login de usuario")
public class LoginRequest {
    @NotBlank
    @Schema(description = "Nombre de usuario", example = "admin", required = true)
    private String username;

    @NotBlank
    @Schema(description = "Contraseña del usuario", example = "admin123", required = true)
    private String password;

    public @NotBlank String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank String username) {
        this.username = username;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }
}
