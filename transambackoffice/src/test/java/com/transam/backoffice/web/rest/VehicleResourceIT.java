package com.transam.backoffice.web.rest;

import static com.transam.backoffice.domain.VehicleAsserts.*;
import static com.transam.backoffice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transam.backoffice.IntegrationTest;
import com.transam.backoffice.domain.Route;
import com.transam.backoffice.domain.Vehicle;
import com.transam.backoffice.domain.enumeration.VehicleType;
import com.transam.backoffice.repository.EntityManager;
import com.transam.backoffice.repository.VehicleRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link VehicleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class VehicleResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final VehicleType DEFAULT_FORMAT = VehicleType.CAR;
    private static final VehicleType UPDATED_FORMAT = VehicleType.VAN;

    private static final String ENTITY_API_URL = "/api/vehicles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Vehicle vehicle;

    private Vehicle insertedVehicle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vehicle createEntity(EntityManager em) {
        Vehicle vehicle = new Vehicle().code(DEFAULT_CODE).description(DEFAULT_DESCRIPTION).format(DEFAULT_FORMAT);
        // Add required entity
        Route route;
        route = em.insert(RouteResourceIT.createEntity()).block();
        vehicle.getRoutes().add(route);
        return vehicle;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vehicle createUpdatedEntity(EntityManager em) {
        Vehicle updatedVehicle = new Vehicle().code(UPDATED_CODE).description(UPDATED_DESCRIPTION).format(UPDATED_FORMAT);
        // Add required entity
        Route route;
        route = em.insert(RouteResourceIT.createUpdatedEntity()).block();
        updatedVehicle.getRoutes().add(route);
        return updatedVehicle;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Vehicle.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        RouteResourceIT.deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        vehicle = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedVehicle != null) {
            vehicleRepository.delete(insertedVehicle).block();
            insertedVehicle = null;
        }
        deleteEntities(em);
    }

    @Test
    void createVehicle() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Vehicle
        var returnedVehicle = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(vehicle))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Vehicle.class)
            .returnResult()
            .getResponseBody();

        // Validate the Vehicle in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertVehicleUpdatableFieldsEquals(returnedVehicle, getPersistedVehicle(returnedVehicle));

        insertedVehicle = returnedVehicle;
    }

    @Test
    void createVehicleWithExistingId() throws Exception {
        // Create the Vehicle with an existing ID
        vehicle.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(vehicle))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vehicle in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        vehicle.setCode(null);

        // Create the Vehicle, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(vehicle))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        vehicle.setDescription(null);

        // Create the Vehicle, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(vehicle))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFormatIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        vehicle.setFormat(null);

        // Create the Vehicle, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(vehicle))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllVehiclesAsStream() {
        // Initialize the database
        vehicleRepository.save(vehicle).block();

        List<Vehicle> vehicleList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Vehicle.class)
            .getResponseBody()
            .filter(vehicle::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(vehicleList).isNotNull();
        assertThat(vehicleList).hasSize(1);
        Vehicle testVehicle = vehicleList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertVehicleAllPropertiesEquals(vehicle, testVehicle);
        assertVehicleUpdatableFieldsEquals(vehicle, testVehicle);
    }

    @Test
    void getAllVehicles() {
        // Initialize the database
        insertedVehicle = vehicleRepository.save(vehicle).block();

        // Get all the vehicleList
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
            .value(hasItem(vehicle.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].format")
            .value(hasItem(DEFAULT_FORMAT.toString()));
    }

    @Test
    void getVehicle() {
        // Initialize the database
        insertedVehicle = vehicleRepository.save(vehicle).block();

        // Get the vehicle
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, vehicle.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(vehicle.getId().intValue()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.format")
            .value(is(DEFAULT_FORMAT.toString()));
    }

    @Test
    void getNonExistingVehicle() {
        // Get the vehicle
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingVehicle() throws Exception {
        // Initialize the database
        insertedVehicle = vehicleRepository.save(vehicle).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vehicle
        Vehicle updatedVehicle = vehicleRepository.findById(vehicle.getId()).block();
        updatedVehicle.code(UPDATED_CODE).description(UPDATED_DESCRIPTION).format(UPDATED_FORMAT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedVehicle.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedVehicle))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vehicle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVehicleToMatchAllProperties(updatedVehicle);
    }

    @Test
    void putNonExistingVehicle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehicle.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, vehicle.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(vehicle))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vehicle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchVehicle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehicle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(vehicle))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vehicle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamVehicle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehicle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(vehicle))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Vehicle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateVehicleWithPatch() throws Exception {
        // Initialize the database
        insertedVehicle = vehicleRepository.save(vehicle).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vehicle using partial update
        Vehicle partialUpdatedVehicle = new Vehicle();
        partialUpdatedVehicle.setId(vehicle.getId());

        partialUpdatedVehicle.code(UPDATED_CODE).format(UPDATED_FORMAT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVehicle.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedVehicle))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vehicle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVehicleUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedVehicle, vehicle), getPersistedVehicle(vehicle));
    }

    @Test
    void fullUpdateVehicleWithPatch() throws Exception {
        // Initialize the database
        insertedVehicle = vehicleRepository.save(vehicle).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vehicle using partial update
        Vehicle partialUpdatedVehicle = new Vehicle();
        partialUpdatedVehicle.setId(vehicle.getId());

        partialUpdatedVehicle.code(UPDATED_CODE).description(UPDATED_DESCRIPTION).format(UPDATED_FORMAT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVehicle.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedVehicle))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vehicle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVehicleUpdatableFieldsEquals(partialUpdatedVehicle, getPersistedVehicle(partialUpdatedVehicle));
    }

    @Test
    void patchNonExistingVehicle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehicle.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, vehicle.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(vehicle))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vehicle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchVehicle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehicle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(vehicle))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vehicle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamVehicle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehicle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(vehicle))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Vehicle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteVehicle() {
        // Initialize the database
        insertedVehicle = vehicleRepository.save(vehicle).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the vehicle
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, vehicle.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return vehicleRepository.count().block();
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

    protected Vehicle getPersistedVehicle(Vehicle vehicle) {
        return vehicleRepository.findById(vehicle.getId()).block();
    }

    protected void assertPersistedVehicleToMatchAllProperties(Vehicle expectedVehicle) {
        // Test fails because reactive api returns an empty object instead of null
        // assertVehicleAllPropertiesEquals(expectedVehicle, getPersistedVehicle(expectedVehicle));
        assertVehicleUpdatableFieldsEquals(expectedVehicle, getPersistedVehicle(expectedVehicle));
    }

    protected void assertPersistedVehicleToMatchUpdatableProperties(Vehicle expectedVehicle) {
        // Test fails because reactive api returns an empty object instead of null
        // assertVehicleAllUpdatablePropertiesEquals(expectedVehicle, getPersistedVehicle(expectedVehicle));
        assertVehicleUpdatableFieldsEquals(expectedVehicle, getPersistedVehicle(expectedVehicle));
    }
}
