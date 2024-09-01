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

        System.out.println("WEEKDAY TIMES: "+ updatedFacility.getWeekdayOpeningTime()+" "+ facility.getWeekdayClosingTime());
        facility.setName(updatedFacility.getName());
        facility.setStatus(updatedFacility.getStatus());
        facility.setImageUrl(updatedFacility.getImageUrl());
        facility.setDescription(updatedFacility.getDescription());
        facility.setWeekdayOpeningTime(updatedFacility.getWeekdayOpeningTime());
        facility.setWeekdayClosingTime(updatedFacility.getWeekdayClosingTime());
        facility.setWeekendOpeningTime(updatedFacility.getWeekendOpeningTime());
        facility.setWeekendClosingTime(updatedFacility.getWeekendClosingTime());

        return facilityRepository.save(facility);

    }

}
