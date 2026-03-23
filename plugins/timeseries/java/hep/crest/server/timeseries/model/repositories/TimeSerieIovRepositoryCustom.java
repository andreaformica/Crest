package hep.crest.server.timeseries.model.repositories;

import hep.crest.server.exceptions.CdbNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import hep.crest.server.timeseries.model.TimeSerieIov;

/**
 * Repository interface for TimeSerieIov.
 */
public interface TimeSerieIovRepositoryCustom {

    /**
     * General purpose Node retrieval.
     *
     * @param args
     * @param preq
     * @return Page of Iov.
     * @throws CdbNotFoundException
     */
    Page<TimeSerieIov> findIovList(
            TimeSerieIovQueryArgs args, Pageable preq) throws CdbNotFoundException;

}
