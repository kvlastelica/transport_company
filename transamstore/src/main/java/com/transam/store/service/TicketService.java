package com.transam.store.service;

import com.transam.store.domain.Ticket;
import com.transam.store.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.transam.store.domain.Ticket}.
 */
@Service
@Transactional
public class TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Save a ticket.
     *
     * @param ticket the entity to save.
     * @return the persisted entity.
     */
    public Mono<Ticket> save(Ticket ticket) {
        LOG.debug("Request to save Ticket : {}", ticket);
        return ticketRepository.save(ticket);
    }

    /**
     * Update a ticket.
     *
     * @param ticket the entity to save.
     * @return the persisted entity.
     */
    public Mono<Ticket> update(Ticket ticket) {
        LOG.debug("Request to update Ticket : {}", ticket);
        return ticketRepository.save(ticket);
    }

    /**
     * Partially update a ticket.
     *
     * @param ticket the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Ticket> partialUpdate(Ticket ticket) {
        LOG.debug("Request to partially update Ticket : {}", ticket);

        return ticketRepository
            .findById(ticket.getId())
            .map(existingTicket -> {
                if (ticket.getName() != null) {
                    existingTicket.setName(ticket.getName());
                }
                if (ticket.getDescription() != null) {
                    existingTicket.setDescription(ticket.getDescription());
                }
                if (ticket.getPrice() != null) {
                    existingTicket.setPrice(ticket.getPrice());
                }
                if (ticket.getProductSize() != null) {
                    existingTicket.setProductSize(ticket.getProductSize());
                }
                if (ticket.getImage() != null) {
                    existingTicket.setImage(ticket.getImage());
                }
                if (ticket.getImageContentType() != null) {
                    existingTicket.setImageContentType(ticket.getImageContentType());
                }

                return existingTicket;
            })
            .flatMap(ticketRepository::save);
    }

    /**
     * Get all the tickets.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Ticket> findAll(Pageable pageable) {
        LOG.debug("Request to get all Tickets");
        return ticketRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of tickets available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return ticketRepository.count();
    }

    /**
     * Get one ticket by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Ticket> findOne(Long id) {
        LOG.debug("Request to get Ticket : {}", id);
        return ticketRepository.findById(id);
    }

    /**
     * Delete the ticket by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Ticket : {}", id);
        return ticketRepository.deleteById(id);
    }
}
