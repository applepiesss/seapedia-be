package com.seapedia.be.seeder;

import com.seapedia.be.enums.Role;
import com.seapedia.be.model.BuyerWallet;
import com.seapedia.be.model.SellerStore;
import com.seapedia.be.model.User;
import com.seapedia.be.repository.BuyerWalletRepository;
import com.seapedia.be.repository.SellerStoreRepository;
import com.seapedia.be.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
import java.util.Optional;

@Component
public class DemoDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SellerStoreRepository storeRepository;
    private final BuyerWalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    public DemoDataSeeder(
            UserRepository userRepository, 
            SellerStoreRepository storeRepository,
            BuyerWalletRepository walletRepository,
            PasswordEncoder passwordEncoder, 
            Environment env) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
    }

    @Override
    public void run(String... args) {
        if (Boolean.parseBoolean(env.getProperty("seed.demo"))) {
            System.out.println("=========================================================");
            System.out.println("Starting Demo Data Seeder...");

            // 1. Admin
            seedUser("admin", "adminseapedia", "admin@seapedia.com", Set.of(Role.ADMIN));

            // 2. Seller
            User seller = seedUser("seller1", "sellerseapedia", "seller1@seapedia.com", Set.of(Role.SELLER));
            if (seller != null && storeRepository.findByOwner(seller).isEmpty()) {
                storeRepository.save(SellerStore.builder().owner(seller).storeName("Toko Seller Seapedia").build());
            }

            // 3. Buyer
            User buyer = seedUser("buyer1", "buyerseapedia", "buyer1@seapedia.com", Set.of(Role.BUYER));
            if (buyer != null && walletRepository.findByBuyer(buyer).isEmpty()) {
                walletRepository.save(BuyerWallet.builder().buyer(buyer).balance(new BigDecimal("1000000")).build());
            }

            // 4. Driver
            seedUser("driver1", "driverseapedia", "driver1@seapedia.com", Set.of(Role.DRIVER));

            System.out.println("Demo data successfully seeded!");
            System.out.println("=========================================================");
        }
    }

    private User seedUser(String username, String password, String email, Set<Role> roles) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) {
            User user = User.builder()
                    .username(username)
                    .passwordHash(passwordEncoder.encode(password))
                    .email(email)
                    .roles(roles)
                    .build();
            return userRepository.save(user);
        }
        return opt.get();
    }
}
