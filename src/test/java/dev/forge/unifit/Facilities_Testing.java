import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import dev.forge.unifit.facility.Facility;
import dev.forge.unifit.facility.FacilityRepository;
import dev.forge.unifit.facility.FacilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

class Facilities_Testing {

    @Mock
    private FacilityRepository facilityRepository;

    @InjectMocks
    private FacilityService facilityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllFacilities() {
        Facility facility1 = new Facility();
        Facility facility2 = new Facility();
        when(facilityRepository.findAll()).thenReturn(Arrays.asList(facility1, facility2));

        List<Facility> facilities = facilityService.getAllFacilities();

        assertEquals(2, facilities.size());
        verify(facilityRepository).findAll(); // Simplified verification
    }

    @Test
    void testGetFacility() {
        Long facilityId = 1L;
        Facility facility = new Facility();
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));

        Facility result = facilityService.getFacility(facilityId);

        assertNotNull(result);
        assertEquals(facility, result);
        verify(facilityRepository).findById(facilityId);
    }

    @Test
    void testGetFacility_NotFound() {
        Long facilityId = 1L;
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            facilityService.getFacility(facilityId);
        });

        assertEquals("No value present", exception.getMessage());
        verify(facilityRepository).findById(facilityId); // Ensure repository method was called
    }

    @Test
    void testSaveFacility() throws IOException {
        Long facilityId = 1L;
        Facility existingFacility = new Facility();
        existingFacility.setId(facilityId);

        LocalTime opening = LocalTime.of(8, 0);
        LocalTime closing = LocalTime.of(12, 0);
        existingFacility.setWeekdayOpeningTime(opening); // Set initial times for existing facility

        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(existingFacility));

        Facility updatedFacility = new Facility();
        updatedFacility.setId(facilityId);
        updatedFacility.setName("Updated Facility");
        updatedFacility.setWeekdayOpeningTime(opening);
        updatedFacility.setWeekendClosingTime(closing);

        // Mocking MultipartFile
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("testImage.png");
        when(file.getBytes()).thenReturn(new byte[]{});

        when(facilityRepository.save(existingFacility)).thenReturn(existingFacility);

        Facility result = facilityService.saveFacility(updatedFacility, file); // Updated method call

        assertEquals("Updated Facility", existingFacility.getName());
        assertEquals(opening, existingFacility.getWeekdayOpeningTime());
        assertEquals(closing, existingFacility.getWeekendClosingTime());
        verify(facilityRepository).findById(facilityId);
        verify(facilityRepository).save(existingFacility);
    }

    @Test
    void testSaveFacility_NotFound() throws IOException {
        Long facilityId = 1L;
        Facility updatedFacility = new Facility();
        updatedFacility.setId(facilityId);

        // Mocking MultipartFile
        MultipartFile file = mock(MultipartFile.class);

        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            facilityService.saveFacility(updatedFacility, file); // Updated method call
        });

        assertEquals("No value present", exception.getMessage());
        verify(facilityRepository).findById(facilityId); // Ensure repository method was called
    }

    @Test
    void testAddFacility() throws IOException {
        Facility newFacility = new Facility();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("testImage.png");
        when(file.getBytes()).thenReturn(new byte[]{});

        // Set the image URL expectation
        Facility savedFacility = new Facility();
        savedFacility.setImageUrl("/uploads/testImage.png");
        when(facilityRepository.save(newFacility)).thenReturn(savedFacility);

        Facility result = facilityService.addFacility(newFacility, file);

        assertNotNull(result);
        assertEquals("/uploads/testImage.png", result.getImageUrl());
        verify(facilityRepository).save(newFacility);
    }

    @Test
void testSaveFacility_PartialUpdate() throws IOException {
    Long facilityId = 1L;
    Facility existingFacility = new Facility();
    existingFacility.setId(facilityId);
    existingFacility.setName("Original Facility");

    // Mock the repository to return the existing facility
    when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(existingFacility));

    // Prepare the updated facility with new values
    Facility updatedFacility = new Facility();
    updatedFacility.setId(facilityId);
    updatedFacility.setName("Updated Facility");

    // Set opening and closing times
    LocalTime opening = LocalTime.of(8, 0);
    updatedFacility.setWeekdayOpeningTime(opening);
    updatedFacility.setWeekendClosingTime(LocalTime.of(10, 0));

    // Mock the MultipartFile
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("testImage.png");
    when(file.getBytes()).thenReturn(new byte[]{}); // Mock an empty byte array for the file

    // Make sure to mock the repository save method as well
    when(facilityRepository.save(existingFacility)).thenReturn(existingFacility);

    // Call the service method with updated facility and mocked file
    Facility result = facilityService.saveFacility(updatedFacility, file);

    // Assert that the name has been updated correctly
    assertEquals("Updated Facility", result.getName());
    assertEquals(opening, result.getWeekdayOpeningTime());
    assertEquals(LocalTime.of(10, 0), result.getWeekendClosingTime());

    // Verify interactions with the repository
    verify(facilityRepository, times(1)).findById(facilityId);
    verify(facilityRepository, times(1)).save(existingFacility);
}

}
