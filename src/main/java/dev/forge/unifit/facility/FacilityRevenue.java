package dev.forge.unifit.facility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.Month;

@Getter
@Setter
@AllArgsConstructor

public class FacilityRevenue {
    private Month month;
    private double totalRevenue;
}
