package dev.forge.unifit.facility.facilitytype;

import java.util.List;

public interface IFacilityTypeService {
    List<FacilityType> getAllFacilityTypes();

    FacilityType saveFacilityType(FacilityType facilityType);

    FacilityType getFacilityTypeById(Long id);
    void addFacilityType(FacilityType facilityType);

}
