package com.seapedia.be.seeder;

import com.seapedia.be.enums.Role;
import com.seapedia.be.model.User;
import com.seapedia.be.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.Optional;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, Environment env) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
    }

    @Override
    public void run(String... args) {
        if (Boolean.parseBoolean(env.getProperty("seed.admin"))) {
            Optional<User> adminOpt = userRepository.findByUsername("admin");
            if (adminOpt.isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .passwordHash(passwordEncoder.encode("adminseapedia"))
                        .email("admin@seapedia.com")
                        .roles(Set.of(Role.ADMIN))
                        .build();
                userRepository.save(admin);
                System.out.println("=========================================================");
                System.out.println("Admin account successfully seeded!");
                System.out.println("Username: admin");
                System.out.println("Password: adminseapedia");
                System.out.println("=========================================================");
            } else {
                System.out.println("=========================================================");
                System.out.println("Admin account already exists. Skipping seeding.");
                System.out.println("=========================================================");
            }
        }
    }
}
