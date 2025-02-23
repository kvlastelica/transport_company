package com.transam.invoice.web.rest;

import com.transam.invoice.domain.Invoice;
import com.transam.invoice.repository.InvoiceRepository;
import com.transam.invoice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.transam.invoice.domain.Invoice}.
 */
@RestController
@RequestMapping("/api/invoices")
@Transactional
public class InvoiceResource {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceResource.class);

    private static final String ENTITY_NAME = "invoice";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InvoiceRepository invoiceRepository;

    public InvoiceResource(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * {@code POST  /invoices} : Create a new invoice.
     *
     * @param invoice the invoice to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new invoice, or with status {@code 400 (Bad Request)} if the invoice has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Invoice>> createInvoice(@Valid @RequestBody Invoice invoice) throws URISyntaxException {
        LOG.debug("REST request to save Invoice : {}", invoice);
        if (invoice.getId() != null) {
            throw new BadRequestAlertException("A new invoice cannot already have an ID", ENTITY_NAME, "idexists");
        }
        invoice.setId(UUID.randomUUID());
        return invoiceRepository
            .save(invoice)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/invoices/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /invoices/:id} : Updates an existing invoice.
     *
     * @param id the id of the invoice to save.
     * @param invoice the invoice to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated invoice,
     * or with status {@code 400 (Bad Request)} if the invoice is not valid,
     * or with status {@code 500 (Internal Server Error)} if the invoice couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Invoice>> updateInvoice(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody Invoice invoice
    ) throws URISyntaxException {
        LOG.debug("REST request to update Invoice : {}, {}", id, invoice);
        if (invoice.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, invoice.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return invoiceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return invoiceRepository
                    .save(invoice.setIsPersisted())
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /invoices/:id} : Partial updates given fields of an existing invoice, field will ignore if it is null
     *
     * @param id the id of the invoice to save.
     * @param invoice the invoice to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated invoice,
     * or with status {@code 400 (Bad Request)} if the invoice is not valid,
     * or with status {@code 404 (Not Found)} if the invoice is not found,
     * or with status {@code 500 (Internal Server Error)} if the invoice couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Invoice>> partialUpdateInvoice(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody Invoice invoice
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Invoice partially : {}, {}", id, invoice);
        if (invoice.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, invoice.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return invoiceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Invoice> result = invoiceRepository
                    .findById(invoice.getId())
                    .map(existingInvoice -> {
                        if (invoice.getCode() != null) {
                            existingInvoice.setCode(invoice.getCode());
                        }
                        if (invoice.getTicketOrderCode() != null) {
                            existingInvoice.setTicketOrderCode(invoice.getTicketOrderCode());
                        }
                        if (invoice.getDate() != null) {
                            existingInvoice.setDate(invoice.getDate());
                        }
                        if (invoice.getDetails() != null) {
                            existingInvoice.setDetails(invoice.getDetails());
                        }
                        if (invoice.getStatus() != null) {
                            existingInvoice.setStatus(invoice.getStatus());
                        }
                        if (invoice.getPaymentMethod() != null) {
                            existingInvoice.setPaymentMethod(invoice.getPaymentMethod());
                        }
                        if (invoice.getPaymentDate() != null) {
                            existingInvoice.setPaymentDate(invoice.getPaymentDate());
                        }
                        if (invoice.getPaymentAmount() != null) {
                            existingInvoice.setPaymentAmount(invoice.getPaymentAmount());
                        }

                        return existingInvoice;
                    })
                    .flatMap(invoiceRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /invoices} : get all the invoices.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of invoices in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Invoice>> getAllInvoices() {
        LOG.debug("REST request to get all Invoices");
        return invoiceRepository.findAll().collectList();
    }

    /**
     * {@code GET  /invoices} : get all the invoices as a stream.
     * @return the {@link Flux} of invoices.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Invoice> getAllInvoicesAsStream() {
        LOG.debug("REST request to get all Invoices as a stream");
        return invoiceRepository.findAll();
    }

    /**
     * {@code GET  /invoices/:id} : get the "id" invoice.
     *
     * @param id the id of the invoice to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the invoice, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Invoice>> getInvoice(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get Invoice : {}", id);
        Mono<Invoice> invoice = invoiceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(invoice);
    }

    /**
     * {@code DELETE  /invoices/:id} : delete the "id" invoice.
     *
     * @param id the id of the invoice to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteInvoice(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete Invoice : {}", id);
        return invoiceRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
