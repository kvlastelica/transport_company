package com.transam.backoffice.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class DepartmentAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDepartmentAllPropertiesEquals(Department expected, Department actual) {
        assertDepartmentAutoGeneratedPropertiesEquals(expected, actual);
        assertDepartmentAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDepartmentAllUpdatablePropertiesEquals(Department expected, Department actual) {
        assertDepartmentUpdatableFieldsEquals(expected, actual);
        assertDepartmentUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDepartmentAutoGeneratedPropertiesEquals(Department expected, Department actual) {
        assertThat(actual)
            .as("Verify Department auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDepartmentUpdatableFieldsEquals(Department expected, Department actual) {
        assertThat(actual)
            .as("Verify Department relevant properties")
            .satisfies(a -> assertThat(a.getDepartmentName()).as("check departmentName").isEqualTo(expected.getDepartmentName()))
            .satisfies(a -> assertThat(a.getLocation()).as("check location").isEqualTo(expected.getLocation()))
            .satisfies(a -> assertThat(a.getDivision()).as("check division").isEqualTo(expected.getDivision()))
            .satisfies(a -> assertThat(a.getFormat()).as("check format").isEqualTo(expected.getFormat()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDepartmentUpdatableRelationshipsEquals(Department expected, Department actual) {
        // empty method
    }
}
