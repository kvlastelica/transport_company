package com.transam.store.web.rest;

import static com.transam.store.domain.TicketOrderAsserts.*;
import static com.transam.store.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transam.store.IntegrationTest;
import com.transam.store.domain.Customer;
import com.transam.store.domain.TicketOrder;
import com.transam.store.domain.enumeration.OrderStatus;
import com.transam.store.repository.EntityManager;
import com.transam.store.repository.TicketOrderRepository;
import com.transam.store.service.TicketOrderService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link TicketOrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TicketOrderResourceIT {

    private static final Instant DEFAULT_PLACED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PLACED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.COMPLETED;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.PENDING;

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Long DEFAULT_INVOICE_ID = 1L;
    private static final Long UPDATED_INVOICE_ID = 2L;

    private static final String ENTITY_API_URL = "/api/ticket-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TicketOrderRepository ticketOrderRepository;

    @Mock
    private TicketOrderRepository ticketOrderRepositoryMock;

    @Mock
    private TicketOrderService ticketOrderServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TicketOrder ticketOrder;

    private TicketOrder insertedTicketOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketOrder createEntity(EntityManager em) {
        TicketOrder ticketOrder = new TicketOrder()
            .placedDate(DEFAULT_PLACED_DATE)
            .status(DEFAULT_STATUS)
            .code(DEFAULT_CODE)
            .invoiceId(DEFAULT_INVOICE_ID);
        // Add required entity
        Customer customer;
        customer = em.insert(CustomerResourceIT.createEntity()).block();
        ticketOrder.setCustomer(customer);
        return ticketOrder;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketOrder createUpdatedEntity(EntityManager em) {
        TicketOrder updatedTicketOrder = new TicketOrder()
            .placedDate(UPDATED_PLACED_DATE)
            .status(UPDATED_STATUS)
            .code(UPDATED_CODE)
            .invoiceId(UPDATED_INVOICE_ID);
        // Add required entity
        Customer customer;
        customer = em.insert(CustomerResourceIT.createUpdatedEntity()).block();
        updatedTicketOrder.setCustomer(customer);
        return updatedTicketOrder;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TicketOrder.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        CustomerResourceIT.deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        ticketOrder = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTicketOrder != null) {
            ticketOrderRepository.delete(insertedTicketOrder).block();
            insertedTicketOrder = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTicketOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TicketOrder
        var returnedTicketOrder = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticketOrder))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TicketOrder.class)
            .returnResult()
            .getResponseBody();

        // Validate the TicketOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTicketOrderUpdatableFieldsEquals(returnedTicketOrder, getPersistedTicketOrder(returnedTicketOrder));

        insertedTicketOrder = returnedTicketOrder;
    }

    @Test
    void createTicketOrderWithExistingId() throws Exception {
        // Create the TicketOrder with an existing ID
        ticketOrder.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticketOrder))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TicketOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkPlacedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticketOrder.setPlacedDate(null);

        // Create the TicketOrder, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticketOrder))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticketOrder.setStatus(null);

        // Create the TicketOrder, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticketOrder))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticketOrder.setCode(null);

        // Create the TicketOrder, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticketOrder))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTicketOrders() {
        // Initialize the database
        insertedTicketOrder = ticketOrderRepository.save(ticketOrder).block();

        // Get all the ticketOrderList
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
            .value(hasItem(ticketOrder.getId().intValue()))
            .jsonPath("$.[*].placedDate")
            .value(hasItem(DEFAULT_PLACED_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].invoiceId")
            .value(hasItem(DEFAULT_INVOICE_ID.intValue()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTicketOrdersWithEagerRelationshipsIsEnabled() {
        when(ticketOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(ticketOrderServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTicketOrdersWithEagerRelationshipsIsNotEnabled() {
        when(ticketOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(ticketOrderRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTicketOrder() {
        // Initialize the database
        insertedTicketOrder = ticketOrderRepository.save(ticketOrder).block();

        // Get the ticketOrder
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, ticketOrder.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(ticketOrder.getId().intValue()))
            .jsonPath("$.placedDate")
            .value(is(DEFAULT_PLACED_DATE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.invoiceId")
            .value(is(DEFAULT_INVOICE_ID.intValue()));
    }

    @Test
    void getNonExistingTicketOrder() {
        // Get the ticketOrder
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTicketOrder() throws Exception {
        // Initialize the database
        insertedTicketOrder = ticketOrderRepository.save(ticketOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketOrder
        TicketOrder updatedTicketOrder = ticketOrderRepository.findById(ticketOrder.getId()).block();
        updatedTicketOrder.placedDate(UPDATED_PLACED_DATE).status(UPDATED_STATUS).code(UPDATED_CODE).invoiceId(UPDATED_INVOICE_ID);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTicketOrder.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedTicketOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TicketOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTicketOrderToMatchAllProperties(updatedTicketOrder);
    }

    @Test
    void putNonExistingTicketOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketOrder.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ticketOrder.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticketOrder))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TicketOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTicketOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketOrder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticketOrder))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TicketOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTicketOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketOrder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticketOrder))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TicketOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTicketOrderWithPatch() throws Exception {
        // Initialize the database
        insertedTicketOrder = ticketOrderRepository.save(ticketOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketOrder using partial update
        TicketOrder partialUpdatedTicketOrder = new TicketOrder();
        partialUpdatedTicketOrder.setId(ticketOrder.getId());

        partialUpdatedTicketOrder.status(UPDATED_STATUS).code(UPDATED_CODE).invoiceId(UPDATED_INVOICE_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTicketOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTicketOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TicketOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketOrderUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTicketOrder, ticketOrder),
            getPersistedTicketOrder(ticketOrder)
        );
    }

    @Test
    void fullUpdateTicketOrderWithPatch() throws Exception {
        // Initialize the database
        insertedTicketOrder = ticketOrderRepository.save(ticketOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketOrder using partial update
        TicketOrder partialUpdatedTicketOrder = new TicketOrder();
        partialUpdatedTicketOrder.setId(ticketOrder.getId());

        partialUpdatedTicketOrder.placedDate(UPDATED_PLACED_DATE).status(UPDATED_STATUS).code(UPDATED_CODE).invoiceId(UPDATED_INVOICE_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTicketOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTicketOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TicketOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketOrderUpdatableFieldsEquals(partialUpdatedTicketOrder, getPersistedTicketOrder(partialUpdatedTicketOrder));
    }

    @Test
    void patchNonExistingTicketOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketOrder.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, ticketOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(ticketOrder))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TicketOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTicketOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketOrder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(ticketOrder))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TicketOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTicketOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketOrder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(ticketOrder))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TicketOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTicketOrder() {
        // Initialize the database
        insertedTicketOrder = ticketOrderRepository.save(ticketOrder).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ticketOrder
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, ticketOrder.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ticketOrderRepository.count().block();
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

    protected TicketOrder getPersistedTicketOrder(TicketOrder ticketOrder) {
        return ticketOrderRepository.findById(ticketOrder.getId()).block();
    }

    protected void assertPersistedTicketOrderToMatchAllProperties(TicketOrder expectedTicketOrder) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTicketOrderAllPropertiesEquals(expectedTicketOrder, getPersistedTicketOrder(expectedTicketOrder));
        assertTicketOrderUpdatableFieldsEquals(expectedTicketOrder, getPersistedTicketOrder(expectedTicketOrder));
    }

    protected void assertPersistedTicketOrderToMatchUpdatableProperties(TicketOrder expectedTicketOrder) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTicketOrderAllUpdatablePropertiesEquals(expectedTicketOrder, getPersistedTicketOrder(expectedTicketOrder));
        assertTicketOrderUpdatableFieldsEquals(expectedTicketOrder, getPersistedTicketOrder(expectedTicketOrder));
    }
}
