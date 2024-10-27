package dev.forge.unifit.event;

import dev.forge.unifit.email.EmailService;
import dev.forge.unifit.facility.Facility;
import dev.forge.unifit.facility.FacilityService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final FacilityService facilityService;
    private final EmailService emailService;

    @GetMapping("")
    public String displayAllEvents(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "admin/admin-event"; // updated to reflect the correct path
    }

    @GetMapping("/create")
    public String showCreateEventForm(Model model) {
        List<Facility> facilities = facilityService.getAllFacilities();
        model.addAttribute("facilities", facilities);
        model.addAttribute("event", new Event());

        // Add minimum date and time for the event
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        model.addAttribute("minDateTime", formattedDate);

        return "admin/admin-createEvent"; // updated to reflect the correct path
    }

    @PostMapping("/create")
    public String createEvent(@ModelAttribute Event event) {
        // Ensure the eventDateTime is parsed correctly
        if (event.getEventDateTime() == null) {
            throw new IllegalArgumentException("Event DateTime is required");
        }

        Event newEvent = eventService.saveEvent(event);
        try {
            emailService.sendEventNotification(newEvent);
        } catch (MessagingException e) {
            throw new RuntimeException("Email not sent",e);
        }
        return "redirect:/events"; // redirect to the events page after saving
    }

    @GetMapping("/edit/{id}")
    public String showEditEventForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        List<Facility> facilities = facilityService.getAllFacilities();
        model.addAttribute("facilities", facilities);
        model.addAttribute("event", event);

        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        model.addAttribute("minDateTime", formattedDate);

        return "admin/admin-editEvent"; // updated to reflect the correct path
    }

    @PostMapping("/edit/{id}")
    public String editEvent(@PathVariable Long id, @ModelAttribute Event event) {
        event.setId(id); // ensure the ID is set for updating
        eventService.updateEvent(id, event); // updated to use the update method in the service
        return "redirect:/events"; // redirect to the events page after updating
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/events"; // redirect to the events page after deleting
    }
}
