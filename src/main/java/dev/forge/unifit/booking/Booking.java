package dev.forge.unifit.booking;

import dev.forge.unifit.facility.Facility;
import dev.forge.unifit.transaction.Transaction;
import dev.forge.unifit.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "booking")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Booking {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate bookedDate;

    private LocalTime start;

    private LocalTime end;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private boolean isMaintenance;

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @OneToOne(mappedBy = "booking")
    private Transaction transaction;

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

}
