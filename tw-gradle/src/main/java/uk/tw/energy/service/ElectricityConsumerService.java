package uk.tw.energy.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;

@Service
public class ElectricityConsumerService {

    private final AccountService accountService;
    private final MeterReadingService meterReadingService;
    private final PricePlanService pricePlanService;

    public ElectricityConsumerService(
            AccountService accountService, MeterReadingService meterReadingService, PricePlanService pricePlanService) {
        this.accountService = accountService;
        this.meterReadingService = meterReadingService;
        this.pricePlanService = pricePlanService;
    }

    public Optional<BigDecimal> calculateUsageCostLastWeek(String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(smartMeterId);
        String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        if (electricityReadings.isEmpty() || pricePlanId == null) {
            return Optional.empty();
        }

        Instant now = Instant.now(); // Current moment in UTC.
        Instant oneWeekAgo = now.minus(Duration.ofDays(7));

        List<ElectricityReading> electricityReadingList = electricityReadings.get().stream()
                .filter(electricityReading -> electricityReading.time().isAfter(oneWeekAgo))
                .toList();

        if (electricityReadingList.isEmpty()) {
            return Optional.ofNullable(BigDecimal.ZERO);
        }

        BigDecimal totalReading = electricityReadingList.stream()
                .map(ElectricityReading::reading)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //  Average reading in KW = (er1.reading + er2.reading + ..... erN.Reading)/N
        BigDecimal averageReading =
                totalReading.divide(BigDecimal.valueOf(electricityReadingList.size()), RoundingMode.HALF_UP);
        //  Usage time in hours = Duration(D) in hours
        BigDecimal hoursInWeek = BigDecimal.valueOf(ChronoUnit.HOURS.between(oneWeekAgo, now));
        //  Energy consumed in kWh = average reading x usage time
        BigDecimal energyConsumed = averageReading.multiply(hoursInWeek);

        Map<String, BigDecimal> tariffUnitPricesPlan = pricePlanService
                .getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId)
                .orElseThrow(() -> new IllegalArgumentException("No price plan attached to the smart meter ID"));

        return Optional.ofNullable(tariffUnitPricesPlan.get(pricePlanId).multiply(energyConsumed));
    }
}
