package cn.iocoder.dashboard.config;

import com.github.fppt.jedismock.RedisServer;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;

@Configuration
@Lazy(false) // 禁用懒加载，因为需要保证 Redis Server 必须先启动
@EnableConfigurationProperties(RedisProperties.class)
@AutoConfigureBefore({RedisAutoConfiguration.class, RedissonAutoConfiguration.class}) // 在 Redis 自动配置前，进行初始化
public class RedisTestConfiguration {

//    /**
//     * 创建模拟的 Redis Server 服务器
//     */
//    @Bean(initMethod = "start", destroyMethod = "stop")
//    public RedisServer redisServer(RedisProperties properties) throws IOException {
//        return new RedisServer(properties.getPort());
//    }

    /**
     * 创建模拟的 Redis Server 服务器
     */
    @Bean(destroyMethod = "stop")
    public RedisServer redisServer(RedisProperties properties) throws IOException {
        RedisServer redisServer = new RedisServer(properties.getPort());
        try {
            redisServer.start();
        } catch (Exception ignore) {}
        return redisServer;
    }

}
