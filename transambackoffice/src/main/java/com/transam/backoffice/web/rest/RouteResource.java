package com.transam.backoffice.web.rest;

import com.transam.backoffice.domain.Route;
import com.transam.backoffice.repository.RouteRepository;
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
 * REST controller for managing {@link com.transam.backoffice.domain.Route}.
 */
@RestController
@RequestMapping("/api/routes")
@Transactional
public class RouteResource {

    private static final Logger LOG = LoggerFactory.getLogger(RouteResource.class);

    private static final String ENTITY_NAME = "route";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RouteRepository routeRepository;

    public RouteResource(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    /**
     * {@code POST  /routes} : Create a new route.
     *
     * @param route the route to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new route, or with status {@code 400 (Bad Request)} if the route has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Route>> createRoute(@Valid @RequestBody Route route) throws URISyntaxException {
        LOG.debug("REST request to save Route : {}", route);
        if (route.getId() != null) {
            throw new BadRequestAlertException("A new route cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return routeRepository
            .save(route)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/routes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /routes/:id} : Updates an existing route.
     *
     * @param id the id of the route to save.
     * @param route the route to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated route,
     * or with status {@code 400 (Bad Request)} if the route is not valid,
     * or with status {@code 500 (Internal Server Error)} if the route couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Route>> updateRoute(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Route route
    ) throws URISyntaxException {
        LOG.debug("REST request to update Route : {}, {}", id, route);
        if (route.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, route.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return routeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return routeRepository
                    .save(route)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /routes/:id} : Partial updates given fields of an existing route, field will ignore if it is null
     *
     * @param id the id of the route to save.
     * @param route the route to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated route,
     * or with status {@code 400 (Bad Request)} if the route is not valid,
     * or with status {@code 404 (Not Found)} if the route is not found,
     * or with status {@code 500 (Internal Server Error)} if the route couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Route>> partialUpdateRoute(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Route route
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Route partially : {}, {}", id, route);
        if (route.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, route.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return routeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Route> result = routeRepository
                    .findById(route.getId())
                    .map(existingRoute -> {
                        if (route.getCode() != null) {
                            existingRoute.setCode(route.getCode());
                        }
                        if (route.getDescription() != null) {
                            existingRoute.setDescription(route.getDescription());
                        }
                        if (route.getStart() != null) {
                            existingRoute.setStart(route.getStart());
                        }
                        if (route.getDestination() != null) {
                            existingRoute.setDestination(route.getDestination());
                        }
                        if (route.getPassengerCapacity() != null) {
                            existingRoute.setPassengerCapacity(route.getPassengerCapacity());
                        }
                        if (route.getParcelTotalWeight() != null) {
                            existingRoute.setParcelTotalWeight(route.getParcelTotalWeight());
                        }

                        return existingRoute;
                    })
                    .flatMap(routeRepository::save);

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
     * {@code GET  /routes} : get all the routes.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of routes in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Route>> getAllRoutes(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all Routes");
        if (eagerload) {
            return routeRepository.findAllWithEagerRelationships().collectList();
        } else {
            return routeRepository.findAll().collectList();
        }
    }

    /**
     * {@code GET  /routes} : get all the routes as a stream.
     * @return the {@link Flux} of routes.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Route> getAllRoutesAsStream() {
        LOG.debug("REST request to get all Routes as a stream");
        return routeRepository.findAll();
    }

    /**
     * {@code GET  /routes/:id} : get the "id" route.
     *
     * @param id the id of the route to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the route, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Route>> getRoute(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Route : {}", id);
        Mono<Route> route = routeRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(route);
    }

    /**
     * {@code DELETE  /routes/:id} : delete the "id" route.
     *
     * @param id the id of the route to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRoute(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Route : {}", id);
        return routeRepository
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
