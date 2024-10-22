package dev.forge.unifit.notification;

import dev.forge.unifit.booking.Booking;
import dev.forge.unifit.facility.FacilityStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification<bookings> {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "notification", cascade = {CascadeType.PERSIST, CascadeType.MERGE} , fetch = FetchType.EAGER)
    private List<Booking> bookings;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;


}
