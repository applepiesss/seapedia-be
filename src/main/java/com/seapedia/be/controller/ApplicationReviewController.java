package com.seapedia.be.controller;

import com.seapedia.be.dto.ReviewRequest;
import com.seapedia.be.model.ApplicationReview;
import com.seapedia.be.repository.ApplicationReviewRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/reviews")
public class ApplicationReviewController {

    private final ApplicationReviewRepository reviewRepository;

    public ApplicationReviewController(ApplicationReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public List<ApplicationReview> getReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationReview submitReview(@Valid @RequestBody ReviewRequest request) {
        ApplicationReview review = ApplicationReview.builder()
                .reviewerName(request.reviewerName().trim())
                .rating(request.rating())
                .comment(request.comment().trim())
                .build();
        return reviewRepository.save(review);
    }
}
