package com.transam.backoffice.web.rest;

import static com.transam.backoffice.domain.RouteScheduleAsserts.*;
import static com.transam.backoffice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transam.backoffice.IntegrationTest;
import com.transam.backoffice.domain.Route;
import com.transam.backoffice.domain.RouteSchedule;
import com.transam.backoffice.repository.EntityManager;
import com.transam.backoffice.repository.RouteScheduleRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link RouteScheduleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RouteScheduleResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_DEPARTURE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DEPARTURE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ARRIVAL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ARRIVAL = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/route-schedules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RouteScheduleRepository routeScheduleRepository;

    @Mock
    private RouteScheduleRepository routeScheduleRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RouteSchedule routeSchedule;

    private RouteSchedule insertedRouteSchedule;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RouteSchedule createEntity(EntityManager em) {
        RouteSchedule routeSchedule = new RouteSchedule()
            .code(DEFAULT_CODE)
            .description(DEFAULT_DESCRIPTION)
            .departure(DEFAULT_DEPARTURE)
            .arrival(DEFAULT_ARRIVAL);
        // Add required entity
        Route route;
        route = em.insert(RouteResourceIT.createEntity()).block();
        routeSchedule.setRoute(route);
        return routeSchedule;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RouteSchedule createUpdatedEntity(EntityManager em) {
        RouteSchedule updatedRouteSchedule = new RouteSchedule()
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .departure(UPDATED_DEPARTURE)
            .arrival(UPDATED_ARRIVAL);
        // Add required entity
        Route route;
        route = em.insert(RouteResourceIT.createUpdatedEntity()).block();
        updatedRouteSchedule.setRoute(route);
        return updatedRouteSchedule;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RouteSchedule.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        RouteResourceIT.deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        routeSchedule = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedRouteSchedule != null) {
            routeScheduleRepository.delete(insertedRouteSchedule).block();
            insertedRouteSchedule = null;
        }
        deleteEntities(em);
    }

    @Test
    void createRouteSchedule() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RouteSchedule
        var returnedRouteSchedule = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(routeSchedule))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(RouteSchedule.class)
            .returnResult()
            .getResponseBody();

        // Validate the RouteSchedule in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRouteScheduleUpdatableFieldsEquals(returnedRouteSchedule, getPersistedRouteSchedule(returnedRouteSchedule));

        insertedRouteSchedule = returnedRouteSchedule;
    }

    @Test
    void createRouteScheduleWithExistingId() throws Exception {
        // Create the RouteSchedule with an existing ID
        routeSchedule.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(routeSchedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RouteSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        routeSchedule.setCode(null);

        // Create the RouteSchedule, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(routeSchedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        routeSchedule.setDescription(null);

        // Create the RouteSchedule, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(routeSchedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDepartureIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        routeSchedule.setDeparture(null);

        // Create the RouteSchedule, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(routeSchedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkArrivalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        routeSchedule.setArrival(null);

        // Create the RouteSchedule, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(routeSchedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllRouteSchedulesAsStream() {
        // Initialize the database
        routeScheduleRepository.save(routeSchedule).block();

        List<RouteSchedule> routeScheduleList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(RouteSchedule.class)
            .getResponseBody()
            .filter(routeSchedule::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(routeScheduleList).isNotNull();
        assertThat(routeScheduleList).hasSize(1);
        RouteSchedule testRouteSchedule = routeScheduleList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertRouteScheduleAllPropertiesEquals(routeSchedule, testRouteSchedule);
        assertRouteScheduleUpdatableFieldsEquals(routeSchedule, testRouteSchedule);
    }

    @Test
    void getAllRouteSchedules() {
        // Initialize the database
        insertedRouteSchedule = routeScheduleRepository.save(routeSchedule).block();

        // Get all the routeScheduleList
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
            .value(hasItem(routeSchedule.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].departure")
            .value(hasItem(DEFAULT_DEPARTURE.toString()))
            .jsonPath("$.[*].arrival")
            .value(hasItem(DEFAULT_ARRIVAL.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRouteSchedulesWithEagerRelationshipsIsEnabled() {
        when(routeScheduleRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(routeScheduleRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRouteSchedulesWithEagerRelationshipsIsNotEnabled() {
        when(routeScheduleRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(routeScheduleRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getRouteSchedule() {
        // Initialize the database
        insertedRouteSchedule = routeScheduleRepository.save(routeSchedule).block();

        // Get the routeSchedule
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, routeSchedule.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(routeSchedule.getId().intValue()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.departure")
            .value(is(DEFAULT_DEPARTURE.toString()))
            .jsonPath("$.arrival")
            .value(is(DEFAULT_ARRIVAL.toString()));
    }

    @Test
    void getNonExistingRouteSchedule() {
        // Get the routeSchedule
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRouteSchedule() throws Exception {
        // Initialize the database
        insertedRouteSchedule = routeScheduleRepository.save(routeSchedule).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the routeSchedule
        RouteSchedule updatedRouteSchedule = routeScheduleRepository.findById(routeSchedule.getId()).block();
        updatedRouteSchedule.code(UPDATED_CODE).description(UPDATED_DESCRIPTION).departure(UPDATED_DEPARTURE).arrival(UPDATED_ARRIVAL);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRouteSchedule.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedRouteSchedule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RouteSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRouteScheduleToMatchAllProperties(updatedRouteSchedule);
    }

    @Test
    void putNonExistingRouteSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routeSchedule.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, routeSchedule.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(routeSchedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RouteSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRouteSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routeSchedule.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(routeSchedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RouteSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRouteSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routeSchedule.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(routeSchedule))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RouteSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRouteScheduleWithPatch() throws Exception {
        // Initialize the database
        insertedRouteSchedule = routeScheduleRepository.save(routeSchedule).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the routeSchedule using partial update
        RouteSchedule partialUpdatedRouteSchedule = new RouteSchedule();
        partialUpdatedRouteSchedule.setId(routeSchedule.getId());

        partialUpdatedRouteSchedule.description(UPDATED_DESCRIPTION).arrival(UPDATED_ARRIVAL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRouteSchedule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRouteSchedule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RouteSchedule in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRouteScheduleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRouteSchedule, routeSchedule),
            getPersistedRouteSchedule(routeSchedule)
        );
    }

    @Test
    void fullUpdateRouteScheduleWithPatch() throws Exception {
        // Initialize the database
        insertedRouteSchedule = routeScheduleRepository.save(routeSchedule).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the routeSchedule using partial update
        RouteSchedule partialUpdatedRouteSchedule = new RouteSchedule();
        partialUpdatedRouteSchedule.setId(routeSchedule.getId());

        partialUpdatedRouteSchedule
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .departure(UPDATED_DEPARTURE)
            .arrival(UPDATED_ARRIVAL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRouteSchedule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRouteSchedule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RouteSchedule in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRouteScheduleUpdatableFieldsEquals(partialUpdatedRouteSchedule, getPersistedRouteSchedule(partialUpdatedRouteSchedule));
    }

    @Test
    void patchNonExistingRouteSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routeSchedule.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, routeSchedule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(routeSchedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RouteSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRouteSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routeSchedule.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(routeSchedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RouteSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRouteSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routeSchedule.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(routeSchedule))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RouteSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRouteSchedule() {
        // Initialize the database
        insertedRouteSchedule = routeScheduleRepository.save(routeSchedule).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the routeSchedule
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, routeSchedule.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return routeScheduleRepository.count().block();
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

    protected RouteSchedule getPersistedRouteSchedule(RouteSchedule routeSchedule) {
        return routeScheduleRepository.findById(routeSchedule.getId()).block();
    }

    protected void assertPersistedRouteScheduleToMatchAllProperties(RouteSchedule expectedRouteSchedule) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRouteScheduleAllPropertiesEquals(expectedRouteSchedule, getPersistedRouteSchedule(expectedRouteSchedule));
        assertRouteScheduleUpdatableFieldsEquals(expectedRouteSchedule, getPersistedRouteSchedule(expectedRouteSchedule));
    }

    protected void assertPersistedRouteScheduleToMatchUpdatableProperties(RouteSchedule expectedRouteSchedule) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRouteScheduleAllUpdatablePropertiesEquals(expectedRouteSchedule, getPersistedRouteSchedule(expectedRouteSchedule));
        assertRouteScheduleUpdatableFieldsEquals(expectedRouteSchedule, getPersistedRouteSchedule(expectedRouteSchedule));
    }
}
