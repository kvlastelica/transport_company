package com.transam.backoffice.repository;

import com.transam.backoffice.domain.RouteSchedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the RouteSchedule entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RouteScheduleRepository extends ReactiveCrudRepository<RouteSchedule, Long>, RouteScheduleRepositoryInternal {
    @Override
    Mono<RouteSchedule> findOneWithEagerRelationships(Long id);

    @Override
    Flux<RouteSchedule> findAllWithEagerRelationships();

    @Override
    Flux<RouteSchedule> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM route_schedule entity WHERE entity.route_id = :id")
    Flux<RouteSchedule> findByRoute(Long id);

    @Query("SELECT * FROM route_schedule entity WHERE entity.route_id IS NULL")
    Flux<RouteSchedule> findAllWhereRouteIsNull();

    @Override
    <S extends RouteSchedule> Mono<S> save(S entity);

    @Override
    Flux<RouteSchedule> findAll();

    @Override
    Mono<RouteSchedule> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RouteScheduleRepositoryInternal {
    <S extends RouteSchedule> Mono<S> save(S entity);

    Flux<RouteSchedule> findAllBy(Pageable pageable);

    Flux<RouteSchedule> findAll();

    Mono<RouteSchedule> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<RouteSchedule> findAllBy(Pageable pageable, Criteria criteria);

    Mono<RouteSchedule> findOneWithEagerRelationships(Long id);

    Flux<RouteSchedule> findAllWithEagerRelationships();

    Flux<RouteSchedule> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
