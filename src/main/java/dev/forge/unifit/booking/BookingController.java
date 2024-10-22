package dev.forge.unifit.booking;

import dev.forge.unifit.facility.Facility;
import dev.forge.unifit.facility.FacilityService;
import dev.forge.unifit.facility.FacilityStatus;
import dev.forge.unifit.notification.Notification;
import dev.forge.unifit.notification.NotificationService;
import dev.forge.unifit.notification.NotificationStatus;
import dev.forge.unifit.transaction.Transaction;
import dev.forge.unifit.user.User;
import dev.forge.unifit.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {
    private final FacilityService facilityService;
    private final UserService userService;
    private final BookingService bookingService;
    private final NotificationService notificationService;

    @GetMapping
    public String bookingSummaryPage(Model model){
        List<Facility> facilities = facilityService.getAllFacilities();
        model.addAttribute("facilities",facilities);
        return "booking";
    }

    @GetMapping("/{id}")
    public String bookingForm(Model model, @AuthenticationPrincipal UserDetails authUser, @PathVariable("id") Long id){
        User user= userService.findByEmail(authUser.getUsername());
        Facility facility = facilityService.getFacility(id);

        if(facility.getStatus() != FacilityStatus.AVAILABLE){
            return "redirect:/booking?error";
        }
        BookingFormDTO bookingForm = new BookingFormDTO();
        bookingForm.setUserId(user.getId());
        bookingForm.setFacilityId(facility.getId());
        model.addAttribute("booking", bookingForm);
        model.addAttribute("user",user);
        model.addAttribute("facility",facility);
        return "add-booking";
    }

    @PostMapping("/add")
    public String createBooking(@ModelAttribute("booking") BookingFormDTO form){
        System.out.println(form.getBookedDate());
        System.out.println(form.getUserId());
        System.out.println(form.getFacilityId());
        System.out.println(form.getStart());
        System.out.println(form.getEnd());

        Booking confirmedBooking = bookingService.createBooking(form);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(confirmedBooking);
        Notification notification = new Notification();
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setBookings(bookings);

        notificationService.saveNotification(notification);
        return "redirect:/user";


    }

    private boolean isDateValid(LocalDate selectedDate) {
        LocalDate currentDate = LocalDate.now();
        // Check if the selected date is not yesterday or earlier
        return !selectedDate.isBefore(currentDate);
    }
}
