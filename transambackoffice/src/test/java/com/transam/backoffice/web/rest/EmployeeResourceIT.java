package com.transam.backoffice.web.rest;

import static com.transam.backoffice.domain.EmployeeAsserts.*;
import static com.transam.backoffice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transam.backoffice.IntegrationTest;
import com.transam.backoffice.domain.Department;
import com.transam.backoffice.domain.Employee;
import com.transam.backoffice.domain.Route;
import com.transam.backoffice.repository.EmployeeRepository;
import com.transam.backoffice.repository.EntityManager;
import com.transam.backoffice.repository.UserRepository;
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
 * Integration tests for the {@link EmployeeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EmployeeResourceIT {

    private static final String DEFAULT_EMPLOYE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_EMPLOYE_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_JOB_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_JOB_TITLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/employees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Employee employee;

    private Employee insertedEmployee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createEntity(EntityManager em) {
        Employee employee = new Employee()
            .employeCode(DEFAULT_EMPLOYE_CODE)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .jobTitle(DEFAULT_JOB_TITLE);
        // Add required entity
        Department department;
        department = em.insert(DepartmentResourceIT.createEntity()).block();
        employee.setDepartment(department);
        // Add required entity
        Route route;
        route = em.insert(RouteResourceIT.createEntity()).block();
        employee.getRoutes().add(route);
        return employee;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createUpdatedEntity(EntityManager em) {
        Employee updatedEmployee = new Employee()
            .employeCode(UPDATED_EMPLOYE_CODE)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .jobTitle(UPDATED_JOB_TITLE);
        // Add required entity
        Department department;
        department = em.insert(DepartmentResourceIT.createUpdatedEntity()).block();
        updatedEmployee.setDepartment(department);
        // Add required entity
        Route route;
        route = em.insert(RouteResourceIT.createUpdatedEntity()).block();
        updatedEmployee.getRoutes().add(route);
        return updatedEmployee;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Employee.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        DepartmentResourceIT.deleteEntities(em);
        RouteResourceIT.deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        employee = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedEmployee != null) {
            employeeRepository.delete(insertedEmployee).block();
            insertedEmployee = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createEmployee() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Employee
        var returnedEmployee = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(employee))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Employee.class)
            .returnResult()
            .getResponseBody();

        // Validate the Employee in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEmployeeUpdatableFieldsEquals(returnedEmployee, getPersistedEmployee(returnedEmployee));

        insertedEmployee = returnedEmployee;
    }

    @Test
    void createEmployeeWithExistingId() throws Exception {
        // Create the Employee with an existing ID
        employee.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(employee))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkEmployeCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        employee.setEmployeCode(null);

        // Create the Employee, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(employee))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        employee.setFirstName(null);

        // Create the Employee, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(employee))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        employee.setLastName(null);

        // Create the Employee, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(employee))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEmployeesAsStream() {
        // Initialize the database
        employeeRepository.save(employee).block();

        List<Employee> employeeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Employee.class)
            .getResponseBody()
            .filter(employee::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(employeeList).isNotNull();
        assertThat(employeeList).hasSize(1);
        Employee testEmployee = employeeList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertEmployeeAllPropertiesEquals(employee, testEmployee);
        assertEmployeeUpdatableFieldsEquals(employee, testEmployee);
    }

    @Test
    void getAllEmployees() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList
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
            .value(hasItem(employee.getId().intValue()))
            .jsonPath("$.[*].employeCode")
            .value(hasItem(DEFAULT_EMPLOYE_CODE))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].jobTitle")
            .value(hasItem(DEFAULT_JOB_TITLE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEmployeesWithEagerRelationshipsIsEnabled() {
        when(employeeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(employeeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEmployeesWithEagerRelationshipsIsNotEnabled() {
        when(employeeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(employeeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getEmployee() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get the employee
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, employee.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(employee.getId().intValue()))
            .jsonPath("$.employeCode")
            .value(is(DEFAULT_EMPLOYE_CODE))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.jobTitle")
            .value(is(DEFAULT_JOB_TITLE));
    }

    @Test
    void getNonExistingEmployee() {
        // Get the employee
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEmployee() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the employee
        Employee updatedEmployee = employeeRepository.findById(employee.getId()).block();
        updatedEmployee
            .employeCode(UPDATED_EMPLOYE_CODE)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .jobTitle(UPDATED_JOB_TITLE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEmployee.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedEmployee))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEmployeeToMatchAllProperties(updatedEmployee);
    }

    @Test
    void putNonExistingEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        employee.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, employee.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(employee))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        employee.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(employee))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        employee.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(employee))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEmployeeWithPatch() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the employee using partial update
        Employee partialUpdatedEmployee = new Employee();
        partialUpdatedEmployee.setId(employee.getId());

        partialUpdatedEmployee.employeCode(UPDATED_EMPLOYE_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEmployee.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEmployee))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Employee in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEmployeeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEmployee, employee), getPersistedEmployee(employee));
    }

    @Test
    void fullUpdateEmployeeWithPatch() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the employee using partial update
        Employee partialUpdatedEmployee = new Employee();
        partialUpdatedEmployee.setId(employee.getId());

        partialUpdatedEmployee
            .employeCode(UPDATED_EMPLOYE_CODE)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .jobTitle(UPDATED_JOB_TITLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEmployee.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEmployee))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Employee in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEmployeeUpdatableFieldsEquals(partialUpdatedEmployee, getPersistedEmployee(partialUpdatedEmployee));
    }

    @Test
    void patchNonExistingEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        employee.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, employee.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(employee))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        employee.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(employee))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        employee.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(employee))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEmployee() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the employee
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, employee.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return employeeRepository.count().block();
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

    protected Employee getPersistedEmployee(Employee employee) {
        return employeeRepository.findById(employee.getId()).block();
    }

    protected void assertPersistedEmployeeToMatchAllProperties(Employee expectedEmployee) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEmployeeAllPropertiesEquals(expectedEmployee, getPersistedEmployee(expectedEmployee));
        assertEmployeeUpdatableFieldsEquals(expectedEmployee, getPersistedEmployee(expectedEmployee));
    }

    protected void assertPersistedEmployeeToMatchUpdatableProperties(Employee expectedEmployee) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEmployeeAllUpdatablePropertiesEquals(expectedEmployee, getPersistedEmployee(expectedEmployee));
        assertEmployeeUpdatableFieldsEquals(expectedEmployee, getPersistedEmployee(expectedEmployee));
    }
}
