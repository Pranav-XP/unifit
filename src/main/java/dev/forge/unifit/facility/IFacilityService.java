package dev.forge.unifit.facility;

import dev.forge.unifit.facility.facilitytype.FacilityType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IFacilityService {
    List<Facility> getAllFacilities();

    Facility getFacility(Long id);
    Facility addFacility(Facility facility, MultipartFile file) throws IOException;

   Facility saveFacility(Facility updatedFacility, MultipartFile file) throws IOException;
}
