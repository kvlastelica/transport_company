package com.transam.store.repository;

import com.transam.store.domain.TicketOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TicketOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketOrderRepository extends ReactiveCrudRepository<TicketOrder, Long>, TicketOrderRepositoryInternal {
    Flux<TicketOrder> findAllBy(Pageable pageable);

    @Override
    Mono<TicketOrder> findOneWithEagerRelationships(Long id);

    @Override
    Flux<TicketOrder> findAllWithEagerRelationships();

    @Override
    Flux<TicketOrder> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM ticket_order entity WHERE entity.customer_id = :id")
    Flux<TicketOrder> findByCustomer(Long id);

    @Query("SELECT * FROM ticket_order entity WHERE entity.customer_id IS NULL")
    Flux<TicketOrder> findAllWhereCustomerIsNull();

    @Override
    <S extends TicketOrder> Mono<S> save(S entity);

    @Override
    Flux<TicketOrder> findAll();

    @Override
    Mono<TicketOrder> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TicketOrderRepositoryInternal {
    <S extends TicketOrder> Mono<S> save(S entity);

    Flux<TicketOrder> findAllBy(Pageable pageable);

    Flux<TicketOrder> findAll();

    Mono<TicketOrder> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TicketOrder> findAllBy(Pageable pageable, Criteria criteria);

    Mono<TicketOrder> findOneWithEagerRelationships(Long id);

    Flux<TicketOrder> findAllWithEagerRelationships();

    Flux<TicketOrder> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
