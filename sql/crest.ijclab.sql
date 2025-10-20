==================================
--
-- Name: payload; Type: TABLE; Schema: public; Owner: svom_cea
--

CREATE TABLE PAYLOAD
   (
    hash character varying(64) NOT NULL,
    insertion_time timestamp without time zone NOT NULL,
    object_type character varying(100) NOT NULL,
    object_name character varying(400) NOT NULL,
    version character varying(20) NOT NULL,
    data_size integer,
	COMPRESSION_TYPE character varying(20) NOT NULL,
	CHECK_SUM character varying(20)
   );

ALTER TABLE public.PAYLOAD OWNER TO svom_cea;

CREATE TABLE PAYLOAD_DATA
   (
    hash character varying(64) NOT NULL,
    data oid NOT NULL
   );

ALTER TABLE public.PAYLOAD_DATA OWNER TO svom_cea;

-- STREAMER_INFO
CREATE TABLE PAYLOAD_STREAMER_DATA
   (
    hash character varying(64) NOT NULL,
	STREAMER_INFO oid NOT NULL
   );

ALTER TABLE public.PAYLOAD_STREAMER_DATA OWNER TO svom_cea;

ALTER TABLE ONLY public.PAYLOAD
    ADD CONSTRAINT payload_pkey PRIMARY KEY (hash);
ALTER TABLE ONLY public.PAYLOAD_DATA
    ADD CONSTRAINT payload_data_pkey PRIMARY KEY (hash);
ALTER TABLE ONLY public.PAYLOAD_STREAMER_DATA
    ADD CONSTRAINT payload_stream_pkey PRIMARY KEY (hash);

GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE public.PAYLOAD TO crest_w;
GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE public.PAYLOAD_DATA TO crest_w;
GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE public.PAYLOAD_STREAMER_DATA TO crest_w;

--
-- Name: run_info; Type: TABLE; Schema: public; Owner: svom_cea
--

CREATE TABLE public.run_info (
    run_number numeric(38,0) NOT NULL,
    start_time timestamp without time zone  NOT NULL,
    end_time timestamp without time zone  NOT NULL
);


ALTER TABLE public.run_info OWNER TO svom_cea;

CREATE TABLE public.global_tag (
    name character varying(100) NOT NULL,
    description character varying(4000) default '' NOT NULL,
    insertion_time timestamp without time zone NOT NULL,
    release character varying(100) NOT NULL,
    scenario character varying(100) default ''  NOT NULL,
    snapshot_time timestamp without time zone NOT NULL,
    type character(1) NOT NULL,
    validity numeric(38,0) default 0 NOT NULL,
    workflow character varying(100) default '' NOT NULL
);


ALTER TABLE public.global_tag OWNER TO svom_cea;

--
-- Name: global_tag_map; Type: TABLE; Schema: public; Owner: svom_cea
--

CREATE TABLE public.global_tag_map (
    global_tag_name character varying(100) NOT NULL,
    label character varying(100) NOT NULL,
    record character varying(100) NOT NULL,
    tag_name character varying(255) NOT NULL
);


ALTER TABLE public.global_tag_map OWNER TO svom_cea;

--
-- Name: iov; Type: TABLE; Schema: public; Owner: svom_cea
--

CREATE TABLE public.iov (
    insertion_time timestamp without time zone NOT NULL,
    since numeric(38,0) NOT NULL,
    tag_name character varying(100) NOT NULL,
    payload_hash character varying(64) NOT NULL
);


ALTER TABLE public.iov OWNER TO svom_cea;

--
-- Name: tag; Type: TABLE; Schema: public; Owner: svom_cea
--

CREATE TABLE public.tag (
    name character varying(255) NOT NULL,
    description character varying(4000) default '' NOT NULL,
    end_of_validity numeric(38,0) default 0 NOT NULL,
    insertion_time timestamp without time zone NOT NULL,
    last_validated_time numeric(38,0) default 0 NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    object_type character varying(4000) default 'lob' NOT NULL,
    synchronization character varying(20) default 'none' NOT NULL,
    time_type character varying(16) NOT NULL
);


ALTER TABLE public.tag OWNER TO svom_cea;


-- FK to the TAG.NAME column? Or better TAG_ID to be used?
-- No, better to keep the string for the moment.
-- BLOBs in row?
-- This may be a small BLOB, we try to keep it in row....To be asked to Andrei and Luca
CREATE TABLE TAG_META
   (
	TAG_NAME character varying(500) NOT NULL,
	INSERTION_TIME timestamp without time zone NOT NULL,
	CHANNEL_SIZE integer,
	COLUMN_SIZE integer,
	DESCRIPTION character varying(4000) default '' NOT NULL,
 	TAG_INFO oid NOT NULL
   );
ALTER TABLE public.TAG_META OWNER TO svom_cea;

GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE public.TAG TO crest_w;
GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE public.IOV TO crest_w;
GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE public.GLOBAL_TAG TO crest_w;
GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE public.TAG_META TO crest_w;
GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE public.GLOBAL_TAG_MAP TO crest_w;

ALTER TABLE ONLY public.global_tag_map
    ADD CONSTRAINT global_tag_map_pkey PRIMARY KEY (global_tag_name, label, record);
ALTER TABLE ONLY public.global_tag
    ADD CONSTRAINT global_tag_pkey PRIMARY KEY (name);
ALTER TABLE ONLY public.iov
    ADD CONSTRAINT iov_pkey PRIMARY KEY (insertion_time, since, tag_name);
ALTER TABLE ONLY public.iov
    ADD CONSTRAINT iov_unique_hashsincetag_key UNIQUE (payload_hash, since, tag_name);
ALTER TABLE ONLY public.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (name);
ALTER TABLE ONLY public.global_tag_map
    ADD CONSTRAINT fk_globaltag_name FOREIGN KEY (global_tag_name) REFERENCES public.global_tag(name);
ALTER TABLE ONLY public.global_tag_map
    ADD CONSTRAINT fk_tag_tagname FOREIGN KEY (tag_name) REFERENCES public.tag(name);
ALTER TABLE ONLY public.iov
    ADD CONSTRAINT fk_iov_tag_tagname FOREIGN KEY (tag_name) REFERENCES public.tag(name);
ALTER TABLE ONLY public.TAG_META
    ADD CONSTRAINT tag_meta_pkey PRIMARY KEY (tag_name);
ALTER TABLE ONLY public.TAG_META
    ADD CONSTRAINT fk_meta_tag_tagname FOREIGN KEY (tag_name) REFERENCES public.tag(name);

ALTER TABLE ONLY public.payload_data
    ADD CONSTRAINT fk_pydata_hash FOREIGN KEY (hash) REFERENCES public.payload(hash);
ALTER TABLE ONLY public.payload_streamer_data
    ADD CONSTRAINT fk_pystreamdata_hash FOREIGN KEY (hash) REFERENCES public.payload(hash);




