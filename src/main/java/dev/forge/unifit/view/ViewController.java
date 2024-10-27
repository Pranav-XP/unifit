package dev.forge.unifit.view;

import dev.forge.unifit.email.EmailService;
import dev.forge.unifit.event.Event;
import dev.forge.unifit.event.EventService;
import dev.forge.unifit.facility.Facility;
import dev.forge.unifit.facility.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;


@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ViewController {
    private final EmailService email;
    private final FacilityService facilityService;
    private final EventService eventService;

    @GetMapping()
    public String homePage(Model model){
        List<Facility> facilities = facilityService.getAllFacilities();
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("facilities",facilities);
        model.addAttribute("events", events);
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }



}
