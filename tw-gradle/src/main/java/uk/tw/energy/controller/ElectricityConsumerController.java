package uk.tw.energy.controller;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.ElectricityConsumerService;

@RestController
@RequestMapping("/api")
public class ElectricityConsumerController {

    private final AccountService accountService;
    private final ElectricityConsumerService electricityConsumerService;

    public ElectricityConsumerController(
            AccountService accountService, ElectricityConsumerService electricityConsumerService) {
        this.accountService = accountService;
        this.electricityConsumerService = electricityConsumerService;
    }

    @GetMapping("/viewUsageCostLastWeek/{smartMeterId}")
    public ResponseEntity<BigDecimal> viewUsageCostLastWeek(@PathVariable String smartMeterId) {
        Optional<BigDecimal> readings = electricityConsumerService.calculateUsageCostLastWeek(smartMeterId);
        return readings.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
