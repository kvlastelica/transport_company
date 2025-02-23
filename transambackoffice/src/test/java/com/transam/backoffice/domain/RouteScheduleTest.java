package com.transam.backoffice.domain;

import static com.transam.backoffice.domain.RouteScheduleTestSamples.*;
import static com.transam.backoffice.domain.RouteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transam.backoffice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RouteScheduleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RouteSchedule.class);
        RouteSchedule routeSchedule1 = getRouteScheduleSample1();
        RouteSchedule routeSchedule2 = new RouteSchedule();
        assertThat(routeSchedule1).isNotEqualTo(routeSchedule2);

        routeSchedule2.setId(routeSchedule1.getId());
        assertThat(routeSchedule1).isEqualTo(routeSchedule2);

        routeSchedule2 = getRouteScheduleSample2();
        assertThat(routeSchedule1).isNotEqualTo(routeSchedule2);
    }

    @Test
    void routeTest() {
        RouteSchedule routeSchedule = getRouteScheduleRandomSampleGenerator();
        Route routeBack = getRouteRandomSampleGenerator();

        routeSchedule.setRoute(routeBack);
        assertThat(routeSchedule.getRoute()).isEqualTo(routeBack);

        routeSchedule.route(null);
        assertThat(routeSchedule.getRoute()).isNull();
    }
}
