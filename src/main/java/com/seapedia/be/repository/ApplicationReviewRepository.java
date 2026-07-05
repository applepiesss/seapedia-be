package com.seapedia.be.repository;

import com.seapedia.be.model.ApplicationReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationReviewRepository extends JpaRepository<ApplicationReview, Long> {
    List<ApplicationReview> findAllByOrderByCreatedAtDesc();
}
