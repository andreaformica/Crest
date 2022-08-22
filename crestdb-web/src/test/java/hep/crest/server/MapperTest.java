package hep.crest.server;

import hep.crest.data.pojo.GlobalTag;
import hep.crest.data.pojo.GlobalTagMap;
import hep.crest.data.pojo.GlobalTagMapId;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.IovId;
import hep.crest.data.pojo.Tag;
import hep.crest.data.runinfo.pojo.RunLumiInfo;
import hep.crest.data.security.pojo.CrestFolders;
import hep.crest.server.swagger.model.FolderDto;
import hep.crest.server.swagger.model.GlobalTagDto;
import hep.crest.server.swagger.model.GlobalTagMapDto;
import hep.crest.server.swagger.model.IovDto;
import hep.crest.server.swagger.model.RunLumiInfoDto;
import hep.crest.server.swagger.model.TagDto;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("default")
@Slf4j
public class MapperTest {
    @Autowired
    @Qualifier("mapper")
    private MapperFacade mapper;

    private final Random rnd = new Random();

    public void fillRandom(Object obj, Class<?> clazz) {
        try {
            Method[] publicMethods = clazz.getMethods();
            for (Method aMethod : publicMethods) {
                if (aMethod.getName().startsWith("set")
                        && aMethod.getParameterCount() == 1) {
                    Class<?> argtype = aMethod.getParameterTypes()[0];
                    if (argtype.equals(Double.class)) {
                        Double val = rnd.nextDouble();
                        aMethod.invoke(obj, val);
                    } else if (argtype.equals(Float.class)) {
                        Float val = rnd.nextFloat();
                        aMethod.invoke(obj, val);
                    } else if (argtype.equals(BigDecimal.class)) {
                        aMethod.invoke(obj, BigDecimal.valueOf(rnd.nextDouble()));
                    } else if (argtype.equals(Long.class)) {
                        Long val = rnd.nextLong();
                        aMethod.invoke(obj, val);
                    } else if (argtype.equals(Integer.class)) {
                        Integer val = rnd.nextInt();
                        aMethod.invoke(obj, val);
                    } else if (argtype.equals(String.class)) {
                        String val = String.valueOf(rnd.nextInt()); // TODO generate better string
                        aMethod.invoke(obj, val);
                    } else if (argtype.equals(Date.class)) {
                        Date val = Date.from(Instant.ofEpochMilli(rnd.nextLong()));
                        aMethod.invoke(obj, val);
                    } else if (argtype.equals(Timestamp.class)) {
                        Timestamp val = Timestamp.from(Instant.ofEpochMilli(rnd.nextLong()));
                        aMethod.invoke(obj, val);
                    } else if (argtype.equals(OffsetDateTime.class)) {
                        OffsetDateTime val = Instant.ofEpochMilli(rnd.nextLong()).atOffset(ZoneOffset.UTC);
                        aMethod.invoke(obj, val);
                    } else if (argtype.equals(Boolean.class)) {
                        Boolean val = rnd.nextBoolean();
                        aMethod.invoke(obj, val);
                    } else {
                        log.warn("fillRandom: not calling setter method {}", aMethod);
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InternalError(e);
        }
    }

    public void testMapper(Class<?> pojoType, Class<?> dtoType) {
        try {
            Object item = pojoType.getDeclaredConstructor().newInstance();
            fillRandom(item, pojoType);
            log.info("Generated item    = {}", item);
            Object dto = mapper.map(item, dtoType);
            log.info("Converted to dto  = {}", dto);
            Object pojo = mapper.map(dto, pojoType);
            log.info("Converted to pojo = {}", pojo);
            assertThat(pojo).isEqualTo(item);
        } catch (Exception e) {
            throw new InternalError(e);
        }
    }

    public void compare(Object item, Class<?> dtoType) {
        try {
            log.info("Generated item    = {}", item);
            Object dto = mapper.map(item, dtoType);
            log.info("Converted to dto  = {}", dto);
            Object pojo = mapper.map(dto, item.getClass());
            log.info("Converted to pojo = {}", pojo);
            assertThat(pojo).isEqualTo(item);
        } catch (Exception e) {
            throw new InternalError(e);
        }
    }

    @Test
    public void testGlobalTags() {
        testMapper(GlobalTag.class, GlobalTagDto.class);
    }

    @Test
    public void testTags() {
        testMapper(Tag.class, TagDto.class);
    }

    @Test
    public void testIovs() {
        IovId id = new IovId();
        fillRandom(id, IovId.class);
        Iov iov = new Iov();
        fillRandom(iov, Iov.class);
        iov.id(id);
        compare(iov, IovDto.class);
    }

    @Test
    public void testGlobalTagMaps() {
        GlobalTagMapId id = new GlobalTagMapId();
        fillRandom(id, GlobalTagMapId.class);
        Tag tag = new Tag();
        fillRandom(tag, Tag.class);
        GlobalTagMap map = new GlobalTagMap();
        fillRandom(map, GlobalTagMap.class);
        map.tag(tag).id(id);
        compare(map, GlobalTagMapDto.class);
    }

    @Test
    public void testRun() {
        testMapper(RunLumiInfo.class, RunLumiInfoDto.class);
    }

    @Test
    public void testCrestFolders() {
        testMapper(CrestFolders.class, FolderDto.class);
    }

}
