package com.transam.backoffice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.transam.backoffice.domain.enumeration.VehicleType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Vehicle.
 */
@Table("vehicle")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Vehicle implements Serializable {

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
    @Column("format")
    private VehicleType format;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "routeschedules", "employees", "vehicles" }, allowSetters = true)
    private Set<Route> routes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Vehicle id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Vehicle code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public Vehicle description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public VehicleType getFormat() {
        return this.format;
    }

    public Vehicle format(VehicleType format) {
        this.setFormat(format);
        return this;
    }

    public void setFormat(VehicleType format) {
        this.format = format;
    }

    public Set<Route> getRoutes() {
        return this.routes;
    }

    public void setRoutes(Set<Route> routes) {
        if (this.routes != null) {
            this.routes.forEach(i -> i.removeVehicle(this));
        }
        if (routes != null) {
            routes.forEach(i -> i.addVehicle(this));
        }
        this.routes = routes;
    }

    public Vehicle routes(Set<Route> routes) {
        this.setRoutes(routes);
        return this;
    }

    public Vehicle addRoute(Route route) {
        this.routes.add(route);
        route.getVehicles().add(this);
        return this;
    }

    public Vehicle removeRoute(Route route) {
        this.routes.remove(route);
        route.getVehicles().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vehicle)) {
            return false;
        }
        return getId() != null && getId().equals(((Vehicle) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Vehicle{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", format='" + getFormat() + "'" +
            "}";
    }
}
