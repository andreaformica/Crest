
--- CREST TRASH TAG
-- INSERT INTO public.tag (name, description, end_of_validity, insertion_time, last_validated_time, modification_time, object_type, synchronization, time_type) VALUES ('TRASH', 'trash', 0, '2023-01-01 10:10:10.000000', 0, '2023-01-01 10:10:10.000000', 'ALL', 'none', 'time');

--
-- Name: delete_packetsbytarname(); Type: FUNCTION; Schema: public; Owner: svom_cea
--

CREATE OR REPLACE FUNCTION public.empty_trash_new(trash_name character varying, nmax integer) RETURNS void
    LANGUAGE plpgsql
    AS $$   DECLARE
     statusflag INTEGER;
     rec record;
     query text;
     doid oid;
     soid oid;
     npylds integer;
     v_error_stack text;
     v_state   TEXT;
     v_msg     TEXT;
     v_detail  TEXT;
     v_hint    TEXT;
     v_context TEXT;

   BEGIN
      statusflag := 0;
      query := 'select payload_hash from iov where tag_name like $1 group by payload_hash';
	  for rec in execute query using trash_name
        loop
            raise notice 'Delete payloads for iov=% and tag=%', rec.payload_hash, trash_name;
            SELECT p.streamer_info into soid FROM PAYLOAD_STREAMER_DATA p WHERE p.hash = rec.payload_hash;
            SELECT p.data into doid FROM PAYLOAD_DATA p WHERE p.hash = rec.payload_hash;
            raise notice 'Remove entry in trash : % ', rec.payload_hash;
            select count(*) into npylds from iov where payload_hash = rec.payload_hash and tag_name != trash_name;
            if npylds > 1 then
                raise notice 'More than one iovs for payload %, only delete iov', rec.payload_hash;
                delete from iov where tag_name = trash_name and payload_hash = rec.payload_hash;
                continue;
            else
                begin
                    raise notice 'Remove entry in iov table for hash : % ', rec.payload_hash;
                    delete from iov where tag_name = trash_name and payload_hash = rec.payload_hash;
                end;
                begin
                    raise notice 'Remove entry in payload table for hash : % ', rec.payload_hash;
                    delete from PAYLOAD p where p.hash = rec.payload_hash;
                    delete from PAYLOAD_DATA p where p.hash = rec.payload_hash;
                    delete from PAYLOAD_STREAMER_DATA p where p.hash = rec.payload_hash;
                    raise notice 'Unlink oids : % - %', doid, soid;
                    perform lo_unlink(doid) ;
                    perform lo_unlink(soid) ;
                exception
                    when others then
                    begin
                        raise notice 'Exception in removing payload %', rec.payload_hash;
                        get stacked diagnostics
                            v_state   = returned_sqlstate,
                            v_msg     = message_text,
                            v_detail  = pg_exception_detail,
                            v_hint    = pg_exception_hint,
                            v_context = pg_exception_context;

                        raise notice E'Got exception:
                            state  : %
                            message: %
                            detail : %
                            hint   : %
                            context: %', v_state, v_msg, v_detail, v_hint, v_context;

                        raise notice E'Got exception:
                            SQLSTATE: %
                            SQLERRM: %', SQLSTATE, SQLERRM;
                    end;
                end;
            end if;
            statusflag := statusflag + 1;
            if statusflag > nmax then
                raise notice 'exit...exceed number of requested iov to delete % (not active)', statusflag;
            end if;
      end loop;
      raise notice 'Finish delete loop';
   EXCEPTION
    when others then
    begin
        raise notice 'Exception in emptying the trash';
        get stacked diagnostics
            v_state   = returned_sqlstate,
            v_msg     = message_text,
            v_detail  = pg_exception_detail,
            v_hint    = pg_exception_hint,
            v_context = pg_exception_context;

        raise notice E'Got exception:
            state  : %
            message: %
            detail : %
            hint   : %
            context: %', v_state, v_msg, v_detail, v_hint, v_context;

        raise notice E'Got exception:
            SQLSTATE: %
            SQLERRM: %', SQLSTATE, SQLERRM;
    end;
   raise notice 'Return number of elements deleted %', statusflag;
   END;
   $$;

