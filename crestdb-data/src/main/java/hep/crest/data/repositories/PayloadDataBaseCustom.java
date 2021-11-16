/**
 * This file is part of PhysCondDB.
 * <p>
 * PhysCondDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * PhysCondDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with PhysCondDB.  If not, see <http://www.gnu.org/licenses/>.
 **/
package hep.crest.data.repositories;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.exceptions.CdbSQLException;
import hep.crest.data.pojo.Payload;
import hep.crest.swagger.model.IovPayloadDto;
import hep.crest.swagger.model.PayloadDto;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author formica
 *
 */
public interface PayloadDataBaseCustom {


    /**
     * @param id
     *            the String
     * @return String or null.
     */
    String exists(String id) throws CdbSQLException;

    /**
     * @param id
     *            the String
     * @return PayloadDto or null.
     */
    PayloadDto find(String id) throws CdbSQLException;

    /**
     * @param id
     *            the String
     * @return Payload Stream or null.
     */
    InputStream findData(String id) throws CdbSQLException;

    /**
     * The method does not access blob data.
     *
     * @param id
     *            the String
     * @return PayloadDto or null.
     */
    PayloadDto findMetaInfo(String id) throws CdbSQLException;

    /**
     * The method does not access blob data.
     *
     * @param id
     *            the String
     * @param streamerInfo
     *            the String
     * @return The number of updated rows.
     */
    int updateMetaInfo(String id, String streamerInfo) throws CdbSQLException;

    /**
     * @param name
     *            the String
     * @param since
     *            the BigDecimal
     * @param until
     *            the BigDecimal
     * @param snapshot
     *            the Date
     * @return List<IovPayloadDto>
     */
    List<IovPayloadDto> getRangeIovPayloadInfo(String name, BigDecimal since, BigDecimal until,
                                               Date snapshot);

    /**
     * @param entity
     *            the PayloadDto
     * @return Either the entity which has been saved or null.
     * @throws AbstractCdbServiceException
     *             It should in reality not throw any exception
     */
    PayloadDto save(PayloadDto entity) throws CdbSQLException;

    /**
     * @param entity
     *            the PayloadDto
     * @param is
     *            the InputStream
     * @return Either the entity which has been saved or null.
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    PayloadDto save(PayloadDto entity, InputStream is) throws CdbSQLException;

    /**
     * @return Payload
     */
    Payload saveNull();

    /**
     * @param id
     *            the String
     * @return
     */
    void delete(String id) throws CdbSQLException;
}
