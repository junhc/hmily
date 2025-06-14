package com.hmily.tcc.demo.springcloud.account.entity;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Optional;

/**
 * Name: Chunk
 * Function:
 *
 * @Author: K.K
 * Create Time: 2025/6/14 17:06
 * Modified By:
 * Modified Time:
 * Description:
 * Version:
 */
public class Chunk implements Serializable {
    private static final String CHUNK_KEY_PREFIX = "json:chunk:";
    private static final String PROGRESS_KEY_PREFIX = "json:progress:";

    private String prefix;
    private String uuid;
    private Integer index;
    private String chunk;
    private Integer total;
    private Long length;

    public String getPrefix() {
        return Optional.ofNullable(this.getPrefix()).orElse("json");
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getChunk() {
        return chunk;
    }

    public void setChunk(String chunk) {
        this.chunk = chunk;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getChunkKey() {
        return getKey(this.getPrefix(), "chunk", this.getUuid() + "_" + this.getIndex());
    }

    public String getProgressKey() {
        return getKey(this.getPrefix(), "progress", this.getUuid());
    }

    public static String getKey(Object... values) {
        return StringUtils.join(values, ":");
    }

    public static Chunk of(String prefix, String uuid, Integer index) {
        Chunk chunk = new Chunk();
        chunk.setPrefix(prefix);
        chunk.setUuid(uuid);
        chunk.setIndex(index);
        return chunk;
    }

    public static Chunk of(String uuid, Integer index) {
        return of(null, uuid, index);
    }

    public static Chunk of(String uuid) {
        return of(null, uuid, null);
    }
}
