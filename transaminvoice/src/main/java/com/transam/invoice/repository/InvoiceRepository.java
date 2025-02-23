package com.transam.invoice.repository;

import com.transam.invoice.domain.Invoice;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Invoice entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InvoiceRepository extends ReactiveCrudRepository<Invoice, UUID>, InvoiceRepositoryInternal {
    @Query("SELECT * FROM invoice entity WHERE entity.user_id = :id")
    Flux<Invoice> findByUser(UUID id);

    @Query("SELECT * FROM invoice entity WHERE entity.user_id IS NULL")
    Flux<Invoice> findAllWhereUserIsNull();

    @Override
    <S extends Invoice> Mono<S> save(S entity);

    @Override
    Flux<Invoice> findAll();

    @Override
    Mono<Invoice> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface InvoiceRepositoryInternal {
    <S extends Invoice> Mono<S> save(S entity);

    Flux<Invoice> findAllBy(Pageable pageable);

    Flux<Invoice> findAll();

    Mono<Invoice> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Invoice> findAllBy(Pageable pageable, Criteria criteria);
}
