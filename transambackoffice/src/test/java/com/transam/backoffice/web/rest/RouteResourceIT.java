package com.transam.backoffice.web.rest;

import static com.transam.backoffice.domain.RouteAsserts.*;
import static com.transam.backoffice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.transam.backoffice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transam.backoffice.IntegrationTest;
import com.transam.backoffice.domain.Route;
import com.transam.backoffice.repository.EntityManager;
import com.transam.backoffice.repository.RouteRepository;
import java.math.BigDecimal;
import java.time.Duration;
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
 * Integration tests for the {@link RouteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RouteResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_START = "AAAAAAAAAA";
    private static final String UPDATED_START = "BBBBBBBBBB";

    private static final String DEFAULT_DESTINATION = "AAAAAAAAAA";
    private static final String UPDATED_DESTINATION = "BBBBBBBBBB";

    private static final Integer DEFAULT_PASSENGER_CAPACITY = 1;
    private static final Integer UPDATED_PASSENGER_CAPACITY = 2;

    private static final BigDecimal DEFAULT_PARCEL_TOTAL_WEIGHT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PARCEL_TOTAL_WEIGHT = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/routes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RouteRepository routeRepository;

    @Mock
    private RouteRepository routeRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Route route;

    private Route insertedRoute;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Route createEntity() {
        return new Route()
            .code(DEFAULT_CODE)
            .description(DEFAULT_DESCRIPTION)
            .start(DEFAULT_START)
            .destination(DEFAULT_DESTINATION)
            .passengerCapacity(DEFAULT_PASSENGER_CAPACITY)
            .parcelTotalWeight(DEFAULT_PARCEL_TOTAL_WEIGHT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Route createUpdatedEntity() {
        return new Route()
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .start(UPDATED_START)
            .destination(UPDATED_DESTINATION)
            .passengerCapacity(UPDATED_PASSENGER_CAPACITY)
            .parcelTotalWeight(UPDATED_PARCEL_TOTAL_WEIGHT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_route__employee").block();
            em.deleteAll("rel_route__vehicle").block();
            em.deleteAll(Route.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        route = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedRoute != null) {
            routeRepository.delete(insertedRoute).block();
            insertedRoute = null;
        }
        deleteEntities(em);
    }

    @Test
    void createRoute() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Route
        var returnedRoute = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(route))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Route.class)
            .returnResult()
            .getResponseBody();

        // Validate the Route in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRouteUpdatableFieldsEquals(returnedRoute, getPersistedRoute(returnedRoute));

        insertedRoute = returnedRoute;
    }

    @Test
    void createRouteWithExistingId() throws Exception {
        // Create the Route with an existing ID
        route.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(route))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        route.setCode(null);

        // Create the Route, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(route))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        route.setDescription(null);

        // Create the Route, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(route))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStartIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        route.setStart(null);

        // Create the Route, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(route))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDestinationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        route.setDestination(null);

        // Create the Route, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(route))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPassengerCapacityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        route.setPassengerCapacity(null);

        // Create the Route, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(route))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkParcelTotalWeightIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        route.setParcelTotalWeight(null);

        // Create the Route, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(route))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllRoutesAsStream() {
        // Initialize the database
        routeRepository.save(route).block();

        List<Route> routeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Route.class)
            .getResponseBody()
            .filter(route::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(routeList).isNotNull();
        assertThat(routeList).hasSize(1);
        Route testRoute = routeList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertRouteAllPropertiesEquals(route, testRoute);
        assertRouteUpdatableFieldsEquals(route, testRoute);
    }

    @Test
    void getAllRoutes() {
        // Initialize the database
        insertedRoute = routeRepository.save(route).block();

        // Get all the routeList
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
            .value(hasItem(route.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].start")
            .value(hasItem(DEFAULT_START))
            .jsonPath("$.[*].destination")
            .value(hasItem(DEFAULT_DESTINATION))
            .jsonPath("$.[*].passengerCapacity")
            .value(hasItem(DEFAULT_PASSENGER_CAPACITY))
            .jsonPath("$.[*].parcelTotalWeight")
            .value(hasItem(sameNumber(DEFAULT_PARCEL_TOTAL_WEIGHT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRoutesWithEagerRelationshipsIsEnabled() {
        when(routeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(routeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRoutesWithEagerRelationshipsIsNotEnabled() {
        when(routeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(routeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getRoute() {
        // Initialize the database
        insertedRoute = routeRepository.save(route).block();

        // Get the route
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, route.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(route.getId().intValue()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.start")
            .value(is(DEFAULT_START))
            .jsonPath("$.destination")
            .value(is(DEFAULT_DESTINATION))
            .jsonPath("$.passengerCapacity")
            .value(is(DEFAULT_PASSENGER_CAPACITY))
            .jsonPath("$.parcelTotalWeight")
            .value(is(sameNumber(DEFAULT_PARCEL_TOTAL_WEIGHT)));
    }

    @Test
    void getNonExistingRoute() {
        // Get the route
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRoute() throws Exception {
        // Initialize the database
        insertedRoute = routeRepository.save(route).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the route
        Route updatedRoute = routeRepository.findById(route.getId()).block();
        updatedRoute
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .start(UPDATED_START)
            .destination(UPDATED_DESTINATION)
            .passengerCapacity(UPDATED_PASSENGER_CAPACITY)
            .parcelTotalWeight(UPDATED_PARCEL_TOTAL_WEIGHT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRoute.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedRoute))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRouteToMatchAllProperties(updatedRoute);
    }

    @Test
    void putNonExistingRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        route.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, route.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(route))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        route.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(route))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        route.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(route))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRouteWithPatch() throws Exception {
        // Initialize the database
        insertedRoute = routeRepository.save(route).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the route using partial update
        Route partialUpdatedRoute = new Route();
        partialUpdatedRoute.setId(route.getId());

        partialUpdatedRoute.description(UPDATED_DESCRIPTION).parcelTotalWeight(UPDATED_PARCEL_TOTAL_WEIGHT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRoute.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRoute))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Route in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRouteUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRoute, route), getPersistedRoute(route));
    }

    @Test
    void fullUpdateRouteWithPatch() throws Exception {
        // Initialize the database
        insertedRoute = routeRepository.save(route).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the route using partial update
        Route partialUpdatedRoute = new Route();
        partialUpdatedRoute.setId(route.getId());

        partialUpdatedRoute
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .start(UPDATED_START)
            .destination(UPDATED_DESTINATION)
            .passengerCapacity(UPDATED_PASSENGER_CAPACITY)
            .parcelTotalWeight(UPDATED_PARCEL_TOTAL_WEIGHT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRoute.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRoute))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Route in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRouteUpdatableFieldsEquals(partialUpdatedRoute, getPersistedRoute(partialUpdatedRoute));
    }

    @Test
    void patchNonExistingRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        route.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, route.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(route))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        route.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(route))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        route.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(route))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRoute() {
        // Initialize the database
        insertedRoute = routeRepository.save(route).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the route
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, route.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return routeRepository.count().block();
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

    protected Route getPersistedRoute(Route route) {
        return routeRepository.findById(route.getId()).block();
    }

    protected void assertPersistedRouteToMatchAllProperties(Route expectedRoute) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRouteAllPropertiesEquals(expectedRoute, getPersistedRoute(expectedRoute));
        assertRouteUpdatableFieldsEquals(expectedRoute, getPersistedRoute(expectedRoute));
    }

    protected void assertPersistedRouteToMatchUpdatableProperties(Route expectedRoute) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRouteAllUpdatablePropertiesEquals(expectedRoute, getPersistedRoute(expectedRoute));
        assertRouteUpdatableFieldsEquals(expectedRoute, getPersistedRoute(expectedRoute));
    }
}
