package io.bootify.saven.domain;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "bills")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Bill {

    @Id
    @Column(nullable = false, updatable = false)
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private UUID id;

    @Column(nullable = false)
    private Long electricityUsed;

    @Column(nullable = false)
    private Long waterUsed;

    @Column(nullable = false)
    private Long gasUsed;

    @Column(nullable = false)
    private Long electricityCost;

    @Column(nullable = false)
    private Long waterCost;

    @Column(nullable = false)
    private Long gasCost;

    @Column(nullable = false)
    private Long totalCost;

    @Column(nullable = false)
    private LocalDateTime storedDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
