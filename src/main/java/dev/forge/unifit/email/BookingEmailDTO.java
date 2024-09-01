package dev.forge.unifit.email;

import lombok.*;


@Getter
@Setter
public class BookingEmailDTO {
    private String customerName;

    private String bookingId;

    private String bookingDate;

    private String facilityName;

    private String bookingStart;

    private String bookingEnd;

    private String total;

    private String status;

}
