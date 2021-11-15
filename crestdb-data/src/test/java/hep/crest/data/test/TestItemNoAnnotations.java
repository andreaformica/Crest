package hep.crest.data.test;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import hep.crest.data.serializers.ByteArrayDeserializer;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Date;

public class TestItemNoAnnotations {

    @JsonDeserialize(using = ByteArrayDeserializer.class)
    private byte[] data;
    private OffsetDateTime modtime;
    private Timestamp instime;
    private Date insdate;
    private String name;

    /**
     *
     */
    public TestItemNoAnnotations() {
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Timestamp getInstime() {
        return instime;
    }

    public void setInstime(Timestamp instime) {
        this.instime = instime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the insdate
     */
    public Date getInsdate() {
        return insdate;
    }

    /**
     * @param insdate the insdate to set
     */
    public void setInsdate(Date insdate) {
        this.insdate = insdate;
    }

    public OffsetDateTime getModtime() {
        return modtime;
    }

    public void setModtime(OffsetDateTime modtime) {
        this.modtime = modtime;
    }

    @Override
    public String toString() {
        return "TestItemNoAnnotations{" +
               "data=" + Arrays.toString(data) +
               ", modtime=" + modtime +
               ", instime=" + instime +
               ", insdate=" + insdate +
               ", name='" + name + '\'' +
               '}';
    }
}
