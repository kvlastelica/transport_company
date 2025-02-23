package com.transam.store.service;

import com.transam.store.domain.OrderItem;
import com.transam.store.repository.OrderItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.transam.store.domain.OrderItem}.
 */
@Service
@Transactional
public class OrderItemService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderItemService.class);

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Save a orderItem.
     *
     * @param orderItem the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrderItem> save(OrderItem orderItem) {
        LOG.debug("Request to save OrderItem : {}", orderItem);
        return orderItemRepository.save(orderItem);
    }

    /**
     * Update a orderItem.
     *
     * @param orderItem the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrderItem> update(OrderItem orderItem) {
        LOG.debug("Request to update OrderItem : {}", orderItem);
        return orderItemRepository.save(orderItem);
    }

    /**
     * Partially update a orderItem.
     *
     * @param orderItem the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OrderItem> partialUpdate(OrderItem orderItem) {
        LOG.debug("Request to partially update OrderItem : {}", orderItem);

        return orderItemRepository
            .findById(orderItem.getId())
            .map(existingOrderItem -> {
                if (orderItem.getQuantity() != null) {
                    existingOrderItem.setQuantity(orderItem.getQuantity());
                }
                if (orderItem.getTotalPrice() != null) {
                    existingOrderItem.setTotalPrice(orderItem.getTotalPrice());
                }

                return existingOrderItem;
            })
            .flatMap(orderItemRepository::save);
    }

    /**
     * Get all the orderItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OrderItem> findAll(Pageable pageable) {
        LOG.debug("Request to get all OrderItems");
        return orderItemRepository.findAllBy(pageable);
    }

    /**
     * Get all the orderItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<OrderItem> findAllWithEagerRelationships(Pageable pageable) {
        return orderItemRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Returns the number of orderItems available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return orderItemRepository.count();
    }

    /**
     * Get one orderItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OrderItem> findOne(Long id) {
        LOG.debug("Request to get OrderItem : {}", id);
        return orderItemRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the orderItem by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete OrderItem : {}", id);
        return orderItemRepository.deleteById(id);
    }
}
