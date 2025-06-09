package com.fleetguard360.adminpanel.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "Solicitud para actualizar un usuario existente")
public class UpdateUserRequest {
    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nombre completo", example = "John Doe", required = true)
    private String fullName;

    @NotBlank
    @Size(max = 50)
    @Email
    @Schema(description = "Correo electrónico", example = "john.doe@example.com", required = true)
    private String email;

    @Schema(description = "Nueva contraseña (opcional)", example = "newpassword123")
    private String password;

    @Schema(description = "Roles asignados al usuario", example = "[\"admin\", \"operator\"]")
    private Set<String> roles;

    @Schema(description = "Indica si el usuario está activo", example = "true", defaultValue = "true")
    private boolean active = true;

    public @NotBlank @Size(max = 100) String getFullName() {
        return fullName;
    }

    public void setFullName(@NotBlank @Size(max = 100) String fullName) {
        this.fullName = fullName;
    }

    public @NotBlank @Size(max = 50) @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank @Size(max = 50) @Email String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
