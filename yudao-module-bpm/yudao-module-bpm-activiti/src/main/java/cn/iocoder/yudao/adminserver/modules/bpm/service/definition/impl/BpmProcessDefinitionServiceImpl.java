package cn.iocoder.yudao.adminserver.modules.bpm.service.definition.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.adminserver.modules.bpm.controller.definition.vo.process.BpmProcessDefinitionListReqVO;
import cn.iocoder.yudao.adminserver.modules.bpm.controller.definition.vo.process.BpmProcessDefinitionPageItemRespVO;
import cn.iocoder.yudao.adminserver.modules.bpm.controller.definition.vo.process.BpmProcessDefinitionPageReqVO;
import cn.iocoder.yudao.adminserver.modules.bpm.controller.definition.vo.process.BpmProcessDefinitionRespVO;
import cn.iocoder.yudao.adminserver.modules.bpm.convert.definition.BpmProcessDefinitionConvert;
import cn.iocoder.yudao.adminserver.modules.bpm.dal.dataobject.definition.BpmProcessDefinitionExtDO;
import cn.iocoder.yudao.adminserver.modules.bpm.dal.mysql.definition.BpmProcessDefinitionExtMapper;
import cn.iocoder.yudao.adminserver.modules.bpm.service.definition.BpmProcessDefinitionService;
import cn.iocoder.yudao.adminserver.modules.bpm.service.definition.dto.BpmProcessDefinitionCreateReqDTO;
import cn.iocoder.yudao.coreservice.modules.bpm.api.form.BpmFormServiceApi;
import cn.iocoder.yudao.coreservice.modules.bpm.api.form.dto.BpmFormDTO;
import cn.iocoder.yudao.framework.activiti.core.util.ActivitiUtils;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.persistence.entity.SuspensionState;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;

import static cn.iocoder.yudao.adminserver.modules.bpm.enums.BpmErrorCodeConstants.PROCESS_DEFINITION_KEY_NOT_MATCH;
import static cn.iocoder.yudao.adminserver.modules.bpm.enums.BpmErrorCodeConstants.PROCESS_DEFINITION_NAME_NOT_MATCH;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static java.util.Collections.emptyList;

