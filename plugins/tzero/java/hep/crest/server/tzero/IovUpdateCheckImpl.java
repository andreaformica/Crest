package hep.crest.server.tzero;

import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.data.pojo.TagTimeTypeEnum;
import hep.crest.server.data.utils.RunIovConverter;
import hep.crest.server.services.IovService;
import hep.crest.server.swagger.model.TagDto;
import hep.crest.server.tzero.repositories.ITzeroDb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

/**
 * This class is used to check the conditions for inserting an IOV.
 */
@Component
@Slf4j
public class IovUpdateCheckImpl implements IovUpdateCheck {

    /**
     * Service.
     */
    private IovService iovService;
    /**
     * Service.
     */
    private ITzeroDb tzeroDb;

    /**
     * Constructor.
     * @param iovService
     * @param tzeroDb
     */
    @Autowired
    IovUpdateCheckImpl(IovService iovService, ITzeroDb tzeroDb) {
        this.iovService = iovService;
        this.tzeroDb = tzeroDb;
    }

    @Override
    public boolean evaluateConditions(Tag tagentity, Iov entity) {
        final String synchro = tagentity.getSynchronization();
        Boolean acceptTime = Boolean.FALSE;
        Iov latest = iovService.latest(tagentity.getName());
        if (latest == null) {
            log.debug("No iov found for tag {}", tagentity.getName());
            return Boolean.TRUE;
        }
        log.debug("Evaluate condition for tag {} with synchro type {} and latest iov {}",
                tagentity.getName(), synchro, latest);
        //
        switch (TagDto.SynchronizationEnum.valueOf(synchro)) {
            case SV:
                log.warn("This logic is not fully implemented. For the moment allows to append "
                        + "IOV. In future it should check the time [now] or the run [now]");
                log.warn("Can only append IOVs....");
                if (latest.getId().getSince().compareTo(entity.getId().getSince()) <= 0) {
                    // Latest is before the new one.
                    log.info("IOV in insert has correct time respect to last IOV : {} > {}",
                            entity, latest);
                    acceptTime = true;
                }
                else {
                    // Latest is after the new one.
                    log.warn("IOV in insert has WRONG time respect to last IOV : {} < {}",
                            entity, latest);
                    acceptTime = false;
                }
                break;
            case UPD:
                log.warn("The logic for UPD synchro depends on an external source for run-lumi");
                acceptTime = isUpdAllowed(tagentity, entity, latest);
                break;
            case ALL:
                log.warn("Can insert data in any case because it is an open tag");
                acceptTime = true;
                break;
            default:
                // Nothing here, synchro type is not implemented.
                // We should throw an exception here.
                // For the time being we accept the insertion.
                log.warn("Synchro type not found....Insertion is not allowed by default [FIXME]");
                log.warn("We allow insertion during development....");
                acceptTime = true;
                break;
        }
        return acceptTime;
    }

    /**
     * Strategy for time based or run-lumi based tags.
     * @param entity
     * @param interval
     * @param last
     * @return boolean
     */
    protected boolean isUpdAllowed(Tag entity, Iov interval, Iov last) {
        // Check the end of validity of the tag.
        if (last.getId().getSince().compareTo(interval.getId().getSince()) > 0) {
            log.warn("The since is before the last iov: insertion is NOT allowed ");
            return false;
        }
        if (TagTimeTypeEnum.TIME.getCode().equalsIgnoreCase(entity.getTimeType())) {
            log.debug("Tag is time based");
            Instant now = Instant.now();
            BigInteger nowTime =
                    BigInteger.valueOf(now.getEpochSecond() * RunIovConverter.TO_NANOSECONDS);
            if (nowTime.compareTo(interval.getId().getSince()) < 0 || last == null) {
                log.debug("The since is in the future...insertion is allowed");
                return true;
            }
            else {
                log.info("The since is in the past...check last iov");
                // Time unit is nanoseconds for the since.
                // The interval should not be older than 36 hours respect to NOW.
                Instant pastnow =
                        now.minusNanos(RunIovConverter.CALIBRATION_LOOP_LENGTH.longValue());
                BigInteger pastnowTime =
                        BigInteger.valueOf(pastnow.getEpochSecond() * RunIovConverter.TO_NANOSECONDS);

                if (interval.getId().getSince().compareTo(pastnowTime) < 0) {
                    log.warn("The since is older than 36 hours: insertion is NOT allowed ");
                    return false;
                }
            }
            log.warn("No conditions found, should not happen: check the tag end time...");
        }
        else {
            log.debug("Tag time type is {}", entity.getTimeType());
            List<Long> calibruns = tzeroDb.calibRunList();
            if (calibruns == null || calibruns.isEmpty()) {
                log.warn("No calibration runs found in the database");
                return false;
            }
            log.debug("Calibration runs found in the database: {}", calibruns);
            // Check if the since is after the last calibration run.
            Long lastrun = calibruns.stream().max(Long::compare).orElse(null);
            if (lastrun == null) {
                log.warn("No calibration run found in the database");
                return false;
            }
            BigInteger lastrunLumi = BigInteger.valueOf(lastrun << 32);
            if (interval.getId().getSince().compareTo(lastrunLumi) < 0) {
                log.warn("The since is before the last calibration run: insertion is NOT allowed ");
                return false;
            }
        }
        BigInteger endofval = entity.getEndOfValidity();
        if (endofval == null || endofval.compareTo(interval.getId().getSince()) <= 0) {
            log.info("The since is after end of validity of the Tag");
            return true;
        }
        log.warn("The since is before end of validity of the Tag");
        return false;
    }
}