ALTER FUNCTION public.empty_trash_new OWNER TO svom_cea;
GRANT EXECUTE ON FUNCTION public.empty_trash_new(character varying, integer) TO crest_w;


--
-- Name: delete_global_tag(); Type: FUNCTION; Schema: public; Owner: svom_cea
--


CREATE OR REPLACE FUNCTION public.delete_global_tag(name character varying, force boolean) RETURNS INTEGER
    LANGUAGE plpgsql
    AS $$   DECLARE
     statusflag INTEGER;
     rec record;
     query text;
     npylds integer;
     v_error_stack text;
     v_state   TEXT;
     v_msg     TEXT;
     v_detail  TEXT;
     v_hint    TEXT;
     v_context TEXT;
   BEGIN
      statusflag := 0;
      query := 'select * from global_tag_map where global_tag_name = $1';
	  for rec in execute query using name
        loop
            raise notice 'Delete tag : %', rec.tag_name;
            perform delete_tag(rec.tag_name, force);
            statusflag := statusflag + 1;
      end loop;
      RETURN statusflag;
   EXCEPTION
    when others then
    begin
        raise notice 'Exception in removing GLOBAL TAG for name %', name;
        get stacked diagnostics
            v_state   = returned_sqlstate,
            v_msg     = message_text,
            v_detail  = pg_exception_detail,
            v_hint    = pg_exception_hint,
            v_context = pg_exception_context;

        raise notice E'Got exception:
            state  : %
            message: %
            detail : %
            hint   : %
            context: %', v_state, v_msg, v_detail, v_hint, v_context;

        raise notice E'Got exception:
            SQLSTATE: %
            SQLERRM: %', SQLSTATE, SQLERRM;
    end;
    RETURN 0;
   END;
   $$;

ALTER FUNCTION public.delete_global_tag OWNER TO svom_cea;
GRANT EXECUTE ON FUNCTION public.delete_global_tag(character varying, boolean) TO crest_w;

--
-- Name: delete_tag(); Type: FUNCTION; Schema: public; Owner: svom_cea
--

CREATE OR REPLACE FUNCTION public.delete_tag(name character varying, force boolean) RETURNS INTEGER
    LANGUAGE plpgsql
    AS $$   DECLARE
     statusflag INTEGER;
     rec record;
     query text;
     npylds integer;
     v_error_stack text;
     v_state   TEXT;
     v_msg     TEXT;
     v_detail  TEXT;
     v_hint    TEXT;
     v_context TEXT;
   BEGIN
      statusflag := 0;
      query := 'select * from tag where name like $1';
	  for rec in execute query using name
        loop
            raise notice 'Delete iovs for tag : %', rec.name;
            perform trash_iov(rec.name, force);
            statusflag := statusflag + 1;
      end loop;
      RETURN statusflag;
   EXCEPTION
    when others then
    begin
        raise notice 'Exception in removing TAG for name %', name;
        get stacked diagnostics
            v_state   = returned_sqlstate,
            v_msg     = message_text,
            v_detail  = pg_exception_detail,
            v_hint    = pg_exception_hint,
            v_context = pg_exception_context;

        raise notice E'Got exception:
            state  : %
            message: %
            detail : %
            hint   : %
            context: %', v_state, v_msg, v_detail, v_hint, v_context;

        raise notice E'Got exception:
            SQLSTATE: %
            SQLERRM: %', SQLSTATE, SQLERRM;
    end;
    RETURN 0;
   END;
   $$;

ALTER FUNCTION public.delete_tag OWNER TO svom_cea;
GRANT EXECUTE ON FUNCTION public.delete_tag(character varying, boolean) TO crest_w;

