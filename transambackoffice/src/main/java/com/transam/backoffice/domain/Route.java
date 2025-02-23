package com.transam.backoffice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Route.
 */
@Table("route")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Route implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("code")
    private String code;

    @NotNull(message = "must not be null")
    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("start")
    private String start;

    @NotNull(message = "must not be null")
    @Column("destination")
    private String destination;

    @NotNull(message = "must not be null")
    @Column("passenger_capacity")
    private Integer passengerCapacity;

    @NotNull(message = "must not be null")
    @Column("parcel_total_weight")
    private BigDecimal parcelTotalWeight;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "route" }, allowSetters = true)
    private Set<RouteSchedule> routeschedules = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "department", "routes" }, allowSetters = true)
    private Set<Employee> employees = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "routes" }, allowSetters = true)
    private Set<Vehicle> vehicles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Route id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Route code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public Route description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart() {
        return this.start;
    }

    public Route start(String start) {
        this.setStart(start);
        return this;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDestination() {
        return this.destination;
    }

    public Route destination(String destination) {
        this.setDestination(destination);
        return this;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getPassengerCapacity() {
        return this.passengerCapacity;
    }

    public Route passengerCapacity(Integer passengerCapacity) {
        this.setPassengerCapacity(passengerCapacity);
        return this;
    }

    public void setPassengerCapacity(Integer passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
    }

    public BigDecimal getParcelTotalWeight() {
        return this.parcelTotalWeight;
    }

    public Route parcelTotalWeight(BigDecimal parcelTotalWeight) {
        this.setParcelTotalWeight(parcelTotalWeight);
        return this;
    }

    public void setParcelTotalWeight(BigDecimal parcelTotalWeight) {
        this.parcelTotalWeight = parcelTotalWeight != null ? parcelTotalWeight.stripTrailingZeros() : null;
    }

    public Set<RouteSchedule> getRouteschedules() {
        return this.routeschedules;
    }

    public void setRouteschedules(Set<RouteSchedule> routeSchedules) {
        if (this.routeschedules != null) {
            this.routeschedules.forEach(i -> i.setRoute(null));
        }
        if (routeSchedules != null) {
            routeSchedules.forEach(i -> i.setRoute(this));
        }
        this.routeschedules = routeSchedules;
    }

    public Route routeschedules(Set<RouteSchedule> routeSchedules) {
        this.setRouteschedules(routeSchedules);
        return this;
    }

    public Route addRouteschedule(RouteSchedule routeSchedule) {
        this.routeschedules.add(routeSchedule);
        routeSchedule.setRoute(this);
        return this;
    }

    public Route removeRouteschedule(RouteSchedule routeSchedule) {
        this.routeschedules.remove(routeSchedule);
        routeSchedule.setRoute(null);
        return this;
    }

    public Set<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    public Route employees(Set<Employee> employees) {
        this.setEmployees(employees);
        return this;
    }

    public Route addEmployee(Employee employee) {
        this.employees.add(employee);
        return this;
    }

    public Route removeEmployee(Employee employee) {
        this.employees.remove(employee);
        return this;
    }

    public Set<Vehicle> getVehicles() {
        return this.vehicles;
    }

    public void setVehicles(Set<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Route vehicles(Set<Vehicle> vehicles) {
        this.setVehicles(vehicles);
        return this;
    }

    public Route addVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
        return this;
    }

    public Route removeVehicle(Vehicle vehicle) {
        this.vehicles.remove(vehicle);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Route)) {
            return false;
        }
        return getId() != null && getId().equals(((Route) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Route{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", start='" + getStart() + "'" +
            ", destination='" + getDestination() + "'" +
            ", passengerCapacity=" + getPassengerCapacity() +
            ", parcelTotalWeight=" + getParcelTotalWeight() +
            "}";
    }
}
