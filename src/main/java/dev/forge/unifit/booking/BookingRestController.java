package dev.forge.unifit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookingRestController {

    private final BookingService bookingService;

    @GetMapping("/availableTimes")
    public ResponseEntity<List<LocalTime>> getAvailableTimes(@RequestParam("date") LocalDate date, @RequestParam("facilityId") Long id) {
        List<LocalTime> availableTimes = bookingService.getAvailableTimes(id,date);

        // Return the times in the response
        return new ResponseEntity<>(availableTimes, HttpStatus.OK);
    }
}

