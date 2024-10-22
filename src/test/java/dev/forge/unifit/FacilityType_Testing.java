package dev.forge.unifit.facility.facilitytype;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException; // Add this import
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class FacilityType_Testing {

    @Mock
    private FacilityTypeRepository facilityTypeRepository;

    @InjectMocks
    private FacilityTypeService facilityTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllFacilityTypes() {
        FacilityType facilityType1 = new FacilityType(); // Assume constructor and setters are available
        FacilityType facilityType2 = new FacilityType();
        when(facilityTypeRepository.findAll()).thenReturn(Arrays.asList(facilityType1, facilityType2));

        List<FacilityType> facilityTypes = facilityTypeService.getAllFacilityTypes();

        assertEquals(2, facilityTypes.size());
        verify(facilityTypeRepository, times(1)).findAll();
    }

    @Test
    void testGetFacilityTypeById() {
        Long facilityTypeId = 1L;
        FacilityType facilityType = new FacilityType();
        when(facilityTypeRepository.findById(facilityTypeId)).thenReturn(Optional.of(facilityType));

        FacilityType result = facilityTypeService.getFacilityTypeById(facilityTypeId);

        assertNotNull(result);
        assertEquals(facilityType, result);
        verify(facilityTypeRepository, times(1)).findById(facilityTypeId);
    }

    @Test
    void testGetFacilityTypeById_NotFound() {
        Long facilityTypeId = 1L;
        when(facilityTypeRepository.findById(facilityTypeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            facilityTypeService.getFacilityTypeById(facilityTypeId);
        });

        assertEquals("No value present", exception.getMessage());
    }

    @Test
    void testAddFacilityType() {
        FacilityType facilityType = new FacilityType();
        when(facilityTypeRepository.save(facilityType)).thenReturn(facilityType);

        facilityTypeService.addFacilityType(facilityType);

        verify(facilityTypeRepository, times(1)).save(facilityType);
    }

    @Test
    void testSaveFacilityType() {
        Long facilityTypeId = 1L;
        FacilityType existingFacilityType = new FacilityType();
        existingFacilityType.setId(facilityTypeId);
        existingFacilityType.setName("Old Name");
        existingFacilityType.setRate(100);

        when(facilityTypeRepository.findById(facilityTypeId)).thenReturn(Optional.of(existingFacilityType));

        FacilityType updatedFacilityType = new FacilityType();
        updatedFacilityType.setId(facilityTypeId);
        updatedFacilityType.setName("Updated Name");
        updatedFacilityType.setRate(150);

        when(facilityTypeRepository.save(existingFacilityType)).thenReturn(existingFacilityType);

        FacilityType result = facilityTypeService.saveFacilityType(updatedFacilityType);

        assertEquals("Updated Name", existingFacilityType.getName());
        assertEquals(150, existingFacilityType.getRate());
        verify(facilityTypeRepository, times(1)).findById(facilityTypeId);
        verify(facilityTypeRepository, times(1)).save(existingFacilityType);
    }

    @Test
    void testSaveFacilityType_NotFound() {
        Long facilityTypeId = 1L;
        FacilityType updatedFacilityType = new FacilityType();
        updatedFacilityType.setId(facilityTypeId);
        when(facilityTypeRepository.findById(facilityTypeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            facilityTypeService.saveFacilityType(updatedFacilityType);
        });

        assertEquals("No value present", exception.getMessage());
    }
}
