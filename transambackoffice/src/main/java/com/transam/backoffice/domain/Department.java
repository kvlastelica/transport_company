package com.transam.backoffice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.transam.backoffice.domain.enumeration.DepartmentType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Department.
 */
@Table("department")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("department_name")
    private String departmentName;

    @Column("location")
    private String location;

    @Column("division")
    private String division;

    @NotNull(message = "must not be null")
    @Column("format")
    private DepartmentType format;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "department", "routes" }, allowSetters = true)
    private Set<Employee> employees = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Department id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return this.departmentName;
    }

    public Department departmentName(String departmentName) {
        this.setDepartmentName(departmentName);
        return this;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getLocation() {
        return this.location;
    }

    public Department location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDivision() {
        return this.division;
    }

    public Department division(String division) {
        this.setDivision(division);
        return this;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public DepartmentType getFormat() {
        return this.format;
    }

    public Department format(DepartmentType format) {
        this.setFormat(format);
        return this;
    }

    public void setFormat(DepartmentType format) {
        this.format = format;
    }

    public Set<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employee> employees) {
        if (this.employees != null) {
            this.employees.forEach(i -> i.setDepartment(null));
        }
        if (employees != null) {
            employees.forEach(i -> i.setDepartment(this));
        }
        this.employees = employees;
    }

    public Department employees(Set<Employee> employees) {
        this.setEmployees(employees);
        return this;
    }

    public Department addEmployee(Employee employee) {
        this.employees.add(employee);
        employee.setDepartment(this);
        return this;
    }

    public Department removeEmployee(Employee employee) {
        this.employees.remove(employee);
        employee.setDepartment(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Department)) {
            return false;
        }
        return getId() != null && getId().equals(((Department) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Department{" +
            "id=" + getId() +
            ", departmentName='" + getDepartmentName() + "'" +
            ", location='" + getLocation() + "'" +
            ", division='" + getDivision() + "'" +
            ", format='" + getFormat() + "'" +
            "}";
    }
}
