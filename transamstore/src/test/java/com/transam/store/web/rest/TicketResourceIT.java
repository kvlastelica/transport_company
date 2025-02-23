package com.transam.store.web.rest;

import static com.transam.store.domain.TicketAsserts.*;
import static com.transam.store.web.rest.TestUtil.createUpdateProxyForBean;
import static com.transam.store.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transam.store.IntegrationTest;
import com.transam.store.domain.Ticket;
import com.transam.store.domain.enumeration.TicketType;
import com.transam.store.repository.EntityManager;
import com.transam.store.repository.TicketRepository;
import java.math.BigDecimal;
import java.util.Base64;
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
 * Integration tests for the {@link TicketResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TicketResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);

    private static final TicketType DEFAULT_PRODUCT_SIZE = TicketType.PASSENGER;
    private static final TicketType UPDATED_PRODUCT_SIZE = TicketType.PASSENGER_KID;

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/tickets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Ticket ticket;

    private Ticket insertedTicket;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ticket createEntity() {
        return new Ticket()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .productSize(DEFAULT_PRODUCT_SIZE)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ticket createUpdatedEntity() {
        return new Ticket()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .productSize(UPDATED_PRODUCT_SIZE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Ticket.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        ticket = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTicket != null) {
            ticketRepository.delete(insertedTicket).block();
            insertedTicket = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTicket() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Ticket
        var returnedTicket = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticket))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Ticket.class)
            .returnResult()
            .getResponseBody();

        // Validate the Ticket in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTicketUpdatableFieldsEquals(returnedTicket, getPersistedTicket(returnedTicket));

        insertedTicket = returnedTicket;
    }

    @Test
    void createTicketWithExistingId() throws Exception {
        // Create the Ticket with an existing ID
        ticket.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticket))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticket.setName(null);

        // Create the Ticket, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticket))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticket.setPrice(null);

        // Create the Ticket, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticket))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkProductSizeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticket.setProductSize(null);

        // Create the Ticket, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticket))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTickets() {
        // Initialize the database
        insertedTicket = ticketRepository.save(ticket).block();

        // Get all the ticketList
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
            .value(hasItem(ticket.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].productSize")
            .value(hasItem(DEFAULT_PRODUCT_SIZE.toString()))
            .jsonPath("$.[*].imageContentType")
            .value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.[*].image")
            .value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    void getTicket() {
        // Initialize the database
        insertedTicket = ticketRepository.save(ticket).block();

        // Get the ticket
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, ticket.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(ticket.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.price")
            .value(is(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.productSize")
            .value(is(DEFAULT_PRODUCT_SIZE.toString()))
            .jsonPath("$.imageContentType")
            .value(is(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.image")
            .value(is(Base64.getEncoder().encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    void getNonExistingTicket() {
        // Get the ticket
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTicket() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.save(ticket).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticket
        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).block();
        updatedTicket
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .productSize(UPDATED_PRODUCT_SIZE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTicket.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedTicket))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTicketToMatchAllProperties(updatedTicket);
    }

    @Test
    void putNonExistingTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticket.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ticket.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticket))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticket.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticket))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticket.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(ticket))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTicketWithPatch() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.save(ticket).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticket using partial update
        Ticket partialUpdatedTicket = new Ticket();
        partialUpdatedTicket.setId(ticket.getId());

        partialUpdatedTicket.productSize(UPDATED_PRODUCT_SIZE).image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTicket.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTicket))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ticket in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTicket, ticket), getPersistedTicket(ticket));
    }

    @Test
    void fullUpdateTicketWithPatch() throws Exception {
        // Initialize the database
        insertedTicket = ticketRepository.save(ticket).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticket using partial update
        Ticket partialUpdatedTicket = new Ticket();
        partialUpdatedTicket.setId(ticket.getId());

        partialUpdatedTicket
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .productSize(UPDATED_PRODUCT_SIZE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTicket.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTicket))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ticket in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketUpdatableFieldsEquals(partialUpdatedTicket, getPersistedTicket(partialUpdatedTicket));
    }

    @Test
    void patchNonExistingTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticket.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, ticket.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(ticket))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticket.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(ticket))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTicket() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticket.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(ticket))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Ticket in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTicket() {
        // Initialize the database
        insertedTicket = ticketRepository.save(ticket).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ticket
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, ticket.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ticketRepository.count().block();
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

    protected Ticket getPersistedTicket(Ticket ticket) {
        return ticketRepository.findById(ticket.getId()).block();
    }

    protected void assertPersistedTicketToMatchAllProperties(Ticket expectedTicket) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTicketAllPropertiesEquals(expectedTicket, getPersistedTicket(expectedTicket));
        assertTicketUpdatableFieldsEquals(expectedTicket, getPersistedTicket(expectedTicket));
    }

    protected void assertPersistedTicketToMatchUpdatableProperties(Ticket expectedTicket) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTicketAllUpdatablePropertiesEquals(expectedTicket, getPersistedTicket(expectedTicket));
        assertTicketUpdatableFieldsEquals(expectedTicket, getPersistedTicket(expectedTicket));
    }
}
