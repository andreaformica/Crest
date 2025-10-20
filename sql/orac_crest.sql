
-- CREST database schema creation script ( testbed is in the ATLAS_PHYS_COND @ INTR )
-- add some comment
-- we added tables corresponding to API v4 and related CREST data model, with a split of BLOBs into separate tables

drop table CREST_ROLES purge;
drop table CREST_USERS purge;
drop table CREST_FOLDERS purge;
drop table PAYLOAD purge;
drop table PAYLOAD_DATA purge;
drop table PAYLOAD_INFO purge;
drop table PAYLOAD_STREAMER_DATA purge;
drop table RUN_LUMI_INFO purge;

drop table IOV purge;
drop table TAG purge;
drop table TAG_META purge;
drop table GLOBAL_TAG purge;
drop table GLOBAL_TAG_MAP purge;

-- =========================== CREST_ROLES ====================================
-- The CREST_ROLEID from a DB sequence?
CREATE TABLE CREST_ROLES
   (
	CREST_USRID VARCHAR2(100) constraint CREST_ROLES_USRID_NN NOT NULL,
	CREST_USRROLE VARCHAR2(100) constraint CREST_ROLES_USRROLE_NN NOT NULL,
	constraint CREST_ROLES_PK PRIMARY KEY (CREST_USRID)
   )
ORGANIZATION INDEX;


COMMENT ON TABLE CREST_ROLES is 'For management of ROLEs inside the CREST schema';


-- =========================== CREST_USERS ====================================

-- The CREST_USRID from a DB sequence?
-- Does the user need to have a CREST_ROLE, thus FK to CREST_ROLES ?
CREATE TABLE CREST_USERS
   (
	CREST_USRID VARCHAR2(100) constraint CREST_USERS_USRID_NN NOT NULL,
	CREST_USRNAME VARCHAR2(100) constraint CREST_USERS_USRNAME_NN NOT NULL,
	CREST_USRPSS VARCHAR2(100) constraint CREST_USERS_USRPSS_NN NOT NULL,
	constraint CREST_USERS_PK PRIMARY KEY (CREST_USRID),
	constraint CREST_USERS_USRNAME_UQ UNIQUE (CREST_USRNAME),
	constraint CREST_USERS_USRPSS_UQ UNIQUE (CREST_USRPSS)
   )
ORGANIZATION INDEX;

COMMENT ON TABLE CREST_USERS is 'For management of USERs inside the CREST schema';

-- =========================== CREST_FOLDERS ====================================

CREATE TABLE CREST_FOLDERS
   (
	CREST_NODE_FULLPATH VARCHAR2(255) constraint CREST_FOLDERS_NODE_FULLPATH_NN NOT NULL,
	CREST_GROUP_ROLE VARCHAR2(100) constraint CREST_FOLDERS_GROUP_ROLE_NN NOT NULL,
	CREST_NODE_NAME VARCHAR2(255) constraint CREST_FOLDERS_NODE_NAME_NN NOT NULL,
	CREST_NODE_DESCRIPTION VARCHAR2(2000) constraint CREST_FOLDERS_NODE_DESCRIPTION_NN NOT NULL,
	CREST_SCHEMA_NAME VARCHAR2(255) constraint CREST_FOLDERS_SCHEMA_NAME_NN NOT NULL,
	CREST_TAG_PATTERN VARCHAR2(255) constraint CREST_FOLDERS_TAG_PATTERN_NN NOT NULL,
	constraint CREST_FOLDERS_PK PRIMARY KEY (CREST_NODE_FULLPATH),
	constraint CREST_FOLDERS_NODE_DESCRIPTION_UQ UNIQUE (CREST_NODE_DESCRIPTION),
	constraint CREST_FOLDERS_TAG_PATTERN_UQ UNIQUE (CREST_TAG_PATTERN)
   )
   PCTFREE 3;


COMMENT ON TABLE CREST_FOLDERS is 'For management of FOLDERs inside the CREST schema';


-- ==================================== TAG ====================================

-- sequence object for the TAG_IDs
-- CREATE SEQUENCE TAGID_SEQUENCE MINVALUE 1 INCREMENT BY 1 NOCACHE NOORDER NOCYCLE GLOBAL ;

