package uk.tw.energy.controller;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.service.ElectricityConsumerService;
import uk.tw.energy.service.MeterReadingService;

@RestController
@RequestMapping("/api")
public class ElectricityConsumerController {

    private final ElectricityConsumerService electricityConsumerService;
    private final MeterReadingService meterReadingService;

    public ElectricityConsumerController(
            ElectricityConsumerService electricityConsumerService, MeterReadingService meterReadingService) {
        this.electricityConsumerService = electricityConsumerService;
        this.meterReadingService = meterReadingService;
    }

    @GetMapping("/viewUsageCostLastWeek")
    public ResponseEntity<BigDecimal> viewUsageCostLastWeek(@PathVariable String smartMeterId) {
        Optional<BigDecimal> readings = electricityConsumerService.calculateUsageCostLastWeek(smartMeterId);
        return readings.isPresent()
                ? ResponseEntity.ok(readings.get())
                : ResponseEntity.notFound().build();
    }
}
