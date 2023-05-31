package com.lx.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {


    /*
    *  RedisTemplate
    * */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // redis key的序列化
        StringRedisSerializer keyRedisSerializer = new StringRedisSerializer();
        // redis value的序列化
        GenericJackson2JsonRedisSerializer valueRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(keyRedisSerializer);
        redisTemplate.setValueSerializer(valueRedisSerializer);
        redisTemplate.setHashKeySerializer(keyRedisSerializer);
        redisTemplate.setHashValueSerializer(valueRedisSerializer);
        return redisTemplate;
    }

    /**
     * 使用RedisTemplate<String, Object> 是需要注意其序列化的方式
     * @param redisConnectionFactory
     * @param redisSerializer
     * @return
     */
/*    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, RedisSerializer<Object> redisSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //redis Key 序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //redis Value 序列化
        redisTemplate.setValueSerializer(redisSerializer);
        //redis HashKey 序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //redis HashValue 序列化
        redisTemplate.setHashValueSerializer(redisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }*/

    /**
     * 更换redis的序列化形式为Jackson
     * @param objectMapper
     * @return
     */
/*    @Bean
    public RedisSerializer<Object> redisSerializer(ObjectMapper objectMapper) {
        //创建JSON序列化器
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(objectMapper);
        return serializer;
    }*/
}
