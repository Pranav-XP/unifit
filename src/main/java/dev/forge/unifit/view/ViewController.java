package dev.forge.unifit.view;

import dev.forge.unifit.email.EmailService;
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

    @GetMapping()
    public String homePage(Model model){
        List<Facility> facilities = facilityService.getAllFacilities();
        model.addAttribute("facilities",facilities);
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }



}
