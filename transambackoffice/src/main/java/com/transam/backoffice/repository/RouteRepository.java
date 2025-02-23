package com.transam.backoffice.repository;

import com.transam.backoffice.domain.Route;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Route entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RouteRepository extends ReactiveCrudRepository<Route, Long>, RouteRepositoryInternal {
    @Override
    Mono<Route> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Route> findAllWithEagerRelationships();

    @Override
    Flux<Route> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM route entity JOIN rel_route__employee joinTable ON entity.id = joinTable.employee_id WHERE joinTable.employee_id = :id"
    )
    Flux<Route> findByEmployee(Long id);

    @Query(
        "SELECT entity.* FROM route entity JOIN rel_route__vehicle joinTable ON entity.id = joinTable.vehicle_id WHERE joinTable.vehicle_id = :id"
    )
    Flux<Route> findByVehicle(Long id);

    @Override
    <S extends Route> Mono<S> save(S entity);

    @Override
    Flux<Route> findAll();

    @Override
    Mono<Route> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RouteRepositoryInternal {
    <S extends Route> Mono<S> save(S entity);

    Flux<Route> findAllBy(Pageable pageable);

    Flux<Route> findAll();

    Mono<Route> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Route> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Route> findOneWithEagerRelationships(Long id);

    Flux<Route> findAllWithEagerRelationships();

    Flux<Route> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
