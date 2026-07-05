package com.seapedia.be.repository;

import com.seapedia.be.model.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PromoRepository extends JpaRepository<Promo, Long> {
    Optional<Promo> findByCodeIgnoreCase(String code);
}
