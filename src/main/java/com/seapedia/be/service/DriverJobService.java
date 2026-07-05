package com.seapedia.be.service;

import com.seapedia.be.dto.DriverDashboardResponse;
import com.seapedia.be.dto.DriverJobResponse;
import com.seapedia.be.enums.DeliveryJobStatus;
import com.seapedia.be.enums.OrderStatus;
import com.seapedia.be.model.DeliveryJob;
import com.seapedia.be.model.OrderStatusHistory;
import com.seapedia.be.model.User;
import com.seapedia.be.repository.DeliveryJobRepository;
import com.seapedia.be.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DriverJobService {

    private final DeliveryJobRepository deliveryJobRepository;
    private final UserRepository userRepository;

    public DriverJobService(DeliveryJobRepository deliveryJobRepository, UserRepository userRepository) {
        this.deliveryJobRepository = deliveryJobRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
    }

    @Transactional(readOnly = true)
    public List<DriverJobResponse> getAvailableJobs() {
        return deliveryJobRepository.findByStatusOrderByCreatedAtDesc(DeliveryJobStatus.AVAILABLE)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DriverJobResponse getJobDetails(Long jobId) {
        DeliveryJob job = deliveryJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        return mapToResponse(job);
    }

    @Transactional
    public DriverJobResponse takeJob(String username, Long jobId) {
        User driver = getUser(username);
        
        // Prevent driver from taking multiple active jobs at once
        Optional<DeliveryJob> existingActiveJob = deliveryJobRepository.findByDriverAndStatus(driver, DeliveryJobStatus.TAKEN);
        if (existingActiveJob.isPresent()) {
            throw new IllegalArgumentException("You already have an active job");
        }

        DeliveryJob job = deliveryJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (job.getStatus() != DeliveryJobStatus.AVAILABLE || job.getDriver() != null) {
            throw new IllegalArgumentException("Job is no longer available");
        }

        job.setDriver(driver);
        job.setStatus(DeliveryJobStatus.TAKEN);
        job.setTakenAt(LocalDateTime.now());
        
        job.getOrder().setStatus(OrderStatus.SEDANG_DIKIRIM);
        job.getOrder().getStatusHistory().add(OrderStatusHistory.builder()
                .order(job.getOrder())
                .status(OrderStatus.SEDANG_DIKIRIM)
                .build());

        return mapToResponse(job);
    }

    @Transactional
    public DriverJobResponse completeJob(String username, Long jobId) {
        User driver = getUser(username);

        DeliveryJob job = deliveryJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (job.getDriver() == null || !job.getDriver().getId().equals(driver.getId())) {
            throw new IllegalArgumentException("You are not assigned to this job");
        }

        if (job.getStatus() != DeliveryJobStatus.TAKEN) {
            throw new IllegalArgumentException("Job cannot be completed from status: " + job.getStatus());
        }

        job.setStatus(DeliveryJobStatus.COMPLETED);
        job.setCompletedAt(LocalDateTime.now());
        
        job.getOrder().setStatus(OrderStatus.PESANAN_SELESAI);
        job.getOrder().getStatusHistory().add(OrderStatusHistory.builder()
                .order(job.getOrder())
                .status(OrderStatus.PESANAN_SELESAI)
                .build());

        return mapToResponse(job);
    }

    @Transactional(readOnly = true)
    public DriverDashboardResponse getDashboard(String username) {
        User driver = getUser(username);
        
        List<DeliveryJob> driverJobs = deliveryJobRepository.findByDriverOrderByCreatedAtDesc(driver);
        
        BigDecimal totalEarnings = BigDecimal.ZERO;
        DriverJobResponse activeJob = null;
        List<DriverJobResponse> jobHistory = new java.util.ArrayList<>();
        
        for (DeliveryJob job : driverJobs) {
            if (job.getStatus() == DeliveryJobStatus.COMPLETED) {
                totalEarnings = totalEarnings.add(job.getEarning());
                jobHistory.add(mapToResponse(job));
            } else if (job.getStatus() == DeliveryJobStatus.TAKEN) {
                activeJob = mapToResponse(job);
            }
        }
        
        return new DriverDashboardResponse(totalEarnings, activeJob, jobHistory);
    }

    private DriverJobResponse mapToResponse(DeliveryJob job) {
        return new DriverJobResponse(
                job.getId(),
                job.getOrder().getId(),
                job.getOrder().getStore().getStoreName(),
                job.getOrder().getDeliveryMethod(),
                job.getEarning(),
                job.getStatus(),
                job.getCreatedAt(),
                job.getTakenAt(),
                job.getCompletedAt()
        );
    }
}
