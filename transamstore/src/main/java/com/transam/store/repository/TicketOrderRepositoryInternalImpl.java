package com.transam.store.repository;

import com.transam.store.domain.TicketOrder;
import com.transam.store.repository.rowmapper.CustomerRowMapper;
import com.transam.store.repository.rowmapper.TicketOrderRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TicketOrder entity.
 */
@SuppressWarnings("unused")
class TicketOrderRepositoryInternalImpl extends SimpleR2dbcRepository<TicketOrder, Long> implements TicketOrderRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CustomerRowMapper customerMapper;
    private final TicketOrderRowMapper ticketorderMapper;

    private static final Table entityTable = Table.aliased("ticket_order", EntityManager.ENTITY_ALIAS);
    private static final Table customerTable = Table.aliased("customer", "customer");

    public TicketOrderRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CustomerRowMapper customerMapper,
        TicketOrderRowMapper ticketorderMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TicketOrder.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.customerMapper = customerMapper;
        this.ticketorderMapper = ticketorderMapper;
    }

    @Override
    public Flux<TicketOrder> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TicketOrder> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TicketOrderSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TicketOrder.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TicketOrder> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TicketOrder> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<TicketOrder> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<TicketOrder> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<TicketOrder> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private TicketOrder process(Row row, RowMetadata metadata) {
        TicketOrder entity = ticketorderMapper.apply(row, "e");
        entity.setCustomer(customerMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends TicketOrder> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
