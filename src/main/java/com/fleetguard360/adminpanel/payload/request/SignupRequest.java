package com.fleetguard360.adminpanel.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "Solicitud de registro de nuevo usuario")
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    @Schema(description = "Nombre de usuario", example = "johndoe", required = true)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    @Schema(description = "Correo electrónico", example = "john.doe@example.com", required = true)
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    @Schema(description = "Contraseña", example = "password123", required = true)
    private String password;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nombre completo", example = "John Doe", required = true)
    private String fullName;

    @Schema(description = "Roles asignados al usuario", example = "[\"admin\", \"operator\"]")
    private Set<String> roles;

    public @NotBlank @Size(min = 3, max = 20) String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank @Size(min = 3, max = 20) String username) {
        this.username = username;
    }

    public @NotBlank @Size(max = 50) @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank @Size(max = 50) @Email String email) {
        this.email = email;
    }

    public @NotBlank @Size(min = 6, max = 40) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @Size(min = 6, max = 40) String password) {
        this.password = password;
    }

    public @NotBlank @Size(max = 100) String getFullName() {
        return fullName;
    }

    public void setFullName(@NotBlank @Size(max = 100) String fullName) {
        this.fullName = fullName;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
