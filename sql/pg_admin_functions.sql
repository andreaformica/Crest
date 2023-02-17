
--
-- Name: delete_packetsbytarname(); Type: FUNCTION; Schema: public; Owner: svom_cea
--

CREATE OR REPLACE FUNCTION public.empty_trash_new(nmax integer) RETURNS INTEGER
    LANGUAGE plpgsql
    AS $$   DECLARE
     statusflag INTEGER;
     rec record;
     query text;
     doid oid;
     soid oid;
     v_error_stack text;
   BEGIN
      statusflag := 0;
      query := 'select * from iov where tag_name = ''TRASH''';
	  for rec in execute query
        loop
            raise notice 'Delete payloads for iov : %', rec.payload_hash;
            SELECT p.streamer_info into soid FROM PAYLOAD_STREAMER_DATA p WHERE p.hash = rec.payload_hash;
            SELECT p.data into doid FROM PAYLOAD_DATA p WHERE p.hash = rec.payload_hash;
            raise notice 'Remove entry in trash : % ', rec.payload_hash;

            begin
                delete from iov where tag_name = 'TRASH' and payload_hash = rec.payload_hash;
                raise notice 'Remove entry in payload table : % ', rec.payload_hash;
                delete from PAYLOAD p where p.hash = rec.payload_hash;
                delete from PAYLOAD_DATA p where p.hash = rec.payload_hash;
                delete from PAYLOAD_STREAMER_DATA p where p.hash = rec.payload_hash;
                raise notice 'Unlink oids : % - %', doid, soid;
                perform lo_unlink(doid) ;
                perform lo_unlink(soid) ;
                IF statusflag % 100 = 0 THEN
                    raise notice 'Commiting every 100 payloads';
                    COMMIT;
                END IF;
            exception
                when others then
                begin
                    raise notice 'Exception in removing payload %', rec.payload_hash;
                end;
            end;
            statusflag := statusflag + 1;
            if statusflag > nmax then
                raise notice 'exit';
                EXIT;
            end if;
      end loop;
   EXCEPTION
    when others then
    begin
        raise notice 'Exception in emptying the trash';
        GET STACKED DIAGNOSTICS v_error_stack = PG_EXCEPTION_CONTEXT;
        RAISE WARNING 'The stack trace of the error is: "%"', v_error_stack;
    end;
   END;
   $$;

ALTER FUNCTION public.empty_trash_new OWNER TO svom_cea;
GRANT EXECUTE ON FUNCTION public.empty_trash_new(integer) TO crest_w;

--
-- Name: delete_packetsbytarname(); Type: FUNCTION; Schema: public; Owner: svom_cea
--

CREATE OR REPLACE FUNCTION public.delete_tag(name character varying, force boolean) RETURNS INTEGER
    LANGUAGE plpgsql
    AS $$   DECLARE
     statusflag INTEGER;
     rec record;
     query text;
     npylds integer;
     v_error_stack text;
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
        GET STACKED DIAGNOSTICS v_error_stack = PG_EXCEPTION_CONTEXT;
        RAISE WARNING 'The stack trace of the error is: "%"', v_error_stack;
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
     v_error_stack text;
   BEGIN
      statusflag := 0;
      query := 'select * from iov where tag_name = $1';
	  for rec in execute query using name
        loop
            raise notice 'Trash iovs % for tag : %', rec.payload_hash, rec.tag_name;
            select count(*) into npylds from iov where payload_hash = rec.payload_hash
                and tag_name != 'TRASH' and tag_name != rec.tag_name;
            if npylds > 1 and not force then
                raise notice 'More than one iovs for payload %', rec.payload_hash;
                continue;
            else
                begin
                    raise notice 'Delete iovs for hash : %', rec.payload_hash;
                    update iov set tag_name='TRASH' where
                        payload_hash = rec.payload_hash and tag_name != 'TRASH';
                exception
                    when others then
                    begin
                        raise notice 'Exception in trashing hash %', rec.payload_hash;
                    end;
                end;
            end if;
            statusflag := statusflag + 1;
      end loop;

      RETURN statusflag;
   EXCEPTION
    when others then
    begin
        raise notice 'Exception in trashing Iov for tag name %', name;
        GET STACKED DIAGNOSTICS v_error_stack = PG_EXCEPTION_CONTEXT;
        RAISE WARNING 'The stack trace of the error is: "%"', v_error_stack;
    end;
    RETURN 0;
   END;
   $$;
ALTER FUNCTION public.trash_iov OWNER TO svom_cea;
GRANT EXECUTE ON FUNCTION public.trash_iov(character varying, boolean) TO crest_w;

CREATE OR REPLACE PROCEDURE public.empty_trash(nmax integer)
    LANGUAGE plpgsql
    AS $$   DECLARE
     statusflag INTEGER;
     rec record;
     query text;
     doid oid;
     soid oid;
     v_error_stack text;
   BEGIN
      statusflag := 0;
      query := 'select * from iov where tag_name = ''TRASH''';
	  for rec in execute query
        loop
            raise notice 'Delete payloads for iov : %', rec.payload_hash;
            SELECT p.streamer_info into soid FROM PAYLOAD p WHERE p.hash = rec.payload_hash;
            SELECT p.data into doid FROM PAYLOAD p WHERE p.hash = rec.payload_hash;
            raise notice 'Remove entry in trash : % ', rec.payload_hash;
            begin
                delete from iov where tag_name = 'TRASH' and payload_hash = rec.payload_hash;
                raise notice 'Remove entry in payload table : % ', rec.payload_hash;
                delete from PAYLOAD p where p.hash = rec.payload_hash;
                raise notice 'Unlink oids : % - %', doid, soid;
                perform lo_unlink(doid) ;
                perform lo_unlink(soid) ;
                IF statusflag % 100 = 0 THEN
                    raise notice 'Commiting every 100 payloads';
                    COMMIT;
                END IF;
            exception
                when others then
                begin
                    raise notice 'Exception in removing payload %', rec.payload_hash;
                end;
            end;
            statusflag := statusflag + 1;
            if statusflag > nmax then
                raise notice 'exit';
                EXIT;
            end if;
      end loop;
   EXCEPTION
    when others then
    begin
        raise notice 'Exception in emptying the trash';
        GET STACKED DIAGNOSTICS v_error_stack = PG_EXCEPTION_CONTEXT;
        RAISE WARNING 'The stack trace of the error is: "%"', v_error_stack;
    end;
   END;
   $$;

ALTER PROCEDURE public.empty_trash OWNER TO svom_cea;
GRANT EXECUTE ON PROCEDURE public.empty_trash(integer) TO crest_w;

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