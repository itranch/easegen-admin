package cn.iocoder.yudao.module.system.service.mail.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.system.controller.admin.mail.vo.template.MailTemplateCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.mail.vo.template.MailTemplatePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.mail.vo.template.MailTemplateUpdateReqVO;
import cn.iocoder.yudao.module.system.convert.mail.MailTemplateConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.mail.MailTemplateDO;
import cn.iocoder.yudao.module.system.dal.mysql.mail.MailTemplateMapper;
import cn.iocoder.yudao.module.system.mq.producer.mail.MailProducer;
import cn.iocoder.yudao.module.system.service.mail.MailTemplateService;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

/**
 * 邮箱模版 Service 实现类
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
@Service
@Validated
@Slf4j
public class MailTemplateServiceImpl implements MailTemplateService {

    @Resource
    private MailTemplateMapper mailTemplateMapper;

    @Resource
    private MailProducer mailProducer;

    /**
     * 邮件模板缓存
     * key：邮件模版标识 {@link MailTemplateDO#getCode()}
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    private volatile Map<String, MailTemplateDO> mailTemplateCache;

    @Override
    @PostConstruct
    public void initLocalCache() {
        // 第一步：查询数据
        List<MailTemplateDO> templates = mailTemplateMapper.selectList();
        log.info("[initLocalCache][缓存邮件模版，数量:{}]", templates.size());

        // 第二步：构建缓存
        mailTemplateCache = convertMap(templates, MailTemplateDO::getCode);
    }

    @Override
    public Long createMailTemplate(MailTemplateCreateReqVO createReqVO) {
        // 校验 code 是否唯一
        validateCodeUnique(null, createReqVO.getCode());

        // 插入
        MailTemplateDO template = MailTemplateConvert.INSTANCE.convert(createReqVO);
        mailTemplateMapper.insert(template);
        // 发送刷新消息
        mailProducer.sendMailTemplateRefreshMessage();
        return template.getId();
    }

    @Override
    public void updateMailTemplate(@Valid MailTemplateUpdateReqVO updateReqVO) {
        // 校验是否存在
        validateMailTemplateExists(updateReqVO.getId());
        // 校验 code 是否唯一
        validateCodeUnique(updateReqVO.getId(),updateReqVO.getCode());

        // 更新
        MailTemplateDO updateObj = MailTemplateConvert.INSTANCE.convert(updateReqVO);
        mailTemplateMapper.updateById(updateObj);
        // 发送刷新消息
        mailProducer.sendMailTemplateRefreshMessage();
    }

    @VisibleForTesting
    public void validateCodeUnique(Long id, String code) {
        MailTemplateDO template = mailTemplateMapper.selectByCode(code);
        if (template == null) {
            return;
        }
        // 存在 template 记录的情况下
        if (id == null // 新增时，说明重复
                || ObjUtil.notEqual(id, template.getId())) { // 更新时，如果 id 不一致，说明重复
            throw exception(MAIL_TEMPLATE_CODE_EXISTS);
        }
    }

    @Override
    public void deleteMailTemplate(Long id) {
        // 校验是否存在
        validateMailTemplateExists(id);

        // 删除
        mailTemplateMapper.deleteById(id);
        // 发送刷新消息
        mailProducer.sendMailTemplateRefreshMessage();
    }

    @Override
    public MailTemplateDO getMailTemplate(Long id) {return mailTemplateMapper.selectById(id);}

    @Override
    public PageResult<MailTemplateDO> getMailTemplatePage(MailTemplatePageReqVO pageReqVO) {
        return mailTemplateMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MailTemplateDO> getMailTemplateList() {return mailTemplateMapper.selectList();}

    @Override
    public MailTemplateDO getMailTemplateByCodeFromCache(String code) {
        return mailTemplateCache.get(code);
    }

    @Override
    public String formatMailTemplateContent(String content, Map<String, String> params) {
        return StrUtil.format(content, params);
    }

    private void validateMailTemplateExists(Long id) {
        if (mailTemplateMapper.selectById(id) == null) {
            throw exception(MAIL_TEMPLATE_NOT_EXISTS);
        }
    }

    @Override
    public long countByAccountId(Long accountId) {
        return mailTemplateMapper.selectCountByAccountId(accountId);
    }

}
