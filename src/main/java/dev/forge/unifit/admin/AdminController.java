package dev.forge.unifit.admin;

import dev.forge.unifit.booking.Booking;
import dev.forge.unifit.booking.BookingFormDTO;
import dev.forge.unifit.booking.BookingService;
import dev.forge.unifit.booking.BookingStatus;
import dev.forge.unifit.event.Event;
import dev.forge.unifit.event.EventService;
import dev.forge.unifit.facility.Facility;
import dev.forge.unifit.facility.FacilityRevenue;
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
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final EventService eventService;


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


    @PostMapping("/facilities/update")
    public String updateFacility(@ModelAttribute("facility") Facility facility, @RequestParam("image") MultipartFile file){


        try {
            facilityService.saveFacility(facility,file);
        } catch (IOException e) {
            throw new RuntimeException("ERROR: Could not save",e);
        }

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
    //UPDATE THE PARAMETERS TO ACCOUNT FOR MULTIPART FILE
    public String addFacility(@ModelAttribute Facility facility, @RequestParam("facilityTypeId") Long facilityTypeId, @RequestParam("image") MultipartFile file){
        FacilityType selected = facilityTypeService.getFacilityTypeById(facilityTypeId);
        facility.setFacilityType(selected);
        try {
            facilityService.addFacility(facility,file);
        } catch (IOException e) {
            throw new RuntimeException("ERROR: Could not save facility");
        }
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
    public String updateBookingPage(@PathVariable("id") Long bookingId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Booking> booking = bookingService.getBooking(bookingId);

        if (booking.isPresent()) {
            model.addAttribute("booking", booking.get());
            return "admin/update-booking";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking ID not found.");
            return "redirect:/admin/booking";
        }


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



    @GetMapping("/events")
    public String getEventsPage(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "admin/admin-event";
    }

    @GetMapping("/events/create")
    public String createEventForm(Model model) {
        Event event = new Event();
        model.addAttribute("event", event);
        List<Facility> facilities = facilityService.getAllFacilities();
        model.addAttribute("facilities", facilities);
        return "admin/admin-createEvent";
    }

    @PostMapping("/events/create")
    public String createEvent(@ModelAttribute("event") Event event,
                              @RequestParam("facilityId") Long facilityId/*,
                              @RequestParam("image") MultipartFile imageFile*/) {
        Facility facility = facilityService.getFacility(facilityId);
        event.setFacility(facility);
        /*        eventService.saveEvent(event, imageFile);  // Now passing the image file to the service*/
        return "redirect:/admin/events";
    }

    @GetMapping("/events/edit/{id}")
    public String editEventForm(@PathVariable("id") Long eventId, Model model) {
        Event event = eventService.getEventById(eventId);
        model.addAttribute("event", event);
        List<Facility> facilities = facilityService.getAllFacilities();
        model.addAttribute("facilities", facilities);
        return "admin/admin-editEvent";
    }

    @PostMapping("/events/update")
    public String updateEvent(@ModelAttribute("event") Event event,
                              @RequestParam("facilityId") Long facilityId/*,
                              @RequestParam("image") MultipartFile imageFile*/) {
        Facility facility = facilityService.getFacility(facilityId);
        event.setFacility(facility);
        /*        eventService.saveEvent(event, imageFile);  // Now passing the image file to the service*/
        return "redirect:/admin/events";
    }

    @GetMapping("/reports")
    public String reportsPage(Model model) {
        List<FacilityRevenue> facilityRevenues = facilityService.getMonthlyRevenue(LocalDate.now());
        model.addAttribute("facilityRevenues", facilityRevenues);

        Map<String, Double> revenueByFacilityType = facilityService.getRevenueByFacilityType(LocalDate.now().getYear());
        model.addAttribute("revenueData", revenueByFacilityType);
        return "admin/admin-report";
    }
}
