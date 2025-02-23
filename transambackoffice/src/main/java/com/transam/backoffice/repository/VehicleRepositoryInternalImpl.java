package com.transam.backoffice.repository;

import com.transam.backoffice.domain.Vehicle;
import com.transam.backoffice.repository.rowmapper.VehicleRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Vehicle entity.
 */
@SuppressWarnings("unused")
class VehicleRepositoryInternalImpl extends SimpleR2dbcRepository<Vehicle, Long> implements VehicleRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final VehicleRowMapper vehicleMapper;

    private static final Table entityTable = Table.aliased("vehicle", EntityManager.ENTITY_ALIAS);

    public VehicleRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        VehicleRowMapper vehicleMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Vehicle.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.vehicleMapper = vehicleMapper;
    }

    @Override
    public Flux<Vehicle> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Vehicle> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = VehicleSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Vehicle.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Vehicle> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Vehicle> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Vehicle process(Row row, RowMetadata metadata) {
        Vehicle entity = vehicleMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Vehicle> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
