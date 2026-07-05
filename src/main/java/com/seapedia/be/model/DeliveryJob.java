package com.seapedia.be.model;

import com.seapedia.be.enums.DeliveryJobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_jobs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryJob {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private CustomerOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = true)
    private User driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryJobStatus status;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal earning;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = true)
    private LocalDateTime takenAt;

    @Column(nullable = true)
    private LocalDateTime completedAt;
}
