package com.transam.invoice.web.rest;

import static com.transam.invoice.domain.InvoiceAsserts.*;
import static com.transam.invoice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.transam.invoice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transam.invoice.IntegrationTest;
import com.transam.invoice.domain.Invoice;
import com.transam.invoice.domain.enumeration.InvoiceStatus;
import com.transam.invoice.domain.enumeration.PaymentMethod;
import com.transam.invoice.repository.EntityManager;
import com.transam.invoice.repository.InvoiceRepository;
import com.transam.invoice.repository.UserRepository;
import java.math.BigDecimal;
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
 * Integration tests for the {@link InvoiceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InvoiceResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_TICKET_ORDER_CODE = "AAAAAAAAAA";
    private static final String UPDATED_TICKET_ORDER_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final InvoiceStatus DEFAULT_STATUS = InvoiceStatus.PAID;
    private static final InvoiceStatus UPDATED_STATUS = InvoiceStatus.ISSUED;

    private static final PaymentMethod DEFAULT_PAYMENT_METHOD = PaymentMethod.CREDIT_CARD;
    private static final PaymentMethod UPDATED_PAYMENT_METHOD = PaymentMethod.CASH_ON_DELIVERY;

    private static final Instant DEFAULT_PAYMENT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PAYMENT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_PAYMENT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PAYMENT_AMOUNT = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/invoices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Invoice invoice;

    private Invoice insertedInvoice;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoice createEntity() {
        return new Invoice()
            .id(UUID.randomUUID())
            .code(DEFAULT_CODE)
            .ticketOrderCode(DEFAULT_TICKET_ORDER_CODE)
            .date(DEFAULT_DATE)
            .details(DEFAULT_DETAILS)
            .status(DEFAULT_STATUS)
            .paymentMethod(DEFAULT_PAYMENT_METHOD)
            .paymentDate(DEFAULT_PAYMENT_DATE)
            .paymentAmount(DEFAULT_PAYMENT_AMOUNT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoice createUpdatedEntity() {
        return new Invoice()
            .id(UUID.randomUUID())
            .code(UPDATED_CODE)
            .ticketOrderCode(UPDATED_TICKET_ORDER_CODE)
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS)
            .status(UPDATED_STATUS)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .paymentAmount(UPDATED_PAYMENT_AMOUNT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Invoice.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        invoice = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedInvoice != null) {
            invoiceRepository.delete(insertedInvoice).block();
            insertedInvoice = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createInvoice() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        invoice.setId(null);
        // Create the Invoice
        var returnedInvoice = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Invoice.class)
            .returnResult()
            .getResponseBody();

        // Validate the Invoice in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInvoiceUpdatableFieldsEquals(returnedInvoice, getPersistedInvoice(returnedInvoice));

        insertedInvoice = returnedInvoice;
    }

    @Test
    void createInvoiceWithExistingId() throws Exception {
        // Create the Invoice with an existing ID
        insertedInvoice = invoiceRepository.save(invoice).block();

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoice.setCode(null);

        // Create the Invoice, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTicketOrderCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoice.setTicketOrderCode(null);

        // Create the Invoice, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoice.setDate(null);

        // Create the Invoice, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoice.setStatus(null);

        // Create the Invoice, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPaymentMethodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoice.setPaymentMethod(null);

        // Create the Invoice, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPaymentDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoice.setPaymentDate(null);

        // Create the Invoice, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPaymentAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoice.setPaymentAmount(null);

        // Create the Invoice, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllInvoicesAsStream() {
        // Initialize the database
        invoice.setId(UUID.randomUUID());
        invoiceRepository.save(invoice).block();

        List<Invoice> invoiceList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Invoice.class)
            .getResponseBody()
            .filter(invoice::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(invoiceList).isNotNull();
        assertThat(invoiceList).hasSize(1);
        Invoice testInvoice = invoiceList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertInvoiceAllPropertiesEquals(invoice, testInvoice);
        assertInvoiceUpdatableFieldsEquals(invoice, testInvoice);
    }

    @Test
    void getAllInvoices() {
        // Initialize the database
        invoice.setId(UUID.randomUUID());
        insertedInvoice = invoiceRepository.save(invoice).block();

        // Get all the invoiceList
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
            .value(hasItem(invoice.getId().toString()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].ticketOrderCode")
            .value(hasItem(DEFAULT_TICKET_ORDER_CODE))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].details")
            .value(hasItem(DEFAULT_DETAILS))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].paymentMethod")
            .value(hasItem(DEFAULT_PAYMENT_METHOD.toString()))
            .jsonPath("$.[*].paymentDate")
            .value(hasItem(DEFAULT_PAYMENT_DATE.toString()))
            .jsonPath("$.[*].paymentAmount")
            .value(hasItem(sameNumber(DEFAULT_PAYMENT_AMOUNT)));
    }

    @Test
    void getInvoice() {
        // Initialize the database
        invoice.setId(UUID.randomUUID());
        insertedInvoice = invoiceRepository.save(invoice).block();

        // Get the invoice
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, invoice.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(invoice.getId().toString()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.ticketOrderCode")
            .value(is(DEFAULT_TICKET_ORDER_CODE))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()))
            .jsonPath("$.details")
            .value(is(DEFAULT_DETAILS))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.paymentMethod")
            .value(is(DEFAULT_PAYMENT_METHOD.toString()))
            .jsonPath("$.paymentDate")
            .value(is(DEFAULT_PAYMENT_DATE.toString()))
            .jsonPath("$.paymentAmount")
            .value(is(sameNumber(DEFAULT_PAYMENT_AMOUNT)));
    }

    @Test
    void getNonExistingInvoice() {
        // Get the invoice
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingInvoice() throws Exception {
        // Initialize the database
        invoice.setId(UUID.randomUUID());
        insertedInvoice = invoiceRepository.save(invoice).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoice
        Invoice updatedInvoice = invoiceRepository.findById(invoice.getId()).block();
        updatedInvoice
            .code(UPDATED_CODE)
            .ticketOrderCode(UPDATED_TICKET_ORDER_CODE)
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS)
            .status(UPDATED_STATUS)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .paymentAmount(UPDATED_PAYMENT_AMOUNT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedInvoice.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedInvoice))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInvoiceToMatchAllProperties(updatedInvoice);
    }

    @Test
    void putNonExistingInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, invoice.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInvoiceWithPatch() throws Exception {
        // Initialize the database
        invoice.setId(UUID.randomUUID());
        insertedInvoice = invoiceRepository.save(invoice).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoice using partial update
        Invoice partialUpdatedInvoice = new Invoice();
        partialUpdatedInvoice.setId(invoice.getId());

        partialUpdatedInvoice.details(UPDATED_DETAILS).status(UPDATED_STATUS).paymentDate(UPDATED_PAYMENT_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInvoice.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInvoice))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Invoice in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvoiceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedInvoice, invoice), getPersistedInvoice(invoice));
    }

    @Test
    void fullUpdateInvoiceWithPatch() throws Exception {
        // Initialize the database
        invoice.setId(UUID.randomUUID());
        insertedInvoice = invoiceRepository.save(invoice).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoice using partial update
        Invoice partialUpdatedInvoice = new Invoice();
        partialUpdatedInvoice.setId(invoice.getId());

        partialUpdatedInvoice
            .code(UPDATED_CODE)
            .ticketOrderCode(UPDATED_TICKET_ORDER_CODE)
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS)
            .status(UPDATED_STATUS)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .paymentAmount(UPDATED_PAYMENT_AMOUNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInvoice.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInvoice))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Invoice in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvoiceUpdatableFieldsEquals(partialUpdatedInvoice, getPersistedInvoice(partialUpdatedInvoice));
    }

    @Test
    void patchNonExistingInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, invoice.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(invoice))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInvoice() {
        // Initialize the database
        invoice.setId(UUID.randomUUID());
        insertedInvoice = invoiceRepository.save(invoice).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the invoice
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, invoice.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return invoiceRepository.count().block();
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

    protected Invoice getPersistedInvoice(Invoice invoice) {
        return invoiceRepository.findById(invoice.getId()).block();
    }

    protected void assertPersistedInvoiceToMatchAllProperties(Invoice expectedInvoice) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInvoiceAllPropertiesEquals(expectedInvoice, getPersistedInvoice(expectedInvoice));
        assertInvoiceUpdatableFieldsEquals(expectedInvoice, getPersistedInvoice(expectedInvoice));
    }

    protected void assertPersistedInvoiceToMatchUpdatableProperties(Invoice expectedInvoice) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInvoiceAllUpdatablePropertiesEquals(expectedInvoice, getPersistedInvoice(expectedInvoice));
        assertInvoiceUpdatableFieldsEquals(expectedInvoice, getPersistedInvoice(expectedInvoice));
    }
}
