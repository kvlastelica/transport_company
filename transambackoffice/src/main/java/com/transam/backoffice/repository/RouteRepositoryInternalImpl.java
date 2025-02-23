package com.transam.backoffice.repository;

import com.transam.backoffice.domain.Employee;
import com.transam.backoffice.domain.Route;
import com.transam.backoffice.domain.Vehicle;
import com.transam.backoffice.repository.rowmapper.RouteRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Route entity.
 */
@SuppressWarnings("unused")
class RouteRepositoryInternalImpl extends SimpleR2dbcRepository<Route, Long> implements RouteRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RouteRowMapper routeMapper;

    private static final Table entityTable = Table.aliased("route", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable employeeLink = new EntityManager.LinkTable(
        "rel_route__employee",
        "route_id",
        "employee_id"
    );
    private static final EntityManager.LinkTable vehicleLink = new EntityManager.LinkTable("rel_route__vehicle", "route_id", "vehicle_id");

    public RouteRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RouteRowMapper routeMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Route.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.routeMapper = routeMapper;
    }

    @Override
    public Flux<Route> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Route> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RouteSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Route.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Route> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Route> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Route> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Route> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Route> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Route process(Row row, RowMetadata metadata) {
        Route entity = routeMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Route> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Route> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(employeeLink, entity.getId(), entity.getEmployees().stream().map(Employee::getId))
            .then();
        result = result.and(entityManager.updateLinkTable(vehicleLink, entity.getId(), entity.getVehicles().stream().map(Vehicle::getId)));
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(employeeLink, entityId).and(entityManager.deleteFromLinkTable(vehicleLink, entityId));
    }
}
