package dev.forge.unifit.email;


import dev.forge.unifit.booking.Booking;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Component
public class EmailService {


    private Map<String,String> createBookingEmailPayload(Booking booking){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");
        BookingEmailDTO bookingPayload;
        bookingPayload = new BookingEmailDTO();

        String customerName = booking.getUser().getFirstName() + " " + booking.getUser().getLastName();

        bookingPayload.setBookingId(booking.getId().toString());
        bookingPayload.setCustomerName(customerName);
        bookingPayload.setBookingDate(booking.getBookedDate().format(dateFormatter));
        bookingPayload.setBookingStart(booking.getStart().format(timeFormatter));
        bookingPayload.setBookingEnd(booking.getEnd().format(timeFormatter));
        bookingPayload.setFacilityName(booking.getFacility().getName());
        bookingPayload.setStatus(booking.getStatus().getDisplayName());
        bookingPayload.setTotal(booking.getFacility().getFacilityType().getRate().toString());

        Map<String, String> data = new HashMap<>();

        // Add firstname
        data.put("bookingId", bookingPayload.getBookingId());
        data.put("customerName",bookingPayload.getCustomerName());
        data.put("bookingDate",bookingPayload.getBookingDate());
        data.put("facilityName",bookingPayload.getFacilityName());
        data.put("bookingStart",bookingPayload.getBookingStart());
        data.put("status",bookingPayload.getStatus());
        data.put("bookingEnd",bookingPayload.getBookingEnd());
        data.put("total",bookingPayload.getTotal());

        return data;
    }

}
