package dev.forge.unifit.booking;

import dev.forge.unifit.facility.Facility;
import dev.forge.unifit.facility.FacilityStatus;
import dev.forge.unifit.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    List<Booking> findAllByUser(User user);

    List<Booking> findAllByStatus(BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.bookedDate = :maintenanceDate " +
            "AND b.facility = :facility " +
            "AND b.start >= :maintenanceStartTime " +
            "AND b.end <= :maintenanceEndTime")
    List<Booking> findBookingsBetweenMaintenanceTimes(
            @Param("maintenanceDate") LocalDate maintenanceDate,
            @Param("facility") Facility facility,
            @Param("maintenanceStartTime") LocalTime maintenanceStartTime,
            @Param("maintenanceEndTime") LocalTime maintenanceEndTime
    );
    @Query("SELECT b.start FROM Booking b WHERE b.bookedDate = :date")
    List<LocalTime> findBookedTimesByDate(@Param("date") LocalDate date);

    @Query("SELECT b.start FROM Booking b WHERE b.facility.id = :facilityId AND b.bookedDate = :date")
    List<LocalTime> findBookedStartTimesByFacilityAndDate(@Param("facilityId") Long facilityId, @Param("date") LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT b FROM Booking b " +
            "WHERE b.bookedDate = :bookedDate " +
            "AND b.start = :start " +
            "AND b.facility.id = :facilityId")
    Optional<Booking> findBookingByBookedDateAndStartAndFacility(@Param("bookedDate") LocalDate bookedDate,
                                                                 @Param("start") LocalTime start,
                                                                 @Param("facilityId") Long facilityId);
    //Integer countBookingsByFacility(Facility facility);
    //Booking findByBookedDateAndStatus(LocalDate bookedDate, BookingStatus status);
}