CREATE OR REPLACE FUNCTION public.trash_iov(name character varying, force boolean) RETURNS INTEGER
    LANGUAGE plpgsql
    AS $$   DECLARE
     statusflag INTEGER;
     rec record;
     query text;
     npylds integer;
     nhash integer;
     ntagtr integer;
     trash_tag text;
     v_error_stack text;
     v_state   TEXT;
     v_msg     TEXT;
     v_detail  TEXT;
     v_hint    TEXT;
     v_context TEXT;
   BEGIN
      statusflag := 0;
      trash_tag := 'TRASH-' || name;
      select count(*) into ntagtr from tag t where t.name like trash_tag;
      if ntagtr = 0 then
          insert into tag (name, description, object_type,
              time_type, end_of_validity, insertion_time,
              last_validated_time, modification_time, synchronization)
              values (trash_tag, 'trash tag', 'removable', 'any', 0, now(), 0, now(), 'none');
      end if;
      query := 'select * from iov where tag_name = $1';
	  for rec in execute query using name
        loop
            raise notice 'Trash iovs % for tag : %', rec.payload_hash, rec.tag_name;
            select count(*) into npylds from iov where payload_hash = rec.payload_hash
                and tag_name != trash_tag and tag_name != rec.tag_name;
            if npylds > 0 and not force then
                raise notice 'More than one iovs for payload %, skip it', rec.payload_hash;
                continue;
            else
                begin
                    raise notice 'Delete iovs for hash : %', rec.payload_hash;
                    update iov set tag_name=trash_tag where
                        payload_hash = rec.payload_hash and tag_name != trash_tag and tag_name = rec.tag_name;
                    if npylds > 0 and force then
                        raise notice 'Delete iov from other tags: hash %', rec.payload_hash;
                        delete from iov where payload_hash = rec.payload_hash and tag_name != trash_tag;
                    end if;
                exception
                    when others then
                    begin
                    raise notice 'Exception in updating IOV for name %', name;
                    get stacked diagnostics
                        v_state   = returned_sqlstate,
                        v_msg     = message_text,
                        v_detail  = pg_exception_detail,
                        v_hint    = pg_exception_hint,
                        v_context = pg_exception_context;

                    raise notice E'Got exception:
                        state  : %
                        message: %
                        detail : %
                        hint   : %
                        context: %', v_state, v_msg, v_detail, v_hint, v_context;

                    raise notice E'Got exception:
                        SQLSTATE: %
                        SQLERRM: %', SQLSTATE, SQLERRM;
                    end;
                end;
            end if;
            statusflag := statusflag + 1;
      end loop;
      RETURN statusflag;
   EXCEPTION
    when others then
    begin
        raise notice 'Exception in trash IOV for name %', name;
        get stacked diagnostics
            v_state   = returned_sqlstate,
            v_msg     = message_text,
            v_detail  = pg_exception_detail,
            v_hint    = pg_exception_hint,
            v_context = pg_exception_context;

        raise notice E'Got exception:
            state  : %
            message: %
            detail : %
            hint   : %
            context: %', v_state, v_msg, v_detail, v_hint, v_context;

        raise notice E'Got exception:
            SQLSTATE: %
            SQLERRM: %', SQLSTATE, SQLERRM;
    end;
    RETURN 0;
   END;
   $$;
ALTER FUNCTION public.trash_iov OWNER TO svom_cea;
GRANT EXECUTE ON FUNCTION public.trash_iov(character varying, boolean) TO crest_w;

--- Insert trash tag ---
insert into tag (name, description, object_type,
    time_type, end_of_validity, insertion_time,
    last_validated_time, modification_time, synchronization)
    values ('TRASH', 'trash tag', 'removable', 'any', 0, now(), 0, now(), 'none');

--- Get DB size ---
SELECT d.datname as Name,  pg_catalog.pg_get_userbyid(d.datdba) as Owner,
    CASE WHEN pg_catalog.has_database_privilege(d.datname, 'CONNECT')
        THEN pg_catalog.pg_size_pretty(pg_catalog.pg_database_size(d.datname))
        ELSE 'No Access'
    END as Size
FROM pg_catalog.pg_database d
    order by
    CASE WHEN pg_catalog.has_database_privilege(d.datname, 'CONNECT')
        THEN pg_catalog.pg_database_size(d.datname)
        ELSE NULL
    END desc
    LIMIT 20;

SELECT pg_size_pretty( pg_total_relation_size('tablename') );