package dev.forge.unifit.facility;


import dev.forge.unifit.booking.Booking;
import dev.forge.unifit.booking.BookingRepository;
import dev.forge.unifit.booking.BookingStatus;
import dev.forge.unifit.facility.facilitytype.FacilityType;
import dev.forge.unifit.facility.facilitytype.FacilityTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityService implements IFacilityService {
    private final FacilityRepository facilityRepository;
    private final BookingRepository bookingRepository;
    private final FacilityTypeRepository facilityTypeRepository;
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
    public Facility saveFacility(Facility updatedFacility, MultipartFile file) throws IOException{
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
        facility.setDescription(updatedFacility.getDescription());

        //ADD IMAGE LOGIC
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
        Files.write(fileNameAndPath, file.getBytes());

        // Prepare image info and add to the list
        String imagePath = "/uploads/" + file.getOriginalFilename();
        facility.setImageUrl(imagePath);

        return facilityRepository.save(facility);
    }

    public List<FacilityRevenue> getMonthlyRevenue(LocalDate year) {
        List<FacilityRevenue> monthlyRevenue = new ArrayList<>();

        // Iterate through each month of the specified year
        for (Month month : Month.values()) {
            LocalDate startDate = LocalDate.of(year.getYear(), month, 1);
            LocalDate endDate = startDate.withMonth(month.getValue()).plusMonths(1).minusDays(1);

            // Fetch bookings for the month
            List<Booking> bookings = bookingRepository.findByBookedDateBetween(startDate, endDate);

            // Calculate total revenue for this month
            double totalRevenue = bookings.stream()
                    .mapToDouble(booking -> {
                        // Calculate hours booked
                        long hours = booking.getEnd().getHour() - booking.getStart().getHour();
                        // Get facility type rate
                        double facilityTypeRate = booking.getFacility().getFacilityType().getRate();
                        return hours * facilityTypeRate;
                    })
                    .sum();

            // Create a FacilityRevenue object for this month
            monthlyRevenue.add(new FacilityRevenue(month, totalRevenue));
        }

        return monthlyRevenue;
    }


    public Map<String, Double> getRevenueByFacilityType(int year) {
        List<FacilityType> facilityTypes = facilityTypeRepository.findAll();
        Map<String, Double> revenueMap = new HashMap<>();

        // Initialize revenue map with zero
        for (FacilityType facilityType : facilityTypes) {
            revenueMap.put(facilityType.getName(), 0.0);
        }

        // Fetch bookings for the specified year
        List<Booking> bookings = bookingRepository.findBookingsByYear(year);

        // Calculate revenue based on facility type
        for (Booking booking : bookings) {
            if (booking.getStatus() != BookingStatus.DELETED) {
                // Assuming facilityTypeRate is a property of FacilityType
                Double facilityTypeRate = Double.valueOf(booking.getFacility().getFacilityType().getRate()); // Replace with actual property

                // Calculate the hours booked
                double hoursBooked = booking.getEnd().getHour() - booking.getStart().getHour();
                double revenue = hoursBooked * facilityTypeRate;

                // Accumulate the revenue for the facility type
                String facilityTypeName = booking.getFacility().getFacilityType().getName();
                revenueMap.put(facilityTypeName, revenueMap.get(facilityTypeName) + revenue);
            }
        }
        return revenueMap;
    }
}