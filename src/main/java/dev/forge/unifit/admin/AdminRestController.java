package dev.forge.unifit.admin;

import dev.forge.unifit.facility.FacilityRevenue;
import dev.forge.unifit.facility.FacilityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/revenue")
public class AdminRestController {

    private final FacilityService facilityService;

    public AdminRestController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping("/monthly")
    public List<FacilityRevenue> getMonthlyRevenue() {
        return facilityService.getMonthlyRevenue(LocalDate.now());
    }

    @GetMapping("/by-facility")
    public Map<String, Double> getRevenueByFacilityType() {
        return facilityService.getRevenueByFacilityType(LocalDate.now().getYear());
    }
}
