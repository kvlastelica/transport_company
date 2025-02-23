package com.transam.store.web.rest;

import com.transam.store.domain.TicketOrder;
import com.transam.store.repository.TicketOrderRepository;
import com.transam.store.service.TicketOrderService;
import com.transam.store.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.transam.store.domain.TicketOrder}.
 */
@RestController
@RequestMapping("/api/ticket-orders")
public class TicketOrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(TicketOrderResource.class);

    private static final String ENTITY_NAME = "ticketOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TicketOrderService ticketOrderService;

    private final TicketOrderRepository ticketOrderRepository;

    public TicketOrderResource(TicketOrderService ticketOrderService, TicketOrderRepository ticketOrderRepository) {
        this.ticketOrderService = ticketOrderService;
        this.ticketOrderRepository = ticketOrderRepository;
    }

    /**
     * {@code POST  /ticket-orders} : Create a new ticketOrder.
     *
     * @param ticketOrder the ticketOrder to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ticketOrder, or with status {@code 400 (Bad Request)} if the ticketOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TicketOrder>> createTicketOrder(@Valid @RequestBody TicketOrder ticketOrder) throws URISyntaxException {
        LOG.debug("REST request to save TicketOrder : {}", ticketOrder);
        if (ticketOrder.getId() != null) {
            throw new BadRequestAlertException("A new ticketOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return ticketOrderService
            .save(ticketOrder)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/ticket-orders/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /ticket-orders/:id} : Updates an existing ticketOrder.
     *
     * @param id the id of the ticketOrder to save.
     * @param ticketOrder the ticketOrder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketOrder,
     * or with status {@code 400 (Bad Request)} if the ticketOrder is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ticketOrder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TicketOrder>> updateTicketOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TicketOrder ticketOrder
    ) throws URISyntaxException {
        LOG.debug("REST request to update TicketOrder : {}, {}", id, ticketOrder);
        if (ticketOrder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketOrder.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ticketOrderRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return ticketOrderService
                    .update(ticketOrder)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /ticket-orders/:id} : Partial updates given fields of an existing ticketOrder, field will ignore if it is null
     *
     * @param id the id of the ticketOrder to save.
     * @param ticketOrder the ticketOrder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketOrder,
     * or with status {@code 400 (Bad Request)} if the ticketOrder is not valid,
     * or with status {@code 404 (Not Found)} if the ticketOrder is not found,
     * or with status {@code 500 (Internal Server Error)} if the ticketOrder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TicketOrder>> partialUpdateTicketOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TicketOrder ticketOrder
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TicketOrder partially : {}, {}", id, ticketOrder);
        if (ticketOrder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketOrder.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ticketOrderRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TicketOrder> result = ticketOrderService.partialUpdate(ticketOrder);

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
     * {@code GET  /ticket-orders} : get all the ticketOrders.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ticketOrders in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TicketOrder>>> getAllTicketOrders(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of TicketOrders");
        return ticketOrderService
            .countAll()
            .zipWith(ticketOrderService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /ticket-orders/:id} : get the "id" ticketOrder.
     *
     * @param id the id of the ticketOrder to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ticketOrder, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TicketOrder>> getTicketOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TicketOrder : {}", id);
        Mono<TicketOrder> ticketOrder = ticketOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ticketOrder);
    }

    /**
     * {@code DELETE  /ticket-orders/:id} : delete the "id" ticketOrder.
     *
     * @param id the id of the ticketOrder to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTicketOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TicketOrder : {}", id);
        return ticketOrderService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
