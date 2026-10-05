package hep.crest.server.timeseries.model.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import hep.crest.server.exceptions.CdbNotFoundException;
import hep.crest.server.data.repositories.TagRepository;
import hep.crest.server.data.pojo.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.stereotype.Repository;
import hep.crest.server.timeseries.model.TimeSerieIov;
import hep.crest.server.timeseries.model.QTimeSerieIov;


import java.util.List;

@Repository
@Slf4j
public class TimeSerieIovRepositoryImpl implements TimeSerieIovRepositoryCustom {
    /**
     * The entity manager.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Tag repository for looking up tag IDs by name.
     */
    @Autowired
    private TagRepository tagRepository;

    @Override
    public Page<TimeSerieIov> findIovList(TimeSerieIovQueryArgs queryArgs, Pageable preq)
            throws CdbNotFoundException {
        JPQLQuery<TimeSerieIov> query = new JPAQuery<>(entityManager);

        final BooleanBuilder where = new BooleanBuilder();

        log.debug("Build query from args: {}", queryArgs);
        if (queryArgs.tagName() != null) {
            // Look up the tag ID by name and use it directly in the query
            log.info("Looking up tag by name: {}", queryArgs.tagName());
            Tag tag = tagRepository.findByName(queryArgs.tagName())
                    .orElseThrow(() -> new CdbNotFoundException("Tag not found: " + queryArgs.tagName()));
            log.info("Found tag: name={}, tagId={}", tag.getName(), tag.getTagId());
            where.and(QTimeSerieIov.timeSerieIov.id.tagId.eq(tag.getTagId()));
            log.info("Added where condition on tag ID: {}", tag.getTagId());
        }
        if (queryArgs.channelId() != null) {
            where.and(QTimeSerieIov.timeSerieIov.id.channelId.eq(queryArgs.channelId()));
            log.debug("Add where condition on channel id: {}", queryArgs.channelId());
        }
        if (queryArgs.since() != null) {
            where.and(QTimeSerieIov.timeSerieIov.id.since.goe(queryArgs.since()));
            log.debug("Add where condition on since: {}", queryArgs.since());
        }
        if (queryArgs.until() != null) {
            where.and(QTimeSerieIov.timeSerieIov.id.since.lt(queryArgs.until()));
            log.debug("Add where condition on until: {}", queryArgs.until());
        }

        // Build the query.
        log.info("Launch query for timeserie iovs....");
        QTimeSerieIov iov = QTimeSerieIov.timeSerieIov;
        query = query.select(iov).from(iov)
                .where(where)
                .distinct();

        log.debug("created JPQL query: {}", query.toString());
        PathBuilder<TimeSerieIov> builder = (new PathBuilderFactory()).create(TimeSerieIov.class);
        Querydsl querydsl = new Querydsl(entityManager, builder);
        log.debug("Fetching total elements count...");
        long totalElements = query.fetchCount();
        log.debug("Total elements: {}", totalElements);
        // Apply pagination
        log.debug("Applying pagination using {}", preq.toString());
        List<TimeSerieIov> rows = querydsl.applyPagination(preq, query).fetch();
        log.debug("Query retrieved {}", rows);
        return new PageImpl<>(rows, preq, totalElements);
    }
}


