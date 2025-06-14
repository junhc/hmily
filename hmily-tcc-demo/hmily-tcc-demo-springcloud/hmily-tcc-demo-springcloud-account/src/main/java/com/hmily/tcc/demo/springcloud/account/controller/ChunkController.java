package com.hmily.tcc.demo.springcloud.account.controller;

import com.alibaba.fastjson.JSONObject;
import com.hmily.tcc.demo.springcloud.account.entity.Chunk;
import com.hmily.tcc.demo.springcloud.account.entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Name: ChunkController
 * Function:
 *
 * @Author: K.K
 * Create Time: 2025/6/14 17:04
 * Modified By:
 * Modified Time:
 * Description:
 * Version:
 */
@RestController
@RequestMapping("/chunk")
public class ChunkController {
    private static final long EXPIRE_TIME = 600; // 10分钟
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 上传JSON分块
     */
    @PostMapping("/upload")
    public Result upload(@RequestBody Chunk chunk) {
        // 保存分块数据
        this.addChunk(chunk);

        // 检查是否所有分块都已上传
        boolean isAll = true;
        List<Integer> indexes = this.getChunkIndexes(chunk.getUuid());
        for (int i = 1; i <= chunk.getTotal(); i++) {
            if (!indexes.contains(i)) {
                isAll = false;
                break;
            }
        }

        if (isAll) {
            // 合并所有分块
            String json = this.getChunk(chunk.getUuid(), indexes.toArray(new Integer[0]));
            if (chunk.getLength() == json.length()) {
                // 处理完整JSON
                JSONObject object = JSONObject.parseObject(json);

                // 清理分块数据
                // cleanUp(chunk.getUuid(), chunk.getTotal());
                return Result.ok(object.getLong("id"));
            }
        }

        return Result.ok(null);
    }

    public void addChunk(Chunk chunk) {
        redisTemplate.executePipelined((RedisCallback<Object>) pipeline -> {
            // 存储当前分块
            pipeline.set(chunk.getChunkKey().getBytes(), chunk.getChunk().getBytes(), Expiration.seconds(EXPIRE_TIME), RedisStringCommands.SetOption.SET_IF_ABSENT);
            // 记录进度
            String progressKey = chunk.getProgressKey();
            pipeline.sAdd(progressKey.getBytes(), String.valueOf(chunk.getIndex()).getBytes());
            pipeline.expire(progressKey.getBytes(), EXPIRE_TIME);
            return null;
        });

    }

    public String getChunk(String uuid, Integer... indexes) {
        List<Object> results = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> pipeline) throws DataAccessException {
                Chunk chunk = Chunk.of(uuid);
                for (Integer index : indexes) {
                    chunk.setIndex(index);
                    pipeline.opsForValue().get(chunk.getChunkKey());
                }
                return null;
            }
        }, new StringRedisSerializer());

        return StringUtils.join(results.toArray());
    }

    public List<Integer> getChunkIndexes(String uuid) {
        String progressKey = Chunk.of(uuid).getProgressKey();
        return (List<Integer>) Optional.ofNullable(redisTemplate.opsForSet().members(progressKey)).map(s -> s.stream().sorted().collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    public void cleanUp(String uuid, Integer total) {
        Chunk chunk = Chunk.of(uuid);
        // 删除分块
        for (int i = 1; i <= total; i++) {
            chunk.setIndex(i);
            redisTemplate.delete(chunk.getChunkKey());
        }

        // 删除进度信息
        redisTemplate.delete(chunk.getProgressKey());
    }


    /**
     * 获取已上传的分块信息
     */
    @GetMapping("/progress")
    public Result getProgress(@RequestParam String uuid) {
        return Result.ok(this.getChunkIndexes(uuid));
    }

    @GetMapping("/get")
    public Result get(@RequestParam String uuid) {
        List<Integer> indexes = this.getChunkIndexes(uuid);
        String json = getChunk(uuid, indexes.toArray(new Integer[0]));
        return Result.ok(JSONObject.parseObject(json));
    }
}
