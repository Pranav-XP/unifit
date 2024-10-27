package dev.forge.unifit.booking;

import dev.forge.unifit.user.User;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface IBookingService {
    List<Booking> createBooking(List<BookingFormDTO> bookings);

    Booking setMaintenance(BookingFormDTO booking);

    List<Booking> getBookingByStatus(BookingStatus status);
    List<Booking> getBookings();

    Optional<Booking> getBooking(Long id);

    List<Booking> getBookingsByUser(User user);
    List<LocalTime> getAvailableTimes(Long facilityId, LocalDate date);

    Booking saveBooking(Booking booking);

    void cancelBookings(List<Booking> cancelledBookings);

    Page<Booking> getPaginated(int pageNo, int pageSize);
}
