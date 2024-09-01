package dev.forge.unifit.booking;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingFormDTO {

    private Long facilityId;
    private Long userId;
    private LocalDate bookedDate;
    private LocalTime start;
    private LocalTime end;


}
