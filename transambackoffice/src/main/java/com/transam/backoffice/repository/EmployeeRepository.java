package com.transam.backoffice.repository;

import com.transam.backoffice.domain.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Employee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long>, EmployeeRepositoryInternal {
    @Override
    Mono<Employee> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Employee> findAllWithEagerRelationships();

    @Override
    Flux<Employee> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM employee entity WHERE entity.user_id = :id")
    Flux<Employee> findByUser(Long id);

    @Query("SELECT * FROM employee entity WHERE entity.user_id IS NULL")
    Flux<Employee> findAllWhereUserIsNull();

    @Query("SELECT * FROM employee entity WHERE entity.department_id = :id")
    Flux<Employee> findByDepartment(Long id);

    @Query("SELECT * FROM employee entity WHERE entity.department_id IS NULL")
    Flux<Employee> findAllWhereDepartmentIsNull();

    @Override
    <S extends Employee> Mono<S> save(S entity);

    @Override
    Flux<Employee> findAll();

    @Override
    Mono<Employee> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EmployeeRepositoryInternal {
    <S extends Employee> Mono<S> save(S entity);

    Flux<Employee> findAllBy(Pageable pageable);

    Flux<Employee> findAll();

    Mono<Employee> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Employee> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Employee> findOneWithEagerRelationships(Long id);

    Flux<Employee> findAllWithEagerRelationships();

    Flux<Employee> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
