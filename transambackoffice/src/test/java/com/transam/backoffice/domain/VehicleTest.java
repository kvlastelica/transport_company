package com.transam.backoffice.domain;

import static com.transam.backoffice.domain.RouteTestSamples.*;
import static com.transam.backoffice.domain.VehicleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transam.backoffice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class VehicleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vehicle.class);
        Vehicle vehicle1 = getVehicleSample1();
        Vehicle vehicle2 = new Vehicle();
        assertThat(vehicle1).isNotEqualTo(vehicle2);

        vehicle2.setId(vehicle1.getId());
        assertThat(vehicle1).isEqualTo(vehicle2);

        vehicle2 = getVehicleSample2();
        assertThat(vehicle1).isNotEqualTo(vehicle2);
    }

    @Test
    void routeTest() {
        Vehicle vehicle = getVehicleRandomSampleGenerator();
        Route routeBack = getRouteRandomSampleGenerator();

        vehicle.addRoute(routeBack);
        assertThat(vehicle.getRoutes()).containsOnly(routeBack);
        assertThat(routeBack.getVehicles()).containsOnly(vehicle);

        vehicle.removeRoute(routeBack);
        assertThat(vehicle.getRoutes()).doesNotContain(routeBack);
        assertThat(routeBack.getVehicles()).doesNotContain(vehicle);

        vehicle.routes(new HashSet<>(Set.of(routeBack)));
        assertThat(vehicle.getRoutes()).containsOnly(routeBack);
        assertThat(routeBack.getVehicles()).containsOnly(vehicle);

        vehicle.setRoutes(new HashSet<>());
        assertThat(vehicle.getRoutes()).doesNotContain(routeBack);
        assertThat(routeBack.getVehicles()).doesNotContain(vehicle);
    }
}
