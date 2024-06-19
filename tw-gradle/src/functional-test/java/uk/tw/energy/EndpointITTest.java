package uk.tw.energy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import uk.tw.energy.builders.MeterReadingsBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
public class EndpointITTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static HttpEntity<MeterReadings> toHttpEntity(MeterReadings meterReadings) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(meterReadings, headers);
    }

    @Test
    public void shouldReturnCorrectCostForMeterWithPricePlan() {

    }


    @Test
    public void givenSmartMeterIdWithPricePlan_whenRequestUsageCost_thenReturnCorrectCost() {
        // Setup test data
        String smartMeterId = "smart-meter-1";
        BigDecimal pricePlan = new BigDecimal("0.2");

        MeterReadings meterReadings = new MeterReadings(smartMeterId, Arrays.asList(
                new ElectricityReading(Instant.now().minus(3, ChronoUnit.DAYS), new BigDecimal("10.0")),
                new ElectricityReading(Instant.now().minus(4, ChronoUnit.DAYS), new BigDecimal("20.0"))
        ));

        HttpEntity<MeterReadings> httpEntity = toHttpEntity(meterReadings);
        restTemplate.postForEntity("/readings/store", httpEntity, String.class);

        // Perform GET request
        ResponseEntity<BigDecimal> response = restTemplate.getForEntity("/api/viewUsageCostLastWeek/{smartMeterId}", BigDecimal.class, smartMeterId);

        // Calculate expected cost
        BigDecimal expectedAverageReading = new BigDecimal("15.0"); // (10 + 20) / 2
        BigDecimal hoursInWeek = BigDecimal.valueOf(168); // 24 * 7
        BigDecimal energyConsumed = expectedAverageReading.multiply(hoursInWeek);
        BigDecimal expectedCost = pricePlan.multiply(energyConsumed);

        // Validate response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCost, response.getBody());
    }

    @Test
    public void givenSmartMeterIdWithoutPricePlan_whenRequestUsageCost_thenReturnNotFound() {
        // Setup test data
        String smartMeterId = "54321";

        MeterReadings meterReadings = new MeterReadings(smartMeterId, Arrays.asList(
                new ElectricityReading(Instant.now().minus(3, ChronoUnit.DAYS), new BigDecimal("10.0"))
        ));

        HttpEntity<MeterReadings> httpEntity = toHttpEntity(meterReadings);
        restTemplate.postForEntity("/readings/store", httpEntity, String.class);

        // Perform GET request
        ResponseEntity<String> response = restTemplate.getForEntity("/api/viewUsageCostLastWeek/{smartMeterId}", String.class, smartMeterId);

        // Validate response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