CREATE TABLE TAG
   (
	-----TAG_ID NUMBER(19) DEFAULT ON NULL TAGID_SEQUENCE.NEXTVAL constraint CREST_TAG_ID_NN NOT NULL,
	NAME VARCHAR2(500) constraint TAG_NAME_NN NOT NULL,
	DESCRIPTION VARCHAR2(32767) constraint TAG_DESCRIPTION_NN NOT NULL, -- 4K max size, is it necessary ?
	INSERTION_TIME TIMESTAMP(0) constraint TAG_INSERTION_TIME_NN NOT NULL,
	MODIFICATION_TIME TIMESTAMP(0) constraint TAG_MODIFICATION_TIME_NN NOT NULL,
	END_OF_VALIDITY NUMBER(20) constraint TAG_END_OF_VALIDITY_NN NOT NULL,
	LAST_VALIDATED_TIME NUMBER(20) constraint TAG_LAST_VALIDATED_TIME_NN NOT NULL,
	OBJECT_TYPE VARCHAR2(4000) constraint TAG_OBJECT_TYPE_NN NOT NULL, -- 4K max size, is it necessary ?
	SYNCHRONIZATION VARCHAR2(20) constraint TAG_SYNCHRONIZATION_NN NOT NULL,
	TIME_TYPE VARCHAR2(16) constraint TAG_TIME_TYPE_NN NOT NULL,
	--constraint TAG_PK PRIMARY KEY (TAG_ID), -- using index LOCAL
	constraint TAG_PK PRIMARY KEY  (NAME) using index GLOBAL
   )
   PCTFREE 3
;


COMMENT ON TABLE TAG is 'For management of TAGs inside the CREST schema';



-- =========================== TAG_META ====================================

-- FK to the TAG.NAME column? Or better TAG_ID to be used?
-- No, better to keep the string for the moment.
-- BLOBs in row?
-- This may be a small BLOB, we try to keep it in row....To be asked to Andrei and Luca
CREATE TABLE TAG_META
   (
	TAG_NAME VARCHAR2(500) constraint TAG_META_NAME_NN NOT NULL,
	INSERTION_TIME TIMESTAMP(0) constraint TAG_META_INSTIME_NN NOT NULL,
	CHANNEL_SIZE NUMBER(10),
	COLUMN_SIZE NUMBER(10),
	DESCRIPTION VARCHAR2(1000),
 	TAG_INFO BLOB constraint TAG_META_INFO_NN NOT NULL,
	constraint TAG_META_PK PRIMARY KEY (TAG_NAME),
    constraint TAG_META_NAME_TAG_NAME_FK FOREIGN KEY(TAG_NAME) REFERENCES TAG(NAME)
   )
   PCTFREE 3
   LOB (tag_info)
	STORE AS (STORAGE (INITIAL 4K NEXT 4K)
	ENABLE STORAGE IN ROW
	CACHE READS
);


COMMENT ON TABLE TAG_META is 'For management of TAGs META information inside the CREST schema';

-- =========================== GLOBAL_TAG ====================================

CREATE TABLE GLOBAL_TAG
   (
	NAME VARCHAR2(100) constraint GLOBAL_TAG_NAME_NN NOT NULL,
	DESCRIPTION VARCHAR2(4000) constraint GLOBAL_TAG_DESCRIPTION_NN NOT NULL,
	INSERTION_TIME TIMESTAMP(0) constraint GLOBAL_TAG_INSERTION_TIME_NN NOT NULL,
	RELEASE VARCHAR2(100) constraint GLOBAL_TAG_RELEASE_NN NOT NULL,
	SCENARIO VARCHAR2(100) constraint GLOBAL_TAG_SCENARIO_NN NOT NULL,
	SNAPSHOT_TIME TIMESTAMP(0) constraint GLOBAL_TAG_SNAPSHOT_NN NOT NULL,
	TYPE CHAR(1) constraint GLOBAL_TAG_TYPE_NN NOT NULL,
	VALIDITY NUMBER(20) constraint GLOBAL_TAG_VALIDITY_NN NOT NULL,
	WORKFLOW VARCHAR2(100) constraint GLOBAL_TAG_WORKFLOW_NN NOT NULL,
	constraint GLOBAL_TAG_PK PRIMARY KEY (NAME)
   )
   PCTFREE 3;


