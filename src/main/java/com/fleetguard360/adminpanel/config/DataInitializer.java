package com.fleetguard360.adminpanel.config;

import com.fleetguard360.adminpanel.model.Role;
import com.fleetguard360.adminpanel.model.User;
import com.fleetguard360.adminpanel.repository.RoleRepository;
import com.fleetguard360.adminpanel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        initRoles();

        // Create admin user if it doesn't exist
        createAdminUser();
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName(Role.ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);

            Role operatorRole = new Role();
            operatorRole.setName(Role.ERole.ROLE_OPERATOR);
            roleRepository.save(operatorRole);

            Role viewerRole = new Role();
            viewerRole.setName(Role.ERole.ROLE_VIEWER);
            roleRepository.save(viewerRole);
        }
    }

    private void createAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@fleetguard360.com");
            admin.setPassword(encoder.encode("admin123"));
            admin.setFullName("System Administrator");

            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Admin Role not found."));
            roles.add(adminRole);

            admin.setRoles(roles);
            userRepository.save(admin);
        }
    }
}
