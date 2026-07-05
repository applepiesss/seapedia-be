package com.seapedia.be.repository;

import com.seapedia.be.enums.DeliveryJobStatus;
import com.seapedia.be.model.DeliveryJob;
import com.seapedia.be.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeliveryJobRepository extends JpaRepository<DeliveryJob, Long> {
    List<DeliveryJob> findByStatusOrderByCreatedAtDesc(DeliveryJobStatus status);
    List<DeliveryJob> findByDriverOrderByCreatedAtDesc(User driver);
    Optional<DeliveryJob> findByDriverAndStatus(User driver, DeliveryJobStatus status);
}
