package com.transam.backoffice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Employee.
 */
@Table("employee")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("employe_code")
    private String employeCode;

    @NotNull(message = "must not be null")
    @Column("first_name")
    private String firstName;

    @NotNull(message = "must not be null")
    @Column("last_name")
    private String lastName;

    @Column("job_title")
    private String jobTitle;

    @org.springframework.data.annotation.Transient
    private User user;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "employees" }, allowSetters = true)
    private Department department;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "routeschedules", "employees", "vehicles" }, allowSetters = true)
    private Set<Route> routes = new HashSet<>();

    @Column("user_id")
    private Long userId;

    @Column("department_id")
    private Long departmentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Employee id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeCode() {
        return this.employeCode;
    }

    public Employee employeCode(String employeCode) {
        this.setEmployeCode(employeCode);
        return this;
    }

    public void setEmployeCode(String employeCode) {
        this.employeCode = employeCode;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Employee firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Employee lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getJobTitle() {
        return this.jobTitle;
    }

    public Employee jobTitle(String jobTitle) {
        this.setJobTitle(jobTitle);
        return this;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Employee user(User user) {
        this.setUser(user);
        return this;
    }

    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) {
        this.department = department;
        this.departmentId = department != null ? department.getId() : null;
    }

    public Employee department(Department department) {
        this.setDepartment(department);
        return this;
    }

    public Set<Route> getRoutes() {
        return this.routes;
    }

    public void setRoutes(Set<Route> routes) {
        if (this.routes != null) {
            this.routes.forEach(i -> i.removeEmployee(this));
        }
        if (routes != null) {
            routes.forEach(i -> i.addEmployee(this));
        }
        this.routes = routes;
    }

    public Employee routes(Set<Route> routes) {
        this.setRoutes(routes);
        return this;
    }

    public Employee addRoute(Route route) {
        this.routes.add(route);
        route.getEmployees().add(this);
        return this;
    }

    public Employee removeRoute(Route route) {
        this.routes.remove(route);
        route.getEmployees().remove(this);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Long getDepartmentId() {
        return this.departmentId;
    }

    public void setDepartmentId(Long department) {
        this.departmentId = department;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }
        return getId() != null && getId().equals(((Employee) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Employee{" +
            "id=" + getId() +
            ", employeCode='" + getEmployeCode() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", jobTitle='" + getJobTitle() + "'" +
            "}";
    }
}
