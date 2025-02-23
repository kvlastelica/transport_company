package com.transam.notification.repository;

import com.transam.notification.domain.Notification;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {}
