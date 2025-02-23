package com.transam.backoffice.domain;

import static com.transam.backoffice.domain.DepartmentTestSamples.*;
import static com.transam.backoffice.domain.EmployeeTestSamples.*;
import static com.transam.backoffice.domain.RouteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transam.backoffice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Employee.class);
        Employee employee1 = getEmployeeSample1();
        Employee employee2 = new Employee();
        assertThat(employee1).isNotEqualTo(employee2);

        employee2.setId(employee1.getId());
        assertThat(employee1).isEqualTo(employee2);

        employee2 = getEmployeeSample2();
        assertThat(employee1).isNotEqualTo(employee2);
    }

    @Test
    void departmentTest() {
        Employee employee = getEmployeeRandomSampleGenerator();
        Department departmentBack = getDepartmentRandomSampleGenerator();

        employee.setDepartment(departmentBack);
        assertThat(employee.getDepartment()).isEqualTo(departmentBack);

        employee.department(null);
        assertThat(employee.getDepartment()).isNull();
    }

    @Test
    void routeTest() {
        Employee employee = getEmployeeRandomSampleGenerator();
        Route routeBack = getRouteRandomSampleGenerator();

        employee.addRoute(routeBack);
        assertThat(employee.getRoutes()).containsOnly(routeBack);
        assertThat(routeBack.getEmployees()).containsOnly(employee);

        employee.removeRoute(routeBack);
        assertThat(employee.getRoutes()).doesNotContain(routeBack);
        assertThat(routeBack.getEmployees()).doesNotContain(employee);

        employee.routes(new HashSet<>(Set.of(routeBack)));
        assertThat(employee.getRoutes()).containsOnly(routeBack);
        assertThat(routeBack.getEmployees()).containsOnly(employee);

        employee.setRoutes(new HashSet<>());
        assertThat(employee.getRoutes()).doesNotContain(routeBack);
        assertThat(routeBack.getEmployees()).doesNotContain(employee);
    }
}
