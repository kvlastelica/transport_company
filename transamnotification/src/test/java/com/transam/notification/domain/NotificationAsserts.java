package com.transam.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertNotificationAllPropertiesEquals(Notification expected, Notification actual) {
        assertNotificationAutoGeneratedPropertiesEquals(expected, actual);
        assertNotificationAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertNotificationAllUpdatablePropertiesEquals(Notification expected, Notification actual) {
        assertNotificationUpdatableFieldsEquals(expected, actual);
        assertNotificationUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertNotificationAutoGeneratedPropertiesEquals(Notification expected, Notification actual) {
        assertThat(actual)
            .as("Verify Notification auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertNotificationUpdatableFieldsEquals(Notification expected, Notification actual) {
        assertThat(actual)
            .as("Verify Notification relevant properties")
            .satisfies(a -> assertThat(a.getDate()).as("check date").isEqualTo(expected.getDate()))
            .satisfies(a -> assertThat(a.getDetails()).as("check details").isEqualTo(expected.getDetails()))
            .satisfies(a -> assertThat(a.getSentDate()).as("check sentDate").isEqualTo(expected.getSentDate()))
            .satisfies(a -> assertThat(a.getFormat()).as("check format").isEqualTo(expected.getFormat()))
            .satisfies(a -> assertThat(a.getUserId()).as("check userId").isEqualTo(expected.getUserId()))
            .satisfies(a -> assertThat(a.getProductId()).as("check productId").isEqualTo(expected.getProductId()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertNotificationUpdatableRelationshipsEquals(Notification expected, Notification actual) {
        // empty method
    }
}
