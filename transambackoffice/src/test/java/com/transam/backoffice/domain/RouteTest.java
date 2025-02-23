package com.transam.backoffice.domain;

import static com.transam.backoffice.domain.EmployeeTestSamples.*;
import static com.transam.backoffice.domain.RouteScheduleTestSamples.*;
import static com.transam.backoffice.domain.RouteTestSamples.*;
import static com.transam.backoffice.domain.VehicleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transam.backoffice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RouteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Route.class);
        Route route1 = getRouteSample1();
        Route route2 = new Route();
        assertThat(route1).isNotEqualTo(route2);

        route2.setId(route1.getId());
        assertThat(route1).isEqualTo(route2);

        route2 = getRouteSample2();
        assertThat(route1).isNotEqualTo(route2);
    }

    @Test
    void routescheduleTest() {
        Route route = getRouteRandomSampleGenerator();
        RouteSchedule routeScheduleBack = getRouteScheduleRandomSampleGenerator();

        route.addRouteschedule(routeScheduleBack);
        assertThat(route.getRouteschedules()).containsOnly(routeScheduleBack);
        assertThat(routeScheduleBack.getRoute()).isEqualTo(route);

        route.removeRouteschedule(routeScheduleBack);
        assertThat(route.getRouteschedules()).doesNotContain(routeScheduleBack);
        assertThat(routeScheduleBack.getRoute()).isNull();

        route.routeschedules(new HashSet<>(Set.of(routeScheduleBack)));
        assertThat(route.getRouteschedules()).containsOnly(routeScheduleBack);
        assertThat(routeScheduleBack.getRoute()).isEqualTo(route);

        route.setRouteschedules(new HashSet<>());
        assertThat(route.getRouteschedules()).doesNotContain(routeScheduleBack);
        assertThat(routeScheduleBack.getRoute()).isNull();
    }

    @Test
    void employeeTest() {
        Route route = getRouteRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        route.addEmployee(employeeBack);
        assertThat(route.getEmployees()).containsOnly(employeeBack);

        route.removeEmployee(employeeBack);
        assertThat(route.getEmployees()).doesNotContain(employeeBack);

        route.employees(new HashSet<>(Set.of(employeeBack)));
        assertThat(route.getEmployees()).containsOnly(employeeBack);

        route.setEmployees(new HashSet<>());
        assertThat(route.getEmployees()).doesNotContain(employeeBack);
    }

    @Test
    void vehicleTest() {
        Route route = getRouteRandomSampleGenerator();
        Vehicle vehicleBack = getVehicleRandomSampleGenerator();

        route.addVehicle(vehicleBack);
        assertThat(route.getVehicles()).containsOnly(vehicleBack);

        route.removeVehicle(vehicleBack);
        assertThat(route.getVehicles()).doesNotContain(vehicleBack);

        route.vehicles(new HashSet<>(Set.of(vehicleBack)));
        assertThat(route.getVehicles()).containsOnly(vehicleBack);

        route.setVehicles(new HashSet<>());
        assertThat(route.getVehicles()).doesNotContain(vehicleBack);
    }
}
