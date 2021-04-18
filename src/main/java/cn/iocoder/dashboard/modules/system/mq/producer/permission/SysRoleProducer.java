package cn.iocoder.dashboard.modules.system.mq.producer.permission;

import cn.iocoder.dashboard.framework.redis.core.util.RedisMessageUtils;
import cn.iocoder.dashboard.modules.system.mq.message.permission.SysRoleRefreshMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Role 角色相关消息的 Producer
 *
 * @author 芋道源码
 */
@Component
public class SysRoleProducer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送 {@link SysRoleRefreshMessage} 消息
     */
    public void sendRoleRefreshMessage() {
        SysRoleRefreshMessage message = new SysRoleRefreshMessage();
        RedisMessageUtils.sendChannelMessage(stringRedisTemplate, message);
    }

}
