
--
-- Name: delete_packetsbytarname(); Type: FUNCTION; Schema: public; Owner: svom_cea
--

CREATE OR REPLACE FUNCTION public.empty_trash_new() RETURNS INTEGER
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
      query := 'select * from v4_iov where tag_name = ''TRASH''';
	  for rec in execute query
        loop
            raise notice 'Delete payloads for iov : %', rec.payload_hash;
            SELECT p.streamer_info into soid FROM PAYLOAD_STREAMER_DATA p WHERE p.hash = rec.payload_hash;
            SELECT p.data into doid FROM PAYLOAD_DATA p WHERE p.hash = rec.payload_hash;
            raise notice 'Remove entry in trash : % ', rec.payload_hash;
            delete from v4_iov where tag_name = 'TRASH' and payload_hash = rec.payload_hash;
            raise notice 'Remove entry in payload table : % ', rec.payload_hash;
            delete from V4_PAYLOAD p where p.hash = rec.payload_hash;
            delete from PAYLOAD_DATA p where p.hash = rec.payload_hash;
            delete from PAYLOAD_STREAMER_DATA p where p.hash = rec.payload_hash;
            raise notice 'Unlink oids : % - %', doid, soid;
            perform lo_unlink(doid) ;
            perform lo_unlink(soid) ;
            statusflag := statusflag + 1;
      end loop;
      RETURN statusflag;
   EXCEPTION
    when others then
    begin
        raise notice 'Exception in emptying the trash';
        GET STACKED DIAGNOSTICS v_error_stack = PG_EXCEPTION_CONTEXT;
        RAISE WARNING 'The stack trace of the error is: "%"', v_error_stack;
    end;
    RETURN 0;
   END;
   $$;

ALTER FUNCTION public.empty_trash_new OWNER TO svom_cea;
GRANT EXECUTE ON FUNCTION public.empty_trash_new() TO crest_w;

--
-- Name: delete_packetsbytarname(); Type: FUNCTION; Schema: public; Owner: svom_cea
--

CREATE OR REPLACE FUNCTION public.delete_tag(name character varying) RETURNS INTEGER
    LANGUAGE plpgsql
    AS $$   DECLARE
     statusflag INTEGER;
     rec record;
     query text;
     npylds integer;
     v_error_stack text;
   BEGIN
      statusflag := 0;
      query := 'select * from tag where name = $1';
	  for rec in execute query using name
        loop
            raise notice 'Delete iovs for tag : %', rec.name;
            perform trash_iov(rec.name);
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

CREATE OR REPLACE FUNCTION public.trash_iov(name character varying) RETURNS INTEGER
    LANGUAGE plpgsql
    AS $$   DECLARE
     statusflag INTEGER;
     rec record;
     query text;
     npylds integer;
     v_error_stack text;
   BEGIN
      statusflag := 0;
      query := 'select * from iov where tag_name = $1';
	  for rec in execute query using name
        loop
            raise notice 'Trash iovs % for tag : %', rec.payload_hash, rec.tag_name;
            select count(*) into npylds from iov where payload_hash = rec.payload_hash
                and tag_name != 'TRASH' and tag_name != rec.tag_name;
            if npylds > 1 then
                raise notice 'More than one iovs for payload %', rec.payload_hash;
                continue;
            end if;
            update iov set tag_name='TRASH' where payload_hash = rec.payload_hash;
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

ALTER FUNCTION public.delete_tag OWNER TO svom_cea;
GRANT EXECUTE ON FUNCTION public.delete_tag(character varying) TO crest_w;
ALTER FUNCTION public.trash_iov OWNER TO svom_cea;
GRANT EXECUTE ON FUNCTION public.trash_iov(character varying) TO crest_w;

CREATE OR REPLACE FUNCTION public.empty_trash() RETURNS INTEGER
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
            delete from iov where tag_name = 'TRASH' and payload_hash = rec.payload_hash;
            raise notice 'Remove entry in payload table : % ', rec.payload_hash;
            delete from PAYLOAD p where p.hash = rec.payload_hash;
            raise notice 'Unlink oids : % - %', doid, soid;
            perform lo_unlink(doid) ;
            perform lo_unlink(soid) ;
            statusflag := statusflag + 1;
      end loop;
      RETURN statusflag;
   EXCEPTION
    when others then
    begin
        raise notice 'Exception in emptying the trash';
        GET STACKED DIAGNOSTICS v_error_stack = PG_EXCEPTION_CONTEXT;
        RAISE WARNING 'The stack trace of the error is: "%"', v_error_stack;
    end;
    RETURN 0;
   END;
   $$;

ALTER FUNCTION public.empty_trash OWNER TO svom_cea;
GRANT EXECUTE ON FUNCTION public.empty_trash() TO crest_w;

--- Insert trash tag ---
insert into tag (name, description, object_type,
    time_type, end_of_validity, insertion_time,
    last_validated_time, modification_time, synchronization)
    values ('TRASH', 'trash tag', 'removable', 'any', 0, now(), 0, now(), 'none');