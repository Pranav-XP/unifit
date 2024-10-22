package dev.forge.unifit.facility;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";


    @Override
    public Facility addFacility(Facility facility,MultipartFile file) throws IOException {
        //ADD IMAGE LOGIC
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
        Files.write(fileNameAndPath, file.getBytes());

        // Prepare image info and add to the list
        String imagePath = "/uploads/" + file.getOriginalFilename();
        facility.setImageUrl(imagePath);
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
