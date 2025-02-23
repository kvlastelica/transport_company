package com.transam.backoffice.repository;

import com.transam.backoffice.domain.RouteSchedule;
import com.transam.backoffice.repository.rowmapper.RouteRowMapper;
import com.transam.backoffice.repository.rowmapper.RouteScheduleRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the RouteSchedule entity.
 */
@SuppressWarnings("unused")
class RouteScheduleRepositoryInternalImpl extends SimpleR2dbcRepository<RouteSchedule, Long> implements RouteScheduleRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RouteRowMapper routeMapper;
    private final RouteScheduleRowMapper routescheduleMapper;

    private static final Table entityTable = Table.aliased("route_schedule", EntityManager.ENTITY_ALIAS);
    private static final Table routeTable = Table.aliased("route", "route");

    public RouteScheduleRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RouteRowMapper routeMapper,
        RouteScheduleRowMapper routescheduleMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(RouteSchedule.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.routeMapper = routeMapper;
        this.routescheduleMapper = routescheduleMapper;
    }

    @Override
    public Flux<RouteSchedule> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<RouteSchedule> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RouteScheduleSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RouteSqlHelper.getColumns(routeTable, "route"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(routeTable)
            .on(Column.create("route_id", entityTable))
            .equals(Column.create("id", routeTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, RouteSchedule.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<RouteSchedule> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<RouteSchedule> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<RouteSchedule> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<RouteSchedule> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<RouteSchedule> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private RouteSchedule process(Row row, RowMetadata metadata) {
        RouteSchedule entity = routescheduleMapper.apply(row, "e");
        entity.setRoute(routeMapper.apply(row, "route"));
        return entity;
    }

    @Override
    public <S extends RouteSchedule> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