COMMENT ON TABLE GLOBAL_TAG is 'For management of GLOBAL_TAGs information inside the CREST schema';


-- =========================== GLOBAL_TAG_MAP ====================================

CREATE TABLE GLOBAL_TAG_MAP
   (
	GLOBAL_TAG_NAME VARCHAR2(100) constraint GLOBAL_TAG_MAP_GLOBAL_TAG_NAME_NN NOT NULL,
	LABEL VARCHAR2(100) constraint GLOBAL_TAG_MAP_LABEL_NN NOT NULL,
	RECORD VARCHAR2(100) constraint GLOBAL_TAG_MAP_RECORD_NN NOT NULL,
	TAG_NAME VARCHAR2(500) constraint GLOBAL_TAG_MAP_TAG_NAME_NN NOT NULL,
	constraint GLOBAL_TAG_MAP_PK PRIMARY KEY (GLOBAL_TAG_NAME, LABEL, RECORD),
	constraint GLOBAL_TAG_MAP_TAG_NAME_FK FOREIGN KEY(TAG_NAME) REFERENCES TAG(NAME),
	constraint GLOBAL_TAG_MAP_GLOBAL_TAG_NAME_FK FOREIGN KEY(GLOBAL_TAG_NAME) REFERENCES GLOBAL_TAG(NAME)
	-- constraint GLOBAL_TAG_MAP_TAG_ID_FK FOREIGN KEY(TAG_ID) REFERENCES TAG(TAG_ID)
	-- TAG_ID NUMBER(19) constraint GLOBAL_TAG_MAP_TAG_ID NOT NULL,
   )
   ORGANIZATION INDEX COMPRESS 1;

COMMENT ON TABLE GLOBAL_TAG_MAP is 'For management of GLOBAL_TAG_MAPs information inside the CREST schema';




-- =========================== PAYLOAD ====================================

CREATE TABLE PAYLOAD
   (
	HASH VARCHAR2(64) constraint PAYLOAD_HASH_NN NOT NULL,
	INSERTION_TIME TIMESTAMP(0) constraint PAYLOAD_INSERTION_TIME_NN NOT NULL,
	OBJECT_TYPE VARCHAR2(100) constraint PAYLOAD_OBJECT_TYPE_NN NOT NULL,
	OBJECT_NAME VARCHAR2(400),
	VERSION VARCHAR2(20) constraint PAYLOAD_VERSION_NN NOT NULL,
	DATA_SIZE NUMBER(10),
	COMPRESSION_TYPE VARCHAR2(255),
	CHECK_SUM VARCHAR2(20),
	constraint PAYLOAD_PK PRIMARY KEY (HASH)
   );

COMMENT ON TABLE PAYLOAD is 'For management of PAYLOADs information inside the CREST schema';
-- DATA

CREATE TABLE PAYLOAD_DATA
   (
	HASH VARCHAR2(64) constraint PAYLOAD_D_HASH_NN NOT NULL,
	DATA BLOB constraint PAYLOAD_D_NN NOT NULL,
	constraint PAYLOAD_D_PK PRIMARY KEY (HASH)
   )
   PCTFREE 0
   LOB (data)
	STORE AS (TABLESPACE ATLAS_PHYS_COND_01 STORAGE(INITIAL 16M NEXT 16M)
	DISABLE STORAGE IN ROW
	CACHE READS);


COMMENT ON TABLE PAYLOAD_DATA is 'Table containing DATA LOBs';

-- Some of the LOB settings can be changed over the time
ALTER TABLE payload_data MODIFY LOB (data) (CACHE READS);
ALTER TABLE payload_data MODIFY LOB (data) (STORAGE (NEXT 512M));
ALTER TABLE payload_data MOVE LOB (data) STORE AS (TABLESPACE ATLAS_PHYS_COND_01 DISABLE STORAGE IN ROW);

-- STREAMER_INFO

CREATE TABLE PAYLOAD_STREAMER_DATA
   (
	HASH VARCHAR2(64) constraint PAYLOAD_SI_HASH_NN NOT NULL,
	STREAMER_INFO BLOB constraint PAYLOAD_STREAMER_INFO_NN NOT NULL,
	constraint PAYLOAD_SI_PK PRIMARY KEY (HASH)
   );


