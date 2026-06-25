package com.example.admin.entity;

import com.example.admin.entity.ApprovalStatus;
import com.example.admin.entity.SpaceCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Entity
@Table(name = "spaces")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "host_id", nullable = false)
    private String hostId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    @Column(nullable = false)
    private String address;

    @Column(name = "thumbnail_url", nullable = false)
    private String thumbnailUrl;

    @Column(name = "price_per_hour", nullable = false)
    private Integer pricePerHour;

    @Enumerated(EnumType.STRING)
    @Column(name = "admin_status", nullable = false)
    private ApprovalStatus adminStatus;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpaceCategory category;

    @Column(precision = 10, scale = 8)
    private BigDecimal lat;

    @Column(precision = 11, scale = 8)
    private BigDecimal lng;

    private String phone;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void update(
            ApprovalStatus adminStatus
    ){
        this.adminStatus = adminStatus;
    }
}