package dev.forge.unifit.facility;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility,Long> {

    @Query("SELECT f.weekdayOpeningTime FROM Facility f WHERE f.id = :facilityId")
    LocalTime findWeekdayOpeningTimeByFacilityId(@Param("facilityId") Long facilityId);

    @Query("SELECT f.weekdayClosingTime FROM Facility f WHERE f.id = :facilityId")
    LocalTime findWeekdayClosingTimeByFacilityId(@Param("facilityId") Long facilityId);

    @Query("SELECT f.weekendOpeningTime FROM Facility f WHERE f.id = :facilityId")
    LocalTime findWeekendOpeningTimeByFacilityId(@Param("facilityId") Long facilityId);

    @Query("SELECT f.weekendClosingTime FROM Facility f WHERE f.id = :facilityId")
    LocalTime findWeekendClosingTimeByFacilityId(@Param("facilityId") Long facilityId);

   /* @Query("SELECT f.weekdayOpeningTime, f.weekdayClosingTime FROM Facility f WHERE f.id = :facilityId")
    List<LocalTime> findWeekdayOpeningAndClosingTimeByFacilityId(@Param("facilityId") Long facilityId);

    @Query("SELECT f.weekendOpeningTime, f.weekendClosingTime FROM Facility f WHERE f.id = :facilityId")
    List<LocalTime> findWeekendOpeningAndClosingTimeByFacilityId(@Param("facilityId") Long facilityId);*/
}
