package dev.forge.unifit.admin;

import dev.forge.unifit.booking.Booking;
import dev.forge.unifit.booking.BookingService;
import dev.forge.unifit.facility.FacilityRevenue;
import dev.forge.unifit.facility.FacilityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/chart")
public class AdminRestController {

    private final FacilityService facilityService;
    private final BookingService bookingService;

    public AdminRestController(FacilityService facilityService, BookingService bookingService) {
        this.facilityService = facilityService;
        this.bookingService = bookingService;
    }

    @GetMapping("/revenue-monthly")
    public List<FacilityRevenue> getMonthlyRevenue() {
        return facilityService.getMonthlyRevenue(LocalDate.now());
    }

    @GetMapping("/revenue-by-facility")
    public Map<String, Double> getRevenueByFacilityType() {
        return facilityService.getRevenueByFacilityType(LocalDate.now().getYear());
    }

    @GetMapping("/bookings-facility")
    public Map<String,Long> getBookingsByFacility() {
        List<Booking> bookings = bookingService.getBookings();
        return countBookingsByFacility(bookings);
    }

    private Map<String,Long> countBookingsByFacility(List<Booking> bookings) {
        return bookings.stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getFacility().getName(),
                        Collectors.counting()
                ));
    }
}
