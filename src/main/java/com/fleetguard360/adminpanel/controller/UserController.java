package com.fleetguard360.adminpanel.controller;

import com.fleetguard360.adminpanel.model.Role;
import com.fleetguard360.adminpanel.model.User;
import com.fleetguard360.adminpanel.payload.request.UpdateUserRequest;
import com.fleetguard360.adminpanel.payload.response.MessageResponse;
import com.fleetguard360.adminpanel.repository.RoleRepository;
import com.fleetguard360.adminpanel.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private static final String ROLE_NOT_FOUND_ERROR = "Error: Role is not found.";

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista de todos los usuarios registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener usuario por ID", description = "Retorna un usuario específico según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID del usuario a buscar") @PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID del usuario a actualizar") @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest updateRequest) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFullName(updateRequest.getFullName());
                    user.setEmail(updateRequest.getEmail());

                    if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
                        user.setPassword(encoder.encode(updateRequest.getPassword()));
                    }

                    if (updateRequest.getRoles() != null) {
                        Set<Role> roles = new HashSet<>();
                        updateRequest.getRoles().forEach(role -> {
                            switch (role) {
                                case "admin":
                                    Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                                            .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_ERROR));
                                    roles.add(adminRole);
                                    break;
                                case "operator":
                                    Role modRole = roleRepository.findByName(Role.ERole.ROLE_OPERATOR)
                                            .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_ERROR));
                                    roles.add(modRole);
                                    break;
                                default:
                                    Role userRole = roleRepository.findByName(Role.ERole.ROLE_VIEWER)
                                            .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_ERROR));
                                    roles.add(userRole);
                            }
                        });
                        user.setRoles(roles);
                    }

                    user.setActive(updateRequest.isActive());

                    userRepository.save(user);
                    return ResponseEntity.ok(new MessageResponse("User updated successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "ID del usuario a eliminar") @PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