/**
 * 流程定义实现
 * 主要进行 Activiti {@link ProcessDefinition} 和 {@link Deployment} 的维护
 *
 * @author yunlongn
 * @author ZJQ
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class BpmProcessDefinitionServiceImpl implements BpmProcessDefinitionService {

    private static final String BPMN_FILE_SUFFIX = ".bpmn";

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private BpmFormServiceApi bpmFormServiceApi;

    @Resource
    private BpmProcessDefinitionExtMapper processDefinitionMapper;

    @Override
    public PageResult<BpmProcessDefinitionPageItemRespVO> getProcessDefinitionPage(BpmProcessDefinitionPageReqVO pageVO) {
        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery();
        if (StrUtil.isNotBlank(pageVO.getKey())) {
            definitionQuery.processDefinitionKey(pageVO.getKey());
        }
        // 执行查询
        List<ProcessDefinition> processDefinitions = definitionQuery.orderByProcessDefinitionVersion().desc()
                .listPage(PageUtils.getStart(pageVO), pageVO.getPageSize());
        if (CollUtil.isEmpty(processDefinitions)) {
            return new PageResult<>(emptyList(), definitionQuery.count());
        }

        // 获得 Deployment Map
        Set<String> deploymentIds = new HashSet<>();
        processDefinitions.forEach(definition -> addIfNotNull(deploymentIds, definition.getDeploymentId()));
        Map<String, Deployment> deploymentMap = getDeploymentMap(deploymentIds);

        // 获得 BpmProcessDefinitionDO Map
        List<BpmProcessDefinitionExtDO> processDefinitionDOs = processDefinitionMapper.selectListByProcessDefinitionIds(
                convertList(processDefinitions, ProcessDefinition::getId));
        Map<String, BpmProcessDefinitionExtDO> processDefinitionDOMap = convertMap(processDefinitionDOs,
                BpmProcessDefinitionExtDO::getProcessDefinitionId);

        // 获得 Form Map
        Set<Long> formIds = convertSet(processDefinitionDOs, BpmProcessDefinitionExtDO::getFormId);
        Map<Long, BpmFormDTO> formMap = bpmFormServiceApi.getFormMap(formIds);

        // 拼接结果
        long definitionCount = definitionQuery.count();
        return new PageResult<>(BpmProcessDefinitionConvert.INSTANCE.convertList(processDefinitions, deploymentMap,
                processDefinitionDOMap, formMap), definitionCount);
    }

    @Override
    public List<BpmProcessDefinitionRespVO> getProcessDefinitionList(BpmProcessDefinitionListReqVO listReqVO) {
        // 拼接查询条件
        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery();
        if (Objects.equals(SuspensionState.SUSPENDED.getStateCode(), listReqVO.getSuspensionState())) {
            definitionQuery.suspended();
        } else if (Objects.equals(SuspensionState.ACTIVE.getStateCode(), listReqVO.getSuspensionState())) {
            definitionQuery.active();
        }
        // 执行查询
        List<ProcessDefinition> processDefinitions = definitionQuery.list();

        // 获得 BpmProcessDefinitionDO Map
        List<BpmProcessDefinitionExtDO> processDefinitionDOs = processDefinitionMapper.selectListByProcessDefinitionIds(
                convertList(processDefinitions, ProcessDefinition::getId));
        Map<String, BpmProcessDefinitionExtDO> processDefinitionDOMap = convertMap(processDefinitionDOs,
                BpmProcessDefinitionExtDO::getProcessDefinitionId);
        // 执行查询，并返回
        return BpmProcessDefinitionConvert.INSTANCE.convertList3(processDefinitions, processDefinitionDOMap);
    }

    @Override
    public String getProcessDefinitionBpmnXML(String id) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(id);
        if (bpmnModel == null) {
            return null;
        }
        return ActivitiUtils.getBpmnXml(bpmnModel);
    }

    @Override
    public BpmnModel getBpmnModel(String processDefinitionId) {
        return repositoryService.getBpmnModel(processDefinitionId);
    }

    @Override
    public ProcessDefinition getProcessDefinition(String id) {
        return repositoryService.getProcessDefinition(id);
    }

    @Override
    public ProcessDefinition getProcessDefinition2(String id) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionId(id).singleResult();
    }

    @Override
    public ProcessDefinition getActiveProcessDefinition(String key) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).active().singleResult();
    }

    @Override
    public BpmProcessDefinitionExtDO getProcessDefinitionExt(String id) {
        return processDefinitionMapper.selectByProcessDefinitionId(id);
    }

    @Override
    public Deployment getDeployment(String id) {
        if (StrUtil.isEmpty(id)) {
            return null;
        }
        return repositoryService.createDeploymentQuery().deploymentId(id).singleResult();
    }

    @Override
    public List<Deployment> getDeployments(Set<String> ids) {
        if (CollUtil.isEmpty(ids)) {
            return emptyList();
        }
        List<Deployment> list = new ArrayList<>(ids.size());
        for (String id : ids) {
            addIfNotNull(list, getDeployment(id));
        }
        return list;
    }

    @Override
    public ProcessDefinition getProcessDefinitionByDeploymentId(String deploymentId) {
        if (StrUtil.isEmpty(deploymentId)) {
            return null;
        }
        return repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
    }

    @Override
    public List<ProcessDefinition> getProcessDefinitionListByDeploymentIds(Set<String> deploymentIds) {
        if (CollUtil.isEmpty(deploymentIds)) {
            return emptyList();
        }
        return repositoryService.createProcessDefinitionQuery().deploymentIds(deploymentIds).list();
    }

    @Override
    public boolean isProcessDefinitionEquals(BpmProcessDefinitionCreateReqDTO createReqDTO) {
        // 校验 name、description 是否更新
        ProcessDefinition oldProcessDefinition = getActiveProcessDefinition(createReqDTO.getKey());
        if (oldProcessDefinition == null) {
            return false;
        }
        BpmProcessDefinitionExtDO oldProcessDefinitionExt = getProcessDefinitionExt(oldProcessDefinition.getId());
        if (!StrUtil.equals(createReqDTO.getName(), oldProcessDefinition.getName())
                || !StrUtil.equals(createReqDTO.getDescription(), oldProcessDefinitionExt.getDescription())
                || !StrUtil.equals(createReqDTO.getCategory(), oldProcessDefinition.getCategory())) {
            return false;
        }
        // 校验 form 信息是否更新
        if (!ObjectUtil.equal(createReqDTO.getFormType(), oldProcessDefinitionExt.getFormType())
                || !ObjectUtil.equal(createReqDTO.getFormId(), oldProcessDefinitionExt.getFormId())
                || !ObjectUtil.equal(createReqDTO.getFormConf(), oldProcessDefinitionExt.getFormConf())
                || !ObjectUtil.equal(createReqDTO.getFormFields(), oldProcessDefinitionExt.getFormFields())
                || !ObjectUtil.equal(createReqDTO.getFormCustomCreatePath(), oldProcessDefinitionExt.getFormCustomCreatePath())
                || !ObjectUtil.equal(createReqDTO.getFormCustomViewPath(), oldProcessDefinitionExt.getFormCustomViewPath())) {
            return false;
        }
        // 校验 BPMN XML 信息
        BpmnModel newModel = ActivitiUtils.buildBpmnModel(createReqDTO.getBpmnBytes());
        BpmnModel oldModel = getBpmnModel(oldProcessDefinition.getId());
        if (!ActivitiUtils.equals(oldModel, newModel)) {
            return false;
        }
        // 最终发现都一致，则返回 true
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 因为进行多个 activiti 操作，所以开启事务
    public String createProcessDefinition(BpmProcessDefinitionCreateReqDTO createReqDTO) {
        // 创建 Deployment 部署
        Deployment deploy = repositoryService.createDeployment()
                .key(createReqDTO.getKey()).name(createReqDTO.getName()).category(createReqDTO.getCategory())
                .addBytes(createReqDTO.getKey() + BPMN_FILE_SUFFIX, createReqDTO.getBpmnBytes())
                .deploy();

        // 设置 ProcessDefinition 的 category 分类
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        repositoryService.setProcessDefinitionCategory(definition.getId(), createReqDTO.getCategory());
        // 注意 1，ProcessDefinition 的 key 和 name 是通过 BPMN 中的 <bpmn2:process /> 的 id 和 name 决定
        // 注意 2，目前该项目的设计上，需要保证 Model、Deployment、ProcessDefinition 使用相同的 key，保证关联性。
        //          否则，会导致 ProcessDefinition 的分页无法查询到。
        if (!Objects.equals(definition.getKey(), createReqDTO.getKey())) {
            throw exception(PROCESS_DEFINITION_KEY_NOT_MATCH, createReqDTO.getKey(), definition.getKey());
        }
        if (!Objects.equals(definition.getName(), createReqDTO.getName())) {
            throw exception(PROCESS_DEFINITION_NAME_NOT_MATCH, createReqDTO.getName(), definition.getName());
        }

        // 插入拓展表
        BpmProcessDefinitionExtDO definitionDO = BpmProcessDefinitionConvert.INSTANCE.convert2(createReqDTO)
                .setProcessDefinitionId(definition.getId());
        processDefinitionMapper.insert(definitionDO);
        return definition.getId();
    }

    @Override
    public void updateProcessDefinitionState(String id, Integer state) {
        // 激活
        if (Objects.equals(SuspensionState.ACTIVE.getStateCode(), state)) {
            repositoryService.activateProcessDefinitionById(id, false, null);
            return;
        }
        // 挂起
        if (Objects.equals(SuspensionState.SUSPENDED.getStateCode(), state)) {
            // suspendProcessInstances = false，进行中的任务，不进行挂起。
            // 原因：只要新的流程不允许发起即可，老流程继续可以执行。
            repositoryService.suspendProcessDefinitionById(id, false, null);
            return;
        }
        log.error("[updateProcessDefinitionState][流程定义({}) 修改未知状态({})]", id, state);
    }

}
