package com.transam.backoffice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A RouteSchedule.
 */
@Table("route_schedule")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RouteSchedule implements Serializable {

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
    @Column("departure")
    private Instant departure;

    @NotNull(message = "must not be null")
    @Column("arrival")
    private Instant arrival;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "routeschedules", "employees", "vehicles" }, allowSetters = true)
    private Route route;

    @Column("route_id")
    private Long routeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RouteSchedule id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public RouteSchedule code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public RouteSchedule description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDeparture() {
        return this.departure;
    }

    public RouteSchedule departure(Instant departure) {
        this.setDeparture(departure);
        return this;
    }

    public void setDeparture(Instant departure) {
        this.departure = departure;
    }

    public Instant getArrival() {
        return this.arrival;
    }

    public RouteSchedule arrival(Instant arrival) {
        this.setArrival(arrival);
        return this;
    }

    public void setArrival(Instant arrival) {
        this.arrival = arrival;
    }

    public Route getRoute() {
        return this.route;
    }

    public void setRoute(Route route) {
        this.route = route;
        this.routeId = route != null ? route.getId() : null;
    }

    public RouteSchedule route(Route route) {
        this.setRoute(route);
        return this;
    }

    public Long getRouteId() {
        return this.routeId;
    }

    public void setRouteId(Long route) {
        this.routeId = route;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RouteSchedule)) {
            return false;
        }
        return getId() != null && getId().equals(((RouteSchedule) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RouteSchedule{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", departure='" + getDeparture() + "'" +
            ", arrival='" + getArrival() + "'" +
            "}";
    }
}
