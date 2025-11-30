/**
 *
 */
package hep.crest.server.data.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.QIov;
import hep.crest.server.data.repositories.args.IovModeEnum;
import hep.crest.server.data.repositories.args.IovQueryArgs;
import hep.crest.server.exceptions.CdbNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

/**
 * Repository for IOVs.
 *
 * @author formica
 *
 */
@Slf4j
public class IovRepositoryImpl implements IovRepositoryCustom {
    /**
     * The entity manager.
     */
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Page<Iov> findIovList(IovQueryArgs queryArgs, Pageable preq) throws CdbNotFoundException {
        log.debug("Build query from args: {}", queryArgs);

        final BooleanBuilder where = new BooleanBuilder();
        final BooleanBuilder subwhere = new BooleanBuilder();

        buildBaseConditions(queryArgs, where, subwhere);
        buildModeSpecificConditions(queryArgs, where, subwhere);

        log.info("Launch query for iovs....");
        JPQLQuery<Iov> query = buildQuery(where);

        return executePaginatedQuery(query, preq);
    }

    /**
     * Build base where conditions from query arguments.
     *
     * @param queryArgs the query arguments
     * @param where     the main where clause
     * @param subwhere  the subquery where clause
     */
    private void buildBaseConditions(IovQueryArgs queryArgs, BooleanBuilder where,
                                     BooleanBuilder subwhere) {
        if (queryArgs.hash() != null) {
            where.and(QIov.iov.payloadHash.eq(queryArgs.hash()));
            log.debug("Add where condition on hash: {}", queryArgs.hash());
        }

        if (queryArgs.tagName() != null) {
            addTagNameCondition(queryArgs.tagName(), where, subwhere);
        }

        if (queryArgs.since() != null) {
            addSinceCondition(queryArgs, where, subwhere);
        }

        if (queryArgs.until() != null) {
            addUntilCondition(queryArgs, where);
        }

        if (queryArgs.snapshot() != null) {
            where.and(QIov.iov.id.insertionTime.loe(queryArgs.snapshot()));
            subwhere.and(QIov.iov.id.insertionTime.loe(queryArgs.snapshot()));
            log.debug("Add where condition on <= snapshot: {}", queryArgs.snapshot());
        }
    }

    /**
     * Add tag name condition to where clause.
     *
     * @param tagName  the tag name
     * @param where    the main where clause
     * @param subwhere the subquery where clause
     */
    private void addTagNameCondition(String tagName, BooleanBuilder where,
                                     BooleanBuilder subwhere) {
        if (tagName.contains("%")) {
            where.and(QIov.iov.id.tagName.like(tagName));
        }
        else {
            where.and(QIov.iov.id.tagName.eq(tagName));
        }
        subwhere.and(QIov.iov.id.tagName.eq(tagName));
        log.debug("Add where condition on tagName: {}", tagName);
    }

    /**
     * Add since condition to where clause.
     *
     * @param queryArgs the query arguments
     * @param where     the main where clause
     * @param subwhere  the subquery where clause
     */
    private void addSinceCondition(IovQueryArgs queryArgs, BooleanBuilder where,
                                   BooleanBuilder subwhere) {
        if (!queryArgs.mode().equals(IovModeEnum.AT)) {
            where.and(QIov.iov.id.since.goe(queryArgs.since()));
            log.debug("Add where condition on >= since: {}", queryArgs.since());
        }
        else {
            subwhere.and(QIov.iov.id.since.loe(queryArgs.since()));
            log.debug("Add where condition on <=since for AT query: {}", queryArgs.since());
        }
    }

    /**
     * Add until condition to where clause based on mode.
     *
     * @param queryArgs the query arguments
     * @param where     the main where clause
     */
    private void addUntilCondition(IovQueryArgs queryArgs, BooleanBuilder where) {
        if (queryArgs.mode().equals(IovModeEnum.RANGES)) {
            where.and(QIov.iov.id.since.loe(queryArgs.until()));
        }
        else {
            where.and(QIov.iov.id.since.lt(queryArgs.until()));
        }
        log.debug("Add where condition on < until: {}", queryArgs.until());
    }

    /**
     * Build mode\-specific where conditions (AT or RANGES mode).
     *
     * @param queryArgs the query arguments
     * @param where     the main where clause
     * @param subwhere  the subquery where clause
     */
    private void buildModeSpecificConditions(IovQueryArgs queryArgs, BooleanBuilder where,
                                             BooleanBuilder subwhere) {
        IovModeEnum mode = queryArgs.mode();

        if (mode.equals(IovModeEnum.AT)) {
            addAtModeCondition(where, subwhere);
        }
        else if (mode.equals(IovModeEnum.RANGES)) {
            addRangesModeCondition(where, subwhere);
        }
    }

    /**
     * Add AT mode specific condition using max since subquery.
     *
     * @param where    the main where clause
     * @param subwhere the subquery where clause
     */
    private void addAtModeCondition(BooleanBuilder where, BooleanBuilder subwhere) {
        log.warn("Adding where condition on MAX of since");
        JPQLQuery<Iov> subquery = new JPAQuery<>(entityManager);
        where.and(QIov.iov.id.since.eq(
                subquery.select(QIov.iov.id.since.max())
                        .from(QIov.iov).where(subwhere).fetchOne()));
    }

    /**
     * Add RANGES mode specific condition using max since subquery.
     *
     * @param where    the main where clause
     * @param subwhere the subquery where clause
     */
    private void addRangesModeCondition(BooleanBuilder where, BooleanBuilder subwhere) {
        log.warn("Adding where condition on MAX of since");
        JPQLQuery<Iov> subquery = new JPAQuery<>(entityManager);
        where.and(QIov.iov.id.since.goe(
                subquery.select(QIov.iov.id.since.max())
                        .from(QIov.iov).where(subwhere).fetchOne()));
    }

    /**
     * Build the base JPQL query.
     *
     * @param where the where clause
     * @return the JPQLQuery
     */
    private JPQLQuery<Iov> buildQuery(BooleanBuilder where) {
        QIov iov = QIov.iov;
        JPQLQuery<Iov> query = new JPAQuery<>(entityManager);
        query = query.select(iov).from(iov).where(where);
        log.debug("created JPQL query: {}", query.toString());
        return query;
    }

    /**
     * Execute the query with pagination.
     *
     * @param query the query to execute
     * @param preq  the pagination request
     * @return Page of Iov results
     */
    private Page<Iov> executePaginatedQuery(JPQLQuery<Iov> query, Pageable preq) {
        PathBuilder<Iov> builder = new PathBuilderFactory().create(Iov.class);
        Querydsl querydsl = new Querydsl(entityManager, builder);
        long totalElements = query.fetchCount();
        List<Iov> rows = querydsl.applyPagination(preq, query).fetch();
        return new PageImpl<>(rows, preq, totalElements);
    }


}
