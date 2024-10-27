package dev.forge.unifit.booking;

import dev.forge.unifit.facility.Facility;
import dev.forge.unifit.facility.FacilityService;
import dev.forge.unifit.facility.FacilityStatus;
import dev.forge.unifit.transaction.Transaction;
import dev.forge.unifit.user.User;
import dev.forge.unifit.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
@SessionAttributes("bookingList")
public class BookingController {
    private final FacilityService facilityService;
    private final UserService userService;
    private final BookingService bookingService;

    @ModelAttribute("bookingList")
    public List<BookingFormDTO> createBookingListModel(){
        return new ArrayList<>();
    }

    /*@GetMapping
    public String bookingSummaryPage(Model model){
        List<Facility> facilities = facilityService.getAllFacilities();
        model.addAttribute("facilities",facilities);
        return "booking";
    }*/

    @GetMapping("")
    public String bookingForm(Model model, @AuthenticationPrincipal UserDetails authUser){
        User user= userService.findByEmail(authUser.getUsername());
        List<Facility> facilities = facilityService.getAllFacilities();

        BookingFormDTO bookingForm = new BookingFormDTO();
        bookingForm.setUserId(user.getId());
        model.addAttribute("booking", bookingForm);
        model.addAttribute("user",user);
        model.addAttribute("facilities",facilities);
        return "add-booking";
    }

    @PostMapping("/add")
    public String createBooking(@ModelAttribute("booking") BookingFormDTO form,
                                @ModelAttribute("bookingList") List<BookingFormDTO> bookingList,
                                Model model,
                                RedirectAttributes redirectAttributes){

        System.out.println(form.getBookedDate());
        System.out.println(form.getUserId());
        System.out.println(form.getFacilityId());
        System.out.println(form.getStart());
        System.out.println(form.getEnd());

        // Check for null values
        if (form.getBookedDate() == null || form.getUserId() == null ||
                form.getFacilityId() == null || form.getStart() == null ||
                form.getEnd() == null) {

            // Add an error message to the redirect attributes
            redirectAttributes.addFlashAttribute("errorMessage", "All fields must be filled out.");

            // Redirect to the form page (or another appropriate page)
            return "redirect:/booking"; // Adjust the URL as needed
        }

        //Check that slot is in the future
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        // Combine booked date with start and end time
        LocalDate bookedDate = form.getBookedDate();
        LocalTime startTime = form.getStart();
        LocalTime endTime = form.getEnd();

        // Create LocalDateTime objects for start and end
        LocalDateTime startDateTime = bookedDate.atTime(startTime);
        LocalDateTime endDateTime = bookedDate.atTime(endTime);

        // Check if booked date and time are in the future
        if (startDateTime.isBefore(now) || endDateTime.isBefore(now)) {
            redirectAttributes.addFlashAttribute("errorMessage", "The booked date and time must be in the future.");
            return "redirect:/booking";
        }

        // Retrieve the facility object
        Facility facility = facilityService.getFacility(form.getFacilityId());
        form.setFacilityName(facility.getName());

        // Check for existing bookings for the same facility, booked date, start time, and end time
        boolean isBookingConflict = bookingList.stream()
                .anyMatch(existingBooking ->
                        existingBooking.getFacilityId().equals(form.getFacilityId()) && // Same facility
                                existingBooking.getBookedDate().equals(form.getBookedDate()) && // Same date
                                existingBooking.getStart().equals(form.getStart()) && // Same start time
                                existingBooking.getEnd().equals(form.getEnd()) // Same end time
                );

        if (isBookingConflict) {
            redirectAttributes.addFlashAttribute("errorMessage", "There is a timeslot conflict. Please try again");
            return "redirect:/booking";
        } else {
            // No conflict, so add the booking to the list
            bookingList.add(form);
            return "redirect:/booking";
        }
    }

    @PostMapping("/confirm")
    public String confirmBookings(@ModelAttribute("bookingList") List<BookingFormDTO> bookingList,
                                  SessionStatus sessionStatus,
                                  RedirectAttributes redirectAttributes){

        try{
            bookingService.createBooking(bookingList);
        }catch (IllegalStateException e){
            redirectAttributes.addFlashAttribute("errorMessage", "Could not create booking. Please try again "+e.getMessage());
            return "redirect:/booking";
        }

        sessionStatus.setComplete();
        return "redirect:/user";
    }

    private boolean isDateValid(LocalDate selectedDate) {
        LocalDate currentDate = LocalDate.now();
        // Check if the selected date is not yesterday or earlier
        return !selectedDate.isBefore(currentDate);
    }
}
