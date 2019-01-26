package hep.crest.data.pojo;
// Generated Aug 2, 2016 3:50:25 PM by Hibernate Tools 3.2.2.GA

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import hep.crest.data.config.*;
/**
 * Tag generated by hbm2java
 */
@Entity
@Table(name = "TAG", schema = DatabasePropertyConfigurator.SCHEMA_NAME)
public class Tag implements java.io.Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = -7205518190608667851L;
	private String name;
	private String timeType;
	private String objectType;
	private String synchronization;
	private String description;
	private BigDecimal lastValidatedTime;
	private BigDecimal endOfValidity;
	private Date insertionTime;
	private Date modificationTime;
	private Set<GlobalTagMap> globalTagMaps = new HashSet<GlobalTagMap>(0);

	public Tag() {
	}

	public Tag(String name) {
		this.name = name;
	}

	public Tag(String name, String timeType, String objectType, String synchronization, String description,
			BigDecimal lastValidatedTime, BigDecimal endOfValidity, Date insertionTime, Date modificationTime) {
		this.name = name;
		this.timeType = timeType;
		this.objectType = objectType;
		this.synchronization = synchronization;
		this.description = description;
		this.lastValidatedTime = lastValidatedTime;
		this.endOfValidity = endOfValidity;
		this.insertionTime = insertionTime;
		this.modificationTime = modificationTime;

	}

	@Id

	@Column(name = "NAME", unique = true, nullable = false, length = 255)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "TIME_TYPE", nullable = false, length = 16)
	public String getTimeType() {
		return this.timeType;
	}

	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}

	@Column(name = "OBJECT_TYPE", nullable = false, length = 255)
	public String getObjectType() {
		return this.objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	@Column(name = "SYNCHRONIZATION", nullable = false, length = 20)
	public String getSynchronization() {
		return this.synchronization;
	}

	public void setSynchronization(String synchronization) {
		this.synchronization = synchronization;
	}

	@Column(name = "DESCRIPTION", nullable = false, length = 4000)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "LAST_VALIDATED_TIME", nullable = false, precision = 38, scale = 0)
	public BigDecimal getLastValidatedTime() {
		return this.lastValidatedTime;
	}

	public void setLastValidatedTime(BigDecimal lastValidatedTime) {
		this.lastValidatedTime = lastValidatedTime;
	}

	@Column(name = "END_OF_VALIDITY", nullable = false, precision = 38, scale = 0)
	public BigDecimal getEndOfValidity() {
		return this.endOfValidity;
	}

	public void setEndOfValidity(BigDecimal endOfValidity) {
		this.endOfValidity = endOfValidity;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "INSERTION_TIME", nullable = false, length = 11)
	public Date getInsertionTime() {
		return this.insertionTime;
	}

	public void setInsertionTime(Date insertionTime) {
		this.insertionTime = insertionTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFICATION_TIME", nullable = false, length = 11)
	public Date getModificationTime() {
		return this.modificationTime;
	}

	public void setModificationTime(Date modificationTime) {
		this.modificationTime = modificationTime;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tag")
	public Set<GlobalTagMap> getGlobalTagMaps() {
		return this.globalTagMaps;
	}

	public void setGlobalTagMaps(Set<GlobalTagMap> globalTagMaps) {
		this.globalTagMaps = globalTagMaps;
	}

	@PrePersist
	public void prePersist() {
		if (this.insertionTime == null) {
			Timestamp now = new Timestamp(new Date().getTime());
			this.insertionTime = now;
			this.modificationTime = now;			
		}
	}

	@PreUpdate
	public void preUpdate() {
		if (this.modificationTime == null) {
			Timestamp now = new Timestamp(new Date().getTime());
			this.modificationTime = now;			
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Tag [name=" + name + ", timeType=" + timeType + ", objectType=" + objectType + ", synchronization="
				+ synchronization + ", description=" + description + ", lastValidatedTime=" + lastValidatedTime
				+ ", endOfValidity=" + endOfValidity + ", insertionTime=" + insertionTime + ", modificationTime="
				+ modificationTime + "]";
	}

}
