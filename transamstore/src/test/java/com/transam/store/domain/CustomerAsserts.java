package com.transam.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerAllPropertiesEquals(Customer expected, Customer actual) {
        assertCustomerAutoGeneratedPropertiesEquals(expected, actual);
        assertCustomerAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerAllUpdatablePropertiesEquals(Customer expected, Customer actual) {
        assertCustomerUpdatableFieldsEquals(expected, actual);
        assertCustomerUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerAutoGeneratedPropertiesEquals(Customer expected, Customer actual) {
        assertThat(actual)
            .as("Verify Customer auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerUpdatableFieldsEquals(Customer expected, Customer actual) {
        assertThat(actual)
            .as("Verify Customer relevant properties")
            .satisfies(a -> assertThat(a.getFirstName()).as("check firstName").isEqualTo(expected.getFirstName()))
            .satisfies(a -> assertThat(a.getLastName()).as("check lastName").isEqualTo(expected.getLastName()))
            .satisfies(a -> assertThat(a.getType()).as("check type").isEqualTo(expected.getType()))
            .satisfies(a -> assertThat(a.getEmail()).as("check email").isEqualTo(expected.getEmail()))
            .satisfies(a -> assertThat(a.getPhone()).as("check phone").isEqualTo(expected.getPhone()))
            .satisfies(a -> assertThat(a.getAddressLine1()).as("check addressLine1").isEqualTo(expected.getAddressLine1()))
            .satisfies(a -> assertThat(a.getAddressLine2()).as("check addressLine2").isEqualTo(expected.getAddressLine2()))
            .satisfies(a -> assertThat(a.getCity()).as("check city").isEqualTo(expected.getCity()))
            .satisfies(a -> assertThat(a.getCountry()).as("check country").isEqualTo(expected.getCountry()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCustomerUpdatableRelationshipsEquals(Customer expected, Customer actual) {
        // empty method
    }
}
