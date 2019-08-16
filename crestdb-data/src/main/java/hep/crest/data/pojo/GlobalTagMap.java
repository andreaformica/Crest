package hep.crest.data.pojo;
// Generated Aug 2, 2016 3:50:25 PM by Hibernate Tools 3.2.2.GA


import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import hep.crest.data.config.*;
/**
 * GlobalTagMap generated by hbm2java
 */
@Entity
@Table(name="GLOBAL_TAG_MAP"
,schema=DatabasePropertyConfigurator.SCHEMA_NAME
)
public class GlobalTagMap implements java.io.Serializable {


     /**
	 * 
	 */
	private static final long serialVersionUID = -3535546379433341349L;
	private GlobalTagMapId id;
     private Tag tag;
     private GlobalTag globalTag;

    public GlobalTagMap() {
    }

    public GlobalTagMap(GlobalTagMapId id, Tag tag, GlobalTag globalTag) {
       this.id = id;
       this.tag = tag;
       this.globalTag = globalTag;
    }

    
     @EmbeddedId
    
    @AttributeOverrides( {
        @AttributeOverride(name="globalTagName", column=@Column(name="GLOBAL_TAG_NAME", nullable=false, length=100) ), 
        @AttributeOverride(name="record", column=@Column(name="RECORD", nullable=false, length=100) ), 
        @AttributeOverride(name="label", column=@Column(name="LABEL", nullable=false, length=100) ) } )
    public GlobalTagMapId getId() {
        return this.id;
    }
    
    public void setId(GlobalTagMapId id) {
        this.id = id;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="TAG_ID", nullable=false)
    public Tag getTag() {
        return this.tag;
    }
    
    public void setTag(Tag tag) {
        this.tag = tag;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="GLOBAL_TAG_NAME", nullable=false, insertable=false, updatable=false)
    public GlobalTag getGlobalTag() {
        return this.globalTag;
    }
    
    public void setGlobalTag(GlobalTag globalTag) {
        this.globalTag = globalTag;
    }


}


