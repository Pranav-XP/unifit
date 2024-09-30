package dev.forge.unifit.facility;


import dev.forge.unifit.facility.facilitytype.FacilityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacilityService implements IFacilityService {
    private final FacilityRepository facilityRepository;
    @Override
    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    @Override
    public Facility getFacility(Long id) {
            Optional<Facility> facility = facilityRepository.findById(id);
            return facility.get();
    }

    @Override
    public Facility addFacility(Facility facility) {
        facility.setStatus(FacilityStatus.AVAILABLE);
        return facilityRepository.save(facility);
    }

    @Override
    public Facility saveFacility(Facility updatedFacility) {
        Facility facility = facilityRepository.findById(updatedFacility.getId()).get();

        // Check and update weekday opening time
        if (updatedFacility.getWeekdayOpeningTime() != null) {
            facility.setWeekdayOpeningTime(updatedFacility.getWeekdayOpeningTime());
        }

        // Check and update weekday closing time
        if (updatedFacility.getWeekdayClosingTime() != null) {
            facility.setWeekdayClosingTime(updatedFacility.getWeekdayClosingTime());
        }

        // Check and update weekend opening time
        if (updatedFacility.getWeekendOpeningTime() != null) {
            facility.setWeekendOpeningTime(updatedFacility.getWeekendOpeningTime());
        }

        // Check and update weekend closing time
        if (updatedFacility.getWeekendClosingTime() != null) {
            facility.setWeekendClosingTime(updatedFacility.getWeekendClosingTime());
        }

        System.out.println("WEEKDAY TIMES: "+ updatedFacility.getWeekdayOpeningTime()+" "+ facility.getWeekdayClosingTime());
        facility.setName(updatedFacility.getName());
        facility.setStatus(updatedFacility.getStatus());
        facility.setImageUrl(updatedFacility.getImageUrl());
        facility.setDescription(updatedFacility.getDescription());

        return facilityRepository.save(facility);

    }

}
