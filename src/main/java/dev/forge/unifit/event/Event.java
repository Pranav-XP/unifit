package dev.forge.unifit.event;

import dev.forge.unifit.facility.Facility;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "event")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Event {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 100, message = "Description must not exceed 100 characters")
    private String title;

    @Size(max = 100, message = "Description must not exceed 100 characters")
    private String eventName;

    @Column(columnDefinition = "LONGTEXT")
    @Size(max = 300, message = "Description must not exceed 300 characters")
    private String description;

    private String time;

    private String date;

    @Column(name = "event_date_time", nullable = false)
    private LocalDateTime eventDateTime;

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;
}
