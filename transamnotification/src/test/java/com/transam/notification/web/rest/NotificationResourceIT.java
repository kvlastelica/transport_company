package com.transam.notification.web.rest;

import static com.transam.notification.domain.NotificationAsserts.*;
import static com.transam.notification.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transam.notification.IntegrationTest;
import com.transam.notification.domain.Notification;
import com.transam.notification.domain.enumeration.NotificationType;
import com.transam.notification.repository.NotificationRepository;
import com.transam.notification.repository.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link NotificationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class NotificationResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final Instant DEFAULT_SENT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final NotificationType DEFAULT_FORMAT = NotificationType.EMAIL;
    private static final NotificationType UPDATED_FORMAT = NotificationType.SMS;

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final String ENTITY_API_URL = "/api/notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Notification notification;

    private Notification insertedNotification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createEntity() {
        return new Notification()
            .date(DEFAULT_DATE)
            .details(DEFAULT_DETAILS)
            .sentDate(DEFAULT_SENT_DATE)
            .format(DEFAULT_FORMAT)
            .userId(DEFAULT_USER_ID)
            .productId(DEFAULT_PRODUCT_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createUpdatedEntity() {
        return new Notification()
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS)
            .sentDate(UPDATED_SENT_DATE)
            .format(UPDATED_FORMAT)
            .userId(UPDATED_USER_ID)
            .productId(UPDATED_PRODUCT_ID);
    }

    @BeforeEach
    public void initTest() {
        notification = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedNotification != null) {
            notificationRepository.delete(insertedNotification).block();
            insertedNotification = null;
        }
    }

    @Test
    void createNotification() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Notification
        var returnedNotification = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notification))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Notification.class)
            .returnResult()
            .getResponseBody();

        // Validate the Notification in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertNotificationUpdatableFieldsEquals(returnedNotification, getPersistedNotification(returnedNotification));

        insertedNotification = returnedNotification;
    }

    @Test
    void createNotificationWithExistingId() throws Exception {
        // Create the Notification with an existing ID
        notification.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notification.setDate(null);

        // Create the Notification, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSentDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notification.setSentDate(null);

        // Create the Notification, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFormatIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notification.setFormat(null);

        // Create the Notification, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notification.setUserId(null);

        // Create the Notification, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkProductIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notification.setProductId(null);

        // Create the Notification, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllNotificationsAsStream() {
        // Initialize the database
        notificationRepository.save(notification).block();

        List<Notification> notificationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Notification.class)
            .getResponseBody()
            .filter(notification::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(notificationList).isNotNull();
        assertThat(notificationList).hasSize(1);
        Notification testNotification = notificationList.get(0);

        assertNotificationAllPropertiesEquals(notification, testNotification);
    }

    @Test
    void getAllNotifications() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(notification.getId()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].details")
            .value(hasItem(DEFAULT_DETAILS))
            .jsonPath("$.[*].sentDate")
            .value(hasItem(DEFAULT_SENT_DATE.toString()))
            .jsonPath("$.[*].format")
            .value(hasItem(DEFAULT_FORMAT.toString()))
            .jsonPath("$.[*].userId")
            .value(hasItem(DEFAULT_USER_ID.intValue()))
            .jsonPath("$.[*].productId")
            .value(hasItem(DEFAULT_PRODUCT_ID.intValue()));
    }

    @Test
    void getNotification() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get the notification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, notification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(notification.getId()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()))
            .jsonPath("$.details")
            .value(is(DEFAULT_DETAILS))
            .jsonPath("$.sentDate")
            .value(is(DEFAULT_SENT_DATE.toString()))
            .jsonPath("$.format")
            .value(is(DEFAULT_FORMAT.toString()))
            .jsonPath("$.userId")
            .value(is(DEFAULT_USER_ID.intValue()))
            .jsonPath("$.productId")
            .value(is(DEFAULT_PRODUCT_ID.intValue()));
    }

    @Test
    void getNonExistingNotification() {
        // Get the notification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingNotification() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notification
        Notification updatedNotification = notificationRepository.findById(notification.getId()).block();
        updatedNotification
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS)
            .sentDate(UPDATED_SENT_DATE)
            .format(UPDATED_FORMAT)
            .userId(UPDATED_USER_ID)
            .productId(UPDATED_PRODUCT_ID);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedNotification.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedNotification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotificationToMatchAllProperties(updatedNotification);
    }

    @Test
    void putNonExistingNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, notification.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notification))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification.details(UPDATED_DETAILS).sentDate(UPDATED_SENT_DATE).format(UPDATED_FORMAT).userId(UPDATED_USER_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedNotification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotification, notification),
            getPersistedNotification(notification)
        );
    }

    @Test
    void fullUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS)
            .sentDate(UPDATED_SENT_DATE)
            .format(UPDATED_FORMAT)
            .userId(UPDATED_USER_ID)
            .productId(UPDATED_PRODUCT_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedNotification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationUpdatableFieldsEquals(partialUpdatedNotification, getPersistedNotification(partialUpdatedNotification));
    }

    @Test
    void patchNonExistingNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, notification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(notification))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteNotification() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the notification
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, notification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return notificationRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Notification getPersistedNotification(Notification notification) {
        return notificationRepository.findById(notification.getId()).block();
    }

    protected void assertPersistedNotificationToMatchAllProperties(Notification expectedNotification) {
        assertNotificationAllPropertiesEquals(expectedNotification, getPersistedNotification(expectedNotification));
    }

    protected void assertPersistedNotificationToMatchUpdatableProperties(Notification expectedNotification) {
        assertNotificationAllUpdatablePropertiesEquals(expectedNotification, getPersistedNotification(expectedNotification));
    }
}
