package dev.forge.unifit.user;


import dev.forge.unifit.booking.Booking;
import dev.forge.unifit.booking.BookingRepository;
import dev.forge.unifit.booking.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final BookingService bookingService;

    @GetMapping("/user")
    public ModelAndView userPage(@AuthenticationPrincipal UserDetails authUser){
        ModelAndView page = new ModelAndView("user");
        User user = userRepository.findByEmail(authUser.getUsername());
        List<Booking> bookings = bookingService.getBookingsByUser(user);

        // Filter bookings that are before today
        List<Booking> pastBookings = bookings.stream()
                .filter(booking -> booking.getBookedDate().isBefore(LocalDate.now()))
                .toList();

        //Upcoming Bookings
        List<Booking> upcomingBookings = bookings.stream()
                .filter(booking -> !booking.getBookedDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(Booking::getBookedDate))
                .collect(Collectors.toList());

        page.addObject("user",user);
        page.addObject("bookings",upcomingBookings);
        page.addObject("pastbookings",pastBookings);
        return page;
    }

    @GetMapping("/user/bookings/{id}")
    public String usersBooking(Model model,@AuthenticationPrincipal UserDetails authUser,@PathVariable("id")Long bookingId){
        User user = userRepository.findByEmail(authUser.getUsername());
        Booking booking = bookingService.getBooking(bookingId);

        if(booking.getUser() != user){
            return "invalid";
        }

        model.addAttribute("booking",booking);

        return "user-booking";

    }



}
