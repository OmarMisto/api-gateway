package com.example.gateway.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.lettuce.core.RedisClient;

import java.time.Duration;
import java.util.function.Supplier;

@Configuration
public class RateLimitConfig {
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Bean
    public LettuceBasedProxyManager<String> lettuceBasedProxyManager(){
        RedisClient redisClient=RedisClient.create(RedisURI.create(host,port));
        StatefulRedisConnection<String,byte[]> connection=redisClient
                .connect(RedisCodec.of(StringCodec.UTF8,ByteArrayCodec.INSTANCE));
        return LettuceBasedProxyManager.builderFor(connection).build();
    }

}
