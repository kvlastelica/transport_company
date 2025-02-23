package com.transam.store.service;

import com.transam.store.domain.TicketOrder;
import com.transam.store.repository.TicketOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.transam.store.domain.TicketOrder}.
 */
@Service
@Transactional
public class TicketOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketOrderService.class);

    private final TicketOrderRepository ticketOrderRepository;

    public TicketOrderService(TicketOrderRepository ticketOrderRepository) {
        this.ticketOrderRepository = ticketOrderRepository;
    }

    /**
     * Save a ticketOrder.
     *
     * @param ticketOrder the entity to save.
     * @return the persisted entity.
     */
    public Mono<TicketOrder> save(TicketOrder ticketOrder) {
        LOG.debug("Request to save TicketOrder : {}", ticketOrder);
        return ticketOrderRepository.save(ticketOrder);
    }

    /**
     * Update a ticketOrder.
     *
     * @param ticketOrder the entity to save.
     * @return the persisted entity.
     */
    public Mono<TicketOrder> update(TicketOrder ticketOrder) {
        LOG.debug("Request to update TicketOrder : {}", ticketOrder);
        return ticketOrderRepository.save(ticketOrder);
    }

    /**
     * Partially update a ticketOrder.
     *
     * @param ticketOrder the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<TicketOrder> partialUpdate(TicketOrder ticketOrder) {
        LOG.debug("Request to partially update TicketOrder : {}", ticketOrder);

        return ticketOrderRepository
            .findById(ticketOrder.getId())
            .map(existingTicketOrder -> {
                if (ticketOrder.getPlacedDate() != null) {
                    existingTicketOrder.setPlacedDate(ticketOrder.getPlacedDate());
                }
                if (ticketOrder.getStatus() != null) {
                    existingTicketOrder.setStatus(ticketOrder.getStatus());
                }
                if (ticketOrder.getCode() != null) {
                    existingTicketOrder.setCode(ticketOrder.getCode());
                }
                if (ticketOrder.getInvoiceId() != null) {
                    existingTicketOrder.setInvoiceId(ticketOrder.getInvoiceId());
                }

                return existingTicketOrder;
            })
            .flatMap(ticketOrderRepository::save);
    }

    /**
     * Get all the ticketOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TicketOrder> findAll(Pageable pageable) {
        LOG.debug("Request to get all TicketOrders");
        return ticketOrderRepository.findAllBy(pageable);
    }

    /**
     * Get all the ticketOrders with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<TicketOrder> findAllWithEagerRelationships(Pageable pageable) {
        return ticketOrderRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Returns the number of ticketOrders available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return ticketOrderRepository.count();
    }

    /**
     * Get one ticketOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<TicketOrder> findOne(Long id) {
        LOG.debug("Request to get TicketOrder : {}", id);
        return ticketOrderRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the ticketOrder by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete TicketOrder : {}", id);
        return ticketOrderRepository.deleteById(id);
    }
}
