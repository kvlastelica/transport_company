package com.transam.backoffice.web.rest;

import static com.transam.backoffice.domain.DepartmentAsserts.*;
import static com.transam.backoffice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transam.backoffice.IntegrationTest;
import com.transam.backoffice.domain.Department;
import com.transam.backoffice.domain.enumeration.DepartmentType;
import com.transam.backoffice.repository.DepartmentRepository;
import com.transam.backoffice.repository.EntityManager;
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
 * Integration tests for the {@link DepartmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DepartmentResourceIT {

    private static final String DEFAULT_DEPARTMENT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final String DEFAULT_DIVISION = "AAAAAAAAAA";
    private static final String UPDATED_DIVISION = "BBBBBBBBBB";

    private static final DepartmentType DEFAULT_FORMAT = DepartmentType.TICKETING;
    private static final DepartmentType UPDATED_FORMAT = DepartmentType.CONTROLLING;

    private static final String ENTITY_API_URL = "/api/departments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Department department;

    private Department insertedDepartment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Department createEntity() {
        return new Department()
            .departmentName(DEFAULT_DEPARTMENT_NAME)
            .location(DEFAULT_LOCATION)
            .division(DEFAULT_DIVISION)
            .format(DEFAULT_FORMAT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Department createUpdatedEntity() {
        return new Department()
            .departmentName(UPDATED_DEPARTMENT_NAME)
            .location(UPDATED_LOCATION)
            .division(UPDATED_DIVISION)
            .format(UPDATED_FORMAT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Department.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        department = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDepartment != null) {
            departmentRepository.delete(insertedDepartment).block();
            insertedDepartment = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDepartment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Department
        var returnedDepartment = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(department))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Department.class)
            .returnResult()
            .getResponseBody();

        // Validate the Department in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDepartmentUpdatableFieldsEquals(returnedDepartment, getPersistedDepartment(returnedDepartment));

        insertedDepartment = returnedDepartment;
    }

    @Test
    void createDepartmentWithExistingId() throws Exception {
        // Create the Department with an existing ID
        department.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(department))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkDepartmentNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        department.setDepartmentName(null);

        // Create the Department, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(department))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFormatIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        department.setFormat(null);

        // Create the Department, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(department))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDepartmentsAsStream() {
        // Initialize the database
        departmentRepository.save(department).block();

        List<Department> departmentList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Department.class)
            .getResponseBody()
            .filter(department::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(departmentList).isNotNull();
        assertThat(departmentList).hasSize(1);
        Department testDepartment = departmentList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertDepartmentAllPropertiesEquals(department, testDepartment);
        assertDepartmentUpdatableFieldsEquals(department, testDepartment);
    }

    @Test
    void getAllDepartments() {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        // Get all the departmentList
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
            .value(hasItem(department.getId().intValue()))
            .jsonPath("$.[*].departmentName")
            .value(hasItem(DEFAULT_DEPARTMENT_NAME))
            .jsonPath("$.[*].location")
            .value(hasItem(DEFAULT_LOCATION))
            .jsonPath("$.[*].division")
            .value(hasItem(DEFAULT_DIVISION))
            .jsonPath("$.[*].format")
            .value(hasItem(DEFAULT_FORMAT.toString()));
    }

    @Test
    void getDepartment() {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        // Get the department
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, department.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(department.getId().intValue()))
            .jsonPath("$.departmentName")
            .value(is(DEFAULT_DEPARTMENT_NAME))
            .jsonPath("$.location")
            .value(is(DEFAULT_LOCATION))
            .jsonPath("$.division")
            .value(is(DEFAULT_DIVISION))
            .jsonPath("$.format")
            .value(is(DEFAULT_FORMAT.toString()));
    }

    @Test
    void getNonExistingDepartment() {
        // Get the department
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDepartment() throws Exception {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the department
        Department updatedDepartment = departmentRepository.findById(department.getId()).block();
        updatedDepartment
            .departmentName(UPDATED_DEPARTMENT_NAME)
            .location(UPDATED_LOCATION)
            .division(UPDATED_DIVISION)
            .format(UPDATED_FORMAT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedDepartment.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedDepartment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDepartmentToMatchAllProperties(updatedDepartment);
    }

    @Test
    void putNonExistingDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        department.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, department.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(department))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        department.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(department))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        department.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(department))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDepartmentWithPatch() throws Exception {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the department using partial update
        Department partialUpdatedDepartment = new Department();
        partialUpdatedDepartment.setId(department.getId());

        partialUpdatedDepartment.departmentName(UPDATED_DEPARTMENT_NAME).division(UPDATED_DIVISION).format(UPDATED_FORMAT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDepartment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDepartment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Department in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDepartmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDepartment, department),
            getPersistedDepartment(department)
        );
    }

    @Test
    void fullUpdateDepartmentWithPatch() throws Exception {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the department using partial update
        Department partialUpdatedDepartment = new Department();
        partialUpdatedDepartment.setId(department.getId());

        partialUpdatedDepartment
            .departmentName(UPDATED_DEPARTMENT_NAME)
            .location(UPDATED_LOCATION)
            .division(UPDATED_DIVISION)
            .format(UPDATED_FORMAT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDepartment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDepartment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Department in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDepartmentUpdatableFieldsEquals(partialUpdatedDepartment, getPersistedDepartment(partialUpdatedDepartment));
    }

    @Test
    void patchNonExistingDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        department.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, department.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(department))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        department.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(department))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        department.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(department))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDepartment() {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the department
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, department.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return departmentRepository.count().block();
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

    protected Department getPersistedDepartment(Department department) {
        return departmentRepository.findById(department.getId()).block();
    }

    protected void assertPersistedDepartmentToMatchAllProperties(Department expectedDepartment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDepartmentAllPropertiesEquals(expectedDepartment, getPersistedDepartment(expectedDepartment));
        assertDepartmentUpdatableFieldsEquals(expectedDepartment, getPersistedDepartment(expectedDepartment));
    }

    protected void assertPersistedDepartmentToMatchUpdatableProperties(Department expectedDepartment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDepartmentAllUpdatablePropertiesEquals(expectedDepartment, getPersistedDepartment(expectedDepartment));
        assertDepartmentUpdatableFieldsEquals(expectedDepartment, getPersistedDepartment(expectedDepartment));
    }
}
