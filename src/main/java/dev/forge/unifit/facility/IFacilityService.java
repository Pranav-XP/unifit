package dev.forge.unifit.facility;

import dev.forge.unifit.facility.facilitytype.FacilityType;

import java.util.List;

public interface IFacilityService {
    List<Facility> getAllFacilities();

    Facility getFacility(Long id);
    Facility addFacility(Facility facility);

    Facility saveFacility(Facility facility);
}
