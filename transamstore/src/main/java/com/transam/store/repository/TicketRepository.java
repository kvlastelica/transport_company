package com.transam.store.repository;

import com.transam.store.domain.Ticket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Ticket entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketRepository extends ReactiveCrudRepository<Ticket, Long>, TicketRepositoryInternal {
    Flux<Ticket> findAllBy(Pageable pageable);

    @Override
    <S extends Ticket> Mono<S> save(S entity);

    @Override
    Flux<Ticket> findAll();

    @Override
    Mono<Ticket> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TicketRepositoryInternal {
    <S extends Ticket> Mono<S> save(S entity);

    Flux<Ticket> findAllBy(Pageable pageable);

    Flux<Ticket> findAll();

    Mono<Ticket> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Ticket> findAllBy(Pageable pageable, Criteria criteria);
}