COMMENT ON TABLE PAYLOAD_STREAMER_DATA is 'Table containing STREAMER_INFO LOBs';

--/*
--IMPORTANT:
--Only some LOB storage parameters can be modified. The ALTER TABLE ... MODIFY LOB statement can  change RETENTION, PCTVERSION, CACHE or NOCACHE LOGGING or NOLOGGING, and the STORAGE clause. One can also change the TABLESPACE using the ALTER TABLE ... MOVE statement.
--However, once the table has been created, we cannot change the CHUNK size, the ENABLE or DISABLE STORAGE IN ROW, or the BASICFILE, or the SECUREFILE settings.
--*/


-- =========================== IOV ====================================

-- To be index-organised with compress 1 or range-partitioned by TAGS_ID

CREATE TABLE IOV
   (
	--TAG_ID NUMBER(19,0) constraint IOV_TAG_ID_NN NOT NULL,
	TAG_NAME VARCHAR2(500) constraint IOV_TAG_NAME_NN NOT NULL,
	SINCE NUMBER(38,0) constraint IOV_SINCE_NN NOT NULL,
	INSERTION_TIME TIMESTAMP(0) constraint IOV_INSERTION_TIME_NN NOT NULL,
	PAYLOAD_HASH VARCHAR2(64) constraint IOV_PAYLOAD_HASH_NN NOT NULL,
	constraint IOV_PK PRIMARY KEY (TAG_NAME, SINCE, INSERTION_TIME) USING INDEX COMPRESS 1,
	-- constraint IOV_TAG_ID_FK FOREIGN KEY(TAG_ID) REFERENCES TAG(TAG_ID),
	constraint IOV_TAG_NAME_FK FOREIGN KEY(TAG_NAME) REFERENCES TAG(NAME),
	constraint IOV_TAG_PAYLOAD_HASH_FK FOREIGN KEY (PAYLOAD_HASH) REFERENCES PAYLOAD(HASH)
   )
   PCTFREE 2;
alter table "ATLAS_PHYS_COND"."IOV" add constraint IOV_PHASH_TAG_SINCE_UK unique("TAG_NAME","SINCE","PAYLOAD_HASH") ;
COMMENT ON TABLE IOV is 'For management of IOVs information inside the CREST schema';




-- =========================== PAYLOAD_INFO ====================================

-- What is this table for? Why these TAG_NAME 3 attribute columns are in a separate table? Could be in TAG table instead?
-- They could be in the TAG table but I want to keep them disentangled for future updates where the TAG table is fixed.
CREATE TABLE PAYLOAD_INFO
   (
	TAG_NAME VARCHAR2(500) constraint PAYLOAD_INFO_TAG_NAME_NN NOT NULL,  -- FK to the TAG.NAME column? Or better TAG_ID to be used?
	NIOVS NUMBER(10),
	TOT_VOLUME NUMBER(10),
	AVG_VOLUME NUMBER(10)
   )
   PCTFREE 0;


COMMENT ON TABLE PAYLOAD_INFO is 'For management of PAYLOAD_INFOs information inside the CREST schema';


-- =========================== RUN_LUMI_INFO ====================================

-- What is this table for?
CREATE TABLE RUN_LUMI_INFO
   (
	--SINCE NUMBER(20) constraint RUN_LUMI_INFO_SINCE_NN NOT NULL,
	END_TIME NUMBER(20) constraint RUN_LUMI_INFO_END_TIME_NN NOT NULL,
	LUMI_BLOCK NUMBER(20) constraint RUN_LUMI_INFO_LUMI_BLOCK_NN NOT NULL,
	RUN NUMBER(20) constraint RUN_LUMI_INFO_RUN_NN NOT NULL,
	START_TIME NUMBER(20) constraint RUN_LUMI_INFO_START_TIME_NN NOT NULL,
	INSERTION_TIME TIMESTAMP(0),
	constraint RUN_LUMI_INFO_PK PRIMARY KEY (RUN, LUMI_BLOCK)
   )
   PCTFREE 0 ;


COMMENT ON TABLE RUN_LUMI_INFO is 'Copy of RUN LUMI information from other sources';

-- ==========================================================================

