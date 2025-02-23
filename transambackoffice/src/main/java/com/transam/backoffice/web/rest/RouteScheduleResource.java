package com.transam.backoffice.web.rest;

import com.transam.backoffice.domain.RouteSchedule;
import com.transam.backoffice.repository.RouteScheduleRepository;
import com.transam.backoffice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
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
 * REST controller for managing {@link com.transam.backoffice.domain.RouteSchedule}.
 */
@RestController
@RequestMapping("/api/route-schedules")
@Transactional
public class RouteScheduleResource {

    private static final Logger LOG = LoggerFactory.getLogger(RouteScheduleResource.class);

    private static final String ENTITY_NAME = "routeSchedule";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RouteScheduleRepository routeScheduleRepository;

    public RouteScheduleResource(RouteScheduleRepository routeScheduleRepository) {
        this.routeScheduleRepository = routeScheduleRepository;
    }

    /**
     * {@code POST  /route-schedules} : Create a new routeSchedule.
     *
     * @param routeSchedule the routeSchedule to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new routeSchedule, or with status {@code 400 (Bad Request)} if the routeSchedule has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<RouteSchedule>> createRouteSchedule(@Valid @RequestBody RouteSchedule routeSchedule)
        throws URISyntaxException {
        LOG.debug("REST request to save RouteSchedule : {}", routeSchedule);
        if (routeSchedule.getId() != null) {
            throw new BadRequestAlertException("A new routeSchedule cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return routeScheduleRepository
            .save(routeSchedule)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/route-schedules/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /route-schedules/:id} : Updates an existing routeSchedule.
     *
     * @param id the id of the routeSchedule to save.
     * @param routeSchedule the routeSchedule to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated routeSchedule,
     * or with status {@code 400 (Bad Request)} if the routeSchedule is not valid,
     * or with status {@code 500 (Internal Server Error)} if the routeSchedule couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<RouteSchedule>> updateRouteSchedule(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RouteSchedule routeSchedule
    ) throws URISyntaxException {
        LOG.debug("REST request to update RouteSchedule : {}, {}", id, routeSchedule);
        if (routeSchedule.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, routeSchedule.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return routeScheduleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return routeScheduleRepository
                    .save(routeSchedule)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /route-schedules/:id} : Partial updates given fields of an existing routeSchedule, field will ignore if it is null
     *
     * @param id the id of the routeSchedule to save.
     * @param routeSchedule the routeSchedule to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated routeSchedule,
     * or with status {@code 400 (Bad Request)} if the routeSchedule is not valid,
     * or with status {@code 404 (Not Found)} if the routeSchedule is not found,
     * or with status {@code 500 (Internal Server Error)} if the routeSchedule couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RouteSchedule>> partialUpdateRouteSchedule(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RouteSchedule routeSchedule
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RouteSchedule partially : {}, {}", id, routeSchedule);
        if (routeSchedule.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, routeSchedule.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return routeScheduleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RouteSchedule> result = routeScheduleRepository
                    .findById(routeSchedule.getId())
                    .map(existingRouteSchedule -> {
                        if (routeSchedule.getCode() != null) {
                            existingRouteSchedule.setCode(routeSchedule.getCode());
                        }
                        if (routeSchedule.getDescription() != null) {
                            existingRouteSchedule.setDescription(routeSchedule.getDescription());
                        }
                        if (routeSchedule.getDeparture() != null) {
                            existingRouteSchedule.setDeparture(routeSchedule.getDeparture());
                        }
                        if (routeSchedule.getArrival() != null) {
                            existingRouteSchedule.setArrival(routeSchedule.getArrival());
                        }

                        return existingRouteSchedule;
                    })
                    .flatMap(routeScheduleRepository::save);

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
     * {@code GET  /route-schedules} : get all the routeSchedules.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of routeSchedules in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<RouteSchedule>> getAllRouteSchedules(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all RouteSchedules");
        if (eagerload) {
            return routeScheduleRepository.findAllWithEagerRelationships().collectList();
        } else {
            return routeScheduleRepository.findAll().collectList();
        }
    }

    /**
     * {@code GET  /route-schedules} : get all the routeSchedules as a stream.
     * @return the {@link Flux} of routeSchedules.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<RouteSchedule> getAllRouteSchedulesAsStream() {
        LOG.debug("REST request to get all RouteSchedules as a stream");
        return routeScheduleRepository.findAll();
    }

    /**
     * {@code GET  /route-schedules/:id} : get the "id" routeSchedule.
     *
     * @param id the id of the routeSchedule to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the routeSchedule, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<RouteSchedule>> getRouteSchedule(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RouteSchedule : {}", id);
        Mono<RouteSchedule> routeSchedule = routeScheduleRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(routeSchedule);
    }

    /**
     * {@code DELETE  /route-schedules/:id} : delete the "id" routeSchedule.
     *
     * @param id the id of the routeSchedule to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRouteSchedule(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RouteSchedule : {}", id);
        return routeScheduleRepository
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
