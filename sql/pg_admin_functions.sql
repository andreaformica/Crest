
--
-- Name: delete_packetsbytarname(); Type: FUNCTION; Schema: public; Owner: svom_cea
--

CREATE OR REPLACE FUNCTION public.delete_payload(hash character varying) RETURNS INTEGER
    LANGUAGE plpgsql
    AS $$   DECLARE
     statusflag INTEGER;
     rec record;
     doid oid;
     soid oid;
     query text;
     v_error_stack text;
   BEGIN
      statusflag := 0;
      query := 'select * from v4_payload where hash = $1';
	  for rec in execute query using hash
        loop
            raise notice 'Get OID for DATA using hash : %', rec.hash;
            SELECT p.data into doid FROM PAYLOAD_DATA p WHERE p.hash = rec.hash;
            raise notice 'Get OID for STREAMER using hash : %', rec.hash;
            SELECT p.streamer_info into soid FROM PAYLOAD_STREAMER_DATA p WHERE p.hash = rec.hash;
            statusflag := statusflag + 1;
            raise notice 'remove payload data for hash : %', rec.hash;
            delete from PAYLOAD_DATA pd where pd.hash = rec.hash;
            raise notice 'remove payload streamer data for hash : %', rec.hash;
            delete from PAYLOAD_STREAMER_DATA pds where pds.hash = rec.hash;
            raise notice 'remove payload for hash : %', rec.hash;
            delete from V4_PAYLOAD p where p.hash = rec.hash;
            raise notice 'remove oids : % %', doid, soid;
            select lo_unlink(doid) ;
            select lo_unlink(soid) ;
      end loop;
      RETURN statusflag;
   EXCEPTION
    when others then
    begin
        raise notice 'Exception in removing LOB for hash %', hash;
        GET STACKED DIAGNOSTICS v_error_stack = PG_EXCEPTION_CONTEXT;
        RAISE WARNING 'The stack trace of the error is: "%"', v_error_stack;
    end;
    RETURN 0;
   END;
   $$;

ALTER FUNCTION public.delete_payload OWNER TO svom_cea;
GRANT EXECUTE ON FUNCTION public.delete_payload(character varying) TO crest_w;


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
