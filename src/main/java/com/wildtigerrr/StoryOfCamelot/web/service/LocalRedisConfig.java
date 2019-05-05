package com.wildtigerrr.StoryOfCamelot.web.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DefaultPropertiesPersister;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

@Configuration
public class LocalRedisConfig {

    @Bean
    public RedisConnectionFactory jedisConnectionFactory(){
        addRedisProperties();
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        return new JedisConnectionFactory(poolConfig);
    }

    private void addRedisProperties() {
        try {
            URI redisURI = new URI(System.getenv("REDIS_URL"));
            Properties props = new Properties();
            props.setProperty("spring.data.redis.repositories.enabled", "true");
            props.setProperty("spring.redis.host", redisURI.getHost());
            props.setProperty("spring.redis.password", redisURI.getUserInfo().split(":",2)[1]);
            props.setProperty("spring.redis.ssl", "true");
//            props.setProperty("spring.redis.pool.max-active", "10");
//            props.setProperty("spring.redis.pool.max-idle", "5");
            props.setProperty("spring.redis.pool.max-wait", "30000");
            props.setProperty("spring.redis.port", String.valueOf(redisURI.getPort()));
            File f = new File("application.properties");
            OutputStream out = new FileOutputStream( f );
            // write into it
            DefaultPropertiesPersister p = new DefaultPropertiesPersister();
            p.store(props, out, "Redis Settings");
        } catch (Exception e ) {
            e.printStackTrace();
        }
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