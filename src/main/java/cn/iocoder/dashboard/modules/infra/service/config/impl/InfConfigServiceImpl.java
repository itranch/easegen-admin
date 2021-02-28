package cn.iocoder.dashboard.modules.infra.service.config.impl;

import cn.iocoder.dashboard.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.dashboard.common.pojo.PageResult;
import cn.iocoder.dashboard.modules.infra.controller.config.vo.InfConfigCreateReqVO;
import cn.iocoder.dashboard.modules.infra.controller.config.vo.InfConfigExportReqVO;
import cn.iocoder.dashboard.modules.infra.controller.config.vo.InfConfigPageReqVO;
import cn.iocoder.dashboard.modules.infra.controller.config.vo.InfConfigUpdateReqVO;
import cn.iocoder.dashboard.modules.infra.convert.config.InfConfigConvert;
import cn.iocoder.dashboard.modules.infra.dal.mysql.config.InfConfigMapper;
import cn.iocoder.dashboard.modules.infra.dal.dataobject.config.InfConfigDO;
import cn.iocoder.dashboard.modules.infra.enums.config.InfConfigTypeEnum;
import cn.iocoder.dashboard.modules.infra.mq.producer.config.InfConfigProducer;
import cn.iocoder.dashboard.modules.infra.service.config.InfConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

import static cn.iocoder.dashboard.modules.infra.enums.InfErrorCodeConstants.*;

/**
 * 参数配置 Service 实现类
 */
@Service
@Slf4j
public class InfConfigServiceImpl implements InfConfigService {

    @Resource
    private InfConfigMapper configMapper;

    @Resource
    private InfConfigProducer configProducer;

    @Override
    public PageResult<InfConfigDO> getConfigPage(InfConfigPageReqVO reqVO) {
        return configMapper.selectPage(reqVO);
    }

    @Override
    public List<InfConfigDO> getConfigList(InfConfigExportReqVO reqVO) {
        return configMapper.selectList(reqVO);
    }

    @Override
    public InfConfigDO getConfig(Long id) {
        return configMapper.selectById(id);
    }

    @Override
    public InfConfigDO getConfigByKey(String key) {
        return configMapper.selectByKey(key);
    }

    @Override
    public Long createConfig(InfConfigCreateReqVO reqVO) {
        // 校验正确性
        checkCreateOrUpdate(null, reqVO.getKey());
        // 插入参数配置
        InfConfigDO config = InfConfigConvert.INSTANCE.convert(reqVO);
        config.setType(InfConfigTypeEnum.CUSTOM.getType());
        configMapper.insert(config);
        // 发送刷新消息
        configProducer.sendConfigRefreshMessage();
        return config.getId();
    }

    @Override
    public void updateConfig(InfConfigUpdateReqVO reqVO) {
        // 校验正确性
        checkCreateOrUpdate(reqVO.getId(), null); // 不允许更新 key
        // 更新参数配置
        InfConfigDO updateObj = InfConfigConvert.INSTANCE.convert(reqVO);
        configMapper.updateById(updateObj);
        // 发送刷新消息
        configProducer.sendConfigRefreshMessage();
    }

    @Override
    public void deleteConfig(Long id) {
        // 校验配置存在
        InfConfigDO config = checkConfigExists(id);
        // 内置配置，不允许删除
        if (InfConfigTypeEnum.SYSTEM.getType().equals(config.getType())) {
            throw ServiceExceptionUtil.exception(CONFIG_CAN_NOT_DELETE_SYSTEM_TYPE);
        }
        // 删除
        configMapper.deleteById(id);
        // 发送刷新消息
        configProducer.sendConfigRefreshMessage();
    }

    private void checkCreateOrUpdate(Long id, String key) {
        // 校验自己存在
        checkConfigExists(id);
        // 校验参数配置 key 的唯一性
        checkConfigKeyUnique(id, key);
    }

    private InfConfigDO checkConfigExists(Long id) {
        if (id == null) {
            return null;
        }
        InfConfigDO config = configMapper.selectById(id);
        if (config == null) {
            throw ServiceExceptionUtil.exception(CONFIG_NOT_FOUND);
        }
        return config;
    }

    private void checkConfigKeyUnique(Long id, String key) {
        InfConfigDO config = configMapper.selectByKey(key);
        if (config == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的参数配置
        if (id == null) {
            throw ServiceExceptionUtil.exception(CONFIG_KEY_DUPLICATE);
        }
        if (!config.getId().equals(id)) {
            throw ServiceExceptionUtil.exception(CONFIG_KEY_DUPLICATE);
        }
    }

}
