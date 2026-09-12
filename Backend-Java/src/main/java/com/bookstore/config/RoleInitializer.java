package com.bookstore.config;

import com.bookstore.entity.Role;
import com.bookstore.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RoleInitializer {

    @Bean
    public CommandLineRunner initializeRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("USER").isEmpty()) {
                roleRepository.save(new Role("USER", "Regular user with basic permissions"));
                log.info("✅ Created USER role");
            }

            if (roleRepository.findByName("ADMIN").isEmpty()) {
                roleRepository.save(new Role("ADMIN", "Administrator with full permissions"));
                log.info("✅ Created ADMIN role");
            }

            if (roleRepository.findByName("MODERATOR").isEmpty()) {
                roleRepository.save(new Role("MODERATOR", "Moderator with limited admin permissions"));
                log.info("✅ Created MODERATOR role");
            }

            if (roleRepository.findByName("SELLER").isEmpty()) {
                roleRepository.save(new Role("SELLER", "Seller can manage their own products"));
                log.info("✅ Created SELLER role");
            }

            log.info("📋 Roles initialized successfully");
        };
    }
}
