package com.wildtigerrr.StoryOfCamelot.web.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class LocalRedisConfig {

    @Bean
    public RedisConnectionFactory jedisConnectionFactory(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        return new JedisConnectionFactory(poolConfig);
    }

//    @Bean
//    public JedisPool getJedisPool() {
//        try {
//            String link = System.getenv("REDIS_URL");
//            System.out.println("Link:");
//            System.out.println(link);
//            URI redisURI = new URI(link);
//            System.out.println("URI");
//            System.out.println(redisURI);
//
//            System.out.println("UInfo");
//            System.out.println(redisURI.getUserInfo());
//            return new JedisPool(new JedisPoolConfig(),
//                    redisURI.getHost(),
//                    redisURI.getPort(),
//                    Protocol.DEFAULT_TIMEOUT,
//                    redisURI.getUserInfo().split(":",2)[1]);
//        } catch (URISyntaxException e) {
//            throw new RuntimeException("Redis couldn't be configured from URL in REDISTOGO_URL env var:"+
//                    System.getenv("REDISTOGO_URL"));
//        }
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(jedisConnectionFactory());
//        return template;
//    }

}