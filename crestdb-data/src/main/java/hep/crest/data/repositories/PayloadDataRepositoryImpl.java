/**
 *
 */
package hep.crest.data.repositories;

import hep.crest.data.exceptions.CdbSQLException;
import hep.crest.data.pojo.PayloadData;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * Repository for Payload DATA.
 *
 * @author formica
 *
 */
@Repository
@Slf4j
public class PayloadDataRepositoryImpl implements PayloadDataRepositoryCustom {

    /**
     * The entity manager.
     */
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveData(String id, InputStream is, int length) throws CdbSQLException {
        PayloadData entity = new PayloadData();
        entity.hash(id);
        entity.data(BlobProxy.generateProxy(is, length));
        entityManager.persist(entity);
    }

    @Override
    public InputStream findData(String id) throws CdbSQLException {
        try {
            // It is important that this method does not apply any transaction.
            // All existing transactions should be handled externally.
            PayloadData entity = entityManager.find(PayloadData.class, id);
            return entity.data().getBinaryStream();
        }
        catch (SQLException e) {
            log.error("Cannot get payload data for " + id);
            throw new CdbSQLException(e);
        }
    }
}
