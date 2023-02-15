
CREATE TABLE public.crest_folders (
    crest_node_fullpath character varying(255) NOT NULL,
    crest_group_role character varying(100) NOT NULL,
    crest_node_description character varying(2000) NOT NULL,
    crest_node_name character varying(255) NOT NULL,
    crest_schema_name character varying(255) NOT NULL,
    crest_tag_pattern character varying(255) NOT NULL
);


ALTER TABLE public.crest_folders OWNER TO svom_cea;

--
-- Name: crest_roles; Type: TABLE; Schema: public; Owner: svom_cea
--

CREATE TABLE public.crest_roles (
    crest_usrid character varying(100) NOT NULL,
    crest_usrrole character varying(100) NOT NULL
);


ALTER TABLE public.crest_roles OWNER TO svom_cea;

--
-- Name: crest_users; Type: TABLE; Schema: public; Owner: svom_cea
--

CREATE TABLE public.crest_users (
    crest_usrid character varying(100) NOT NULL,
    crest_usrpss character varying(100) NOT NULL,
    crest_usrname character varying(100) NOT NULL
);


ALTER TABLE public.crest_users OWNER TO svom_cea;

--
-- Name: global_tag; Type: TABLE; Schema: public; Owner: svom_cea
--

CREATE TABLE public.global_tag (
    name character varying(100) NOT NULL,
    description character varying(4000) NOT NULL,
    insertion_time timestamp without time zone NOT NULL,
    release character varying(100) NOT NULL,
    scenario character varying(100) NOT NULL,
    snapshot_time timestamp without time zone NOT NULL,
    type character(1) NOT NULL,
    validity numeric(38,0) NOT NULL,
    workflow character varying(100) NOT NULL
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
-- Name: payload; Type: TABLE; Schema: public; Owner: svom_cea
--

CREATE TABLE V4_PAYLOAD
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

ALTER TABLE public.V4_PAYLOAD OWNER TO svom_cea;

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

ALTER TABLE ONLY public.V4_PAYLOAD
    ADD CONSTRAINT payload_v4_pkey PRIMARY KEY (hash);
ALTER TABLE ONLY public.PAYLOAD_DATA
    ADD CONSTRAINT payload_data_pkey PRIMARY KEY (hash);
ALTER TABLE ONLY public.PAYLOAD_STREAMER_DATA
    ADD CONSTRAINT payload_stream_pkey PRIMARY KEY (hash);

GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE public.V4_PAYLOAD TO crest_w;
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

CREATE TABLE public.v4_global_tag (
    name character varying(100) NOT NULL,
    description character varying(4000) NOT NULL,
    insertion_time timestamp without time zone NOT NULL,
    release character varying(100) NOT NULL,
    scenario character varying(100) NOT NULL,
    snapshot_time timestamp without time zone NOT NULL,
    type character(1) NOT NULL,
    validity numeric(38,0) NOT NULL,
    workflow character varying(100) NOT NULL
);


ALTER TABLE public.v4_global_tag OWNER TO svom_cea;

--
-- Name: global_tag_map; Type: TABLE; Schema: public; Owner: svom_cea
--

CREATE TABLE public.v4_global_tag_map (
    global_tag_name character varying(100) NOT NULL,
    label character varying(100) NOT NULL,
    record character varying(100) NOT NULL,
    tag_name character varying(255) NOT NULL
);


ALTER TABLE public.v4_global_tag_map OWNER TO svom_cea;

--
-- Name: iov; Type: TABLE; Schema: public; Owner: svom_cea
--

CREATE TABLE public.v4_iov (
    insertion_time timestamp without time zone NOT NULL,
    since numeric(38,0) NOT NULL,
    tag_name character varying(100) NOT NULL,
    payload_hash character varying(64) NOT NULL
);


ALTER TABLE public.v4_iov OWNER TO svom_cea;

--
-- Name: tag; Type: TABLE; Schema: public; Owner: svom_cea
--

CREATE TABLE public.v4_tag (
    name character varying(255) NOT NULL,
    description character varying(4000) NOT NULL,
    end_of_validity numeric(38,0) NOT NULL,
    insertion_time timestamp without time zone NOT NULL,
    last_validated_time numeric(38,0) NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    object_type character varying(4000) NOT NULL,
    synchronization character varying(20) NOT NULL,
    time_type character varying(16) NOT NULL
);


ALTER TABLE public.v4_tag OWNER TO svom_cea;

ALTER TABLE ONLY public.v4_global_tag_map
    ADD CONSTRAINT v4_global_tag_map_pkey PRIMARY KEY (global_tag_name, label, record);
ALTER TABLE ONLY public.v4_global_tag
    ADD CONSTRAINT v4_global_tag_pkey PRIMARY KEY (name);
ALTER TABLE ONLY public.v4_iov
    ADD CONSTRAINT v4_iov_pkey PRIMARY KEY (insertion_time, since, tag_name);
ALTER TABLE ONLY public.v4_iov
    ADD CONSTRAINT v4_iov_unique_hashsincetag_key UNIQUE (payload_hash, since, tag_name);
ALTER TABLE ONLY public.v4_tag
    ADD CONSTRAINT v4_tag_pkey PRIMARY KEY (name);
ALTER TABLE ONLY public.v4_global_tag_map
    ADD CONSTRAINT fk_v4_globaltag_name FOREIGN KEY (global_tag_name) REFERENCES public.v4_global_tag(name);
ALTER TABLE ONLY public.v4_global_tag_map
    ADD CONSTRAINT fk_v4_tag_tagname FOREIGN KEY (tag_name) REFERENCES public.v4_tag(name);
ALTER TABLE ONLY public.v4_iov
    ADD CONSTRAINT fk_v4_tag_tagname FOREIGN KEY (tag_name) REFERENCES public.v4_tag(name);





