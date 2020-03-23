package com.wildtigerrr.StoryOfCamelot.bin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@EnableRedisRepositories(basePackages = "com.wildtigerrr.StoryOfCamelot.database.redis.schema")
@Profile("!test")
public class RedisConfig {

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        URI redisURI = null;
        try {
            redisURI = new URI(System.getenv("REDIS_URL"));
        } catch (URISyntaxException ignored) {}
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisURI.getHost(), redisURI.getPort());
        redisStandaloneConfiguration.setPassword(redisURI.getUserInfo().split(":")[1]);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }

}

