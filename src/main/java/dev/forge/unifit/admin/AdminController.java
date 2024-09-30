package dev.forge.unifit.admin;

import dev.forge.unifit.booking.Booking;
import dev.forge.unifit.booking.BookingFormDTO;
import dev.forge.unifit.booking.BookingService;
import dev.forge.unifit.booking.BookingStatus;
import dev.forge.unifit.facility.Facility;
import dev.forge.unifit.facility.FacilityService;
import dev.forge.unifit.facility.facilitytype.FacilityType;
import dev.forge.unifit.facility.facilitytype.FacilityTypeService;

import dev.forge.unifit.user.User;
import dev.forge.unifit.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    private final FacilityService facilityService;
    private final FacilityTypeService facilityTypeService;
    private final BookingService bookingService;
    private final UserService userService;


    @GetMapping
    public String adminHomePage(Model model){
        List<Booking> bookings = bookingService.getBookings();
        Map<String,Long> chartData = countBookingsByFacility(bookings);
        // Filter out past bookings
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);

        List<Booking> sortedBookings  = bookings.stream()
                .filter(booking -> !booking.getBookedDate().isBefore(today))
                .filter(booking -> booking.getBookedDate().isBefore(nextWeek))
                .sorted(Comparator.comparing(Booking::getBookedDate))
                .collect(Collectors.toList());

        model.addAttribute("bookings",sortedBookings);
        model.addAttribute("chartData",chartData);
        return "admin/admin";
    }


    @GetMapping("/facilities")
    public String adminFacilitiesPage(Model model){
        List<FacilityType> facilityTypes = facilityTypeService.getAllFacilityTypes();
        List<Facility> facilities = facilityService.getAllFacilities();
        model.addAttribute("facilities",facilities);
        model.addAttribute("facilityTypes",facilityTypes);
        return "admin/admin-facilities";
    }

    @GetMapping("facilities/type/{id}")
    public String adminFacilitiesTypePage(Model model,@PathVariable("id") Long  facilityTypeId){
        FacilityType facilityType = facilityTypeService.getFacilityTypeById(facilityTypeId);
        model.addAttribute("facilityType",facilityType);

        return "admin/update-type";
    }

    @PostMapping("facilities/type/update")
    public String updateFacilityType(@ModelAttribute("facilityType") FacilityType facilityType){
        facilityTypeService.saveFacilityType(facilityType);
        return "redirect:/admin/facilities";
    }

    @PostMapping("/facilities/update")
    public String updateFacility(@ModelAttribute("facility") Facility facility){


        facilityService.saveFacility(facility);

        return "redirect:/admin/facilities";
    }

    @GetMapping("/facilities/addtype")
    public String addTypeForm(Model model){
        FacilityType newFacilityType = new FacilityType();
        model.addAttribute("facilityType",newFacilityType);
        return "admin/add-facility-type";
    }

    @PostMapping("/facilities/addtype")
    public String addFacilityType(@ModelAttribute FacilityType newFacilityType){
        facilityTypeService.addFacilityType(newFacilityType);
        return "redirect:/admin/facilities";
    }

    @GetMapping("/facilities/add")
    public String addFacilityForm(Model model){
        Facility newFacility = new Facility();
        List<FacilityType> facilityType = facilityTypeService.getAllFacilityTypes();
        model.addAttribute("facility",newFacility);
        model.addAttribute("facilityTypes",facilityType);

        return "admin/add-facility";
    }

    @PostMapping("/facilities/add")
    public String addFacility(@ModelAttribute Facility facility, @RequestParam("facilityTypeId") Long facilityTypeId){
        FacilityType selected = facilityTypeService.getFacilityTypeById(facilityTypeId);
        facility.setFacilityType(selected);
        facilityService.addFacility(facility);
        return "redirect:/admin/facilities";
    }

    @GetMapping("/users")
    public String adminUsersPage(){
        return "admin/admin-users";
    }

    @GetMapping("/booking")
    public String allBookingsPage(Model model){
        return paginatedBookings(1,model);
    }

    @GetMapping("/booking/page/{pageNo}")
    public String paginatedBookings(@PathVariable("pageNo") int pageNo,Model model){
        int pageSize = 10;

        Page<Booking> page = bookingService.getPaginated(pageNo,pageSize);
        List<Booking> bookings = page.getContent();

        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("bookings",bookings);

        return "admin/admin-bookings";

    }

    @GetMapping("/booking/{id}")
    public String updateBookingPage(@PathVariable("id") Long bookingId, Model model) {
        Booking userBooking = bookingService.getBooking(bookingId);
        model.addAttribute("booking",userBooking);

        return "admin/update-booking";
    }

    @GetMapping("/facilities/{id}")
    public String updateFacilityPage(@PathVariable("id") Long facilityId, Model model){
        Facility facility = facilityService.getFacility(facilityId);
        model.addAttribute("facility",facility);

        return "admin/update-facility";
    }



    @PostMapping("/booking/update")
    public String updateBookingStatus(@ModelAttribute("booking") Booking booking) {
        bookingService.saveBooking(booking);

        return "redirect:/admin/booking/"+booking.getId();
    }

    @GetMapping("/maintenance")
    public String maintenancePage(Model model){
        List<Facility> facilities = facilityService.getAllFacilities();
        List<Booking> schedules = bookingService.getBookingByStatus(BookingStatus.MAINTENANCE);

        model.addAttribute("schedules",schedules);
        model.addAttribute("facilities",facilities);
        return "admin/admin-maintenance";
    }

    @GetMapping("/maintenance/{id}")
    public String maintenanceForm(Model model,@PathVariable("id") Long id,@AuthenticationPrincipal UserDetails authUser){
        Facility facility = facilityService.getFacility(id);
        User user= userService.findByEmail(authUser.getUsername());
        BookingFormDTO bookingForm = new BookingFormDTO();
        bookingForm.setUserId(user.getId());
        bookingForm.setFacilityId(facility.getId());
        model.addAttribute("schedule", bookingForm);
        model.addAttribute("user",user);
        model.addAttribute("facility",facility);
        return "admin/add-maintenance";
    }

    @PostMapping("/maintenance/add")
    public String addMaintenance(@ModelAttribute("schedule") BookingFormDTO form){

        bookingService.setMaintenance(form);
        return "redirect:/admin/maintenance";
    }

    private Map<String,Long> countBookingsByFacility(List<Booking> bookings) {
        return bookings.stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getFacility().getName(),
                        Collectors.counting()
                ));
    }
}
