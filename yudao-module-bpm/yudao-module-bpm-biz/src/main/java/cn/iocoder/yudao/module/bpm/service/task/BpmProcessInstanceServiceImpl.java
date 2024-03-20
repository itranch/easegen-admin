package cn.iocoder.yudao.module.bpm.service.task;import cn.hutool.core.collection.CollUtil;import cn.hutool.core.lang.Assert;import cn.hutool.core.util.ArrayUtil;import cn.hutool.core.util.StrUtil;import cn.iocoder.yudao.framework.common.pojo.PageResult;import cn.iocoder.yudao.framework.common.util.date.DateUtils;import cn.iocoder.yudao.framework.common.util.number.NumberUtils;import cn.iocoder.yudao.framework.common.util.object.PageUtils;import cn.iocoder.yudao.framework.flowable.core.context.FlowableContextHolder;import cn.iocoder.yudao.framework.flowable.core.util.FlowableUtils;import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.instance.BpmProcessInstanceCancelReqVO;import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.instance.BpmProcessInstanceCreateReqVO;import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.instance.BpmProcessInstanceMyPageReqVO;import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.instance.BpmProcessInstanceRespVO;import cn.iocoder.yudao.module.bpm.convert.task.BpmProcessInstanceConvert;import cn.iocoder.yudao.module.bpm.dal.dataobject.definition.BpmProcessDefinitionInfoDO;import cn.iocoder.yudao.module.bpm.enums.task.BpmDeleteReasonEnum;import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceStatusEnum;import cn.iocoder.yudao.module.bpm.framework.bpm.core.event.BpmProcessInstanceResultEventPublisher;import cn.iocoder.yudao.module.bpm.framework.flowable.core.enums.BpmConstants;import cn.iocoder.yudao.module.bpm.service.definition.BpmProcessDefinitionService;import cn.iocoder.yudao.module.bpm.service.message.BpmMessageService;import cn.iocoder.yudao.module.system.api.dept.DeptApi;import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;import cn.iocoder.yudao.module.system.api.user.AdminUserApi;import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;import jakarta.annotation.Resource;import jakarta.validation.Valid;import lombok.extern.slf4j.Slf4j;import org.flowable.engine.HistoryService;import org.flowable.engine.RuntimeService;import org.flowable.engine.delegate.event.FlowableCancelledEvent;import org.flowable.engine.history.HistoricProcessInstance;import org.flowable.engine.history.HistoricProcessInstanceQuery;import org.flowable.engine.repository.ProcessDefinition;import org.flowable.engine.runtime.ProcessInstance;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import org.springframework.validation.annotation.Validated;import java.util.*;import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import static cn.iocoder.yudao.module.bpm.enums.ErrorCodeConstants.*;/** * 流程实例 Service 实现类 * * ProcessDefinition & ProcessInstance & Execution & Task 的关系： *  1. <a href="https://blog.csdn.net/bobozai86/article/details/105210414" /> * * HistoricProcessInstance & ProcessInstance 的关系： *  1. <a href=" https://my.oschina.net/843294669/blog/71902" /> * * 简单来说，前者 = 历史 + 运行中的流程实例，后者仅是运行中的流程实例 * * @author 芋道源码 */@Service@Validated@Slf4jpublic class BpmProcessInstanceServiceImpl implements BpmProcessInstanceService {    @Resource    private RuntimeService runtimeService;    @Resource    private HistoryService historyService;    @Resource    private BpmProcessDefinitionService processDefinitionService;    @Resource    private BpmMessageService messageService;    @Resource    private BpmProcessInstanceResultEventPublisher processInstanceResultEventPublisher;    @Resource    private AdminUserApi adminUserApi;    @Resource    private DeptApi deptApi;    @Override    public ProcessInstance getProcessInstance(String id) {        return runtimeService.createProcessInstanceQuery()                .includeProcessVariables()                .processInstanceId(id)                .singleResult();    }    @Override    public List<ProcessInstance> getProcessInstances(Set<String> ids) {        return runtimeService.createProcessInstanceQuery().processInstanceIds(ids).list();    }    @Override    public HistoricProcessInstance getHistoricProcessInstance(String id) {        return historyService.createHistoricProcessInstanceQuery().processInstanceId(id).includeProcessVariables().singleResult();    }    @Override    public List<HistoricProcessInstance> getHistoricProcessInstances(Set<String> ids) {        return historyService.createHistoricProcessInstanceQuery().processInstanceIds(ids).list();    }    @Override    public PageResult<HistoricProcessInstance> getMyProcessInstancePage(Long userId,                                                                        BpmProcessInstanceMyPageReqVO pageReqVO) {        // 通过 BpmProcessInstanceExtDO 表，先查询到对应的分页        HistoricProcessInstanceQuery processInstanceQuery = historyService.createHistoricProcessInstanceQuery().includeProcessVariables()                .startedBy(String.valueOf(userId))                .orderByProcessInstanceStartTime().desc();        if (StrUtil.isNotEmpty(pageReqVO.getName())) {            processInstanceQuery.processInstanceNameLike("%" + pageReqVO.getName() + "%");        }        if (StrUtil.isNotEmpty(pageReqVO.getProcessDefinitionId())) {            processInstanceQuery.processDefinitionId("%" + pageReqVO.getProcessDefinitionId() + "%");        }        if (StrUtil.isNotEmpty(pageReqVO.getCategory())) {            processInstanceQuery.processDefinitionCategory(pageReqVO.getCategory());        }        if (pageReqVO.getStatus() != null) {            processInstanceQuery.variableValueEquals(BpmConstants.PROCESS_INSTANCE_VARIABLE_STATUS, pageReqVO.getStatus());        }        if (ArrayUtil.isNotEmpty(pageReqVO.getCreateTime())) {            processInstanceQuery.startedAfter(DateUtils.of(pageReqVO.getCreateTime()[0]));            processInstanceQuery.startedBefore(DateUtils.of(pageReqVO.getCreateTime()[1]));        }        // 查询数量        long processInstanceCount = processInstanceQuery.count();        if (processInstanceCount == 0) {            return PageResult.empty(processInstanceCount);        }        // 查询列表        List<HistoricProcessInstance> processInstanceList = processInstanceQuery.listPage(PageUtils.getStart(pageReqVO), pageReqVO.getPageSize());        return new PageResult<>(processInstanceList, processInstanceCount);    }    @Override    @Transactional(rollbackFor = Exception.class)    public String createProcessInstance(Long userId, @Valid BpmProcessInstanceCreateReqVO createReqVO) {        // 获得流程定义        ProcessDefinition definition = processDefinitionService.getProcessDefinition(createReqVO.getProcessDefinitionId());        // 发起流程        return createProcessInstance0(userId, definition, createReqVO.getVariables(), null, createReqVO.getAssignee());    }    @Override    public String createProcessInstance(Long userId, @Valid BpmProcessInstanceCreateReqDTO createReqDTO) {        // 获得流程定义        ProcessDefinition definition = processDefinitionService.getActiveProcessDefinition(createReqDTO.getProcessDefinitionKey());        // 发起流程        return createProcessInstance0(userId, definition, createReqDTO.getVariables(), createReqDTO.getBusinessKey(), createReqDTO.getAssignee());    }    private String createProcessInstance0(Long userId, ProcessDefinition definition,                                          Map<String, Object> variables, String businessKey,                                          Map<String, List<Long>> assignee) {        // 校验流程定义        if (definition == null) {            throw exception(PROCESS_DEFINITION_NOT_EXISTS);        }        if (definition.isSuspended()) {            throw exception(PROCESS_DEFINITION_IS_SUSPENDED);        }        // 设置上下文信息        // TODO @hai：要不往 variables 存到一个全局固定 key 里，减少对上下文的依赖        FlowableContextHolder.setAssignee(assignee);        // 创建流程实例        FlowableUtils.filterProcessInstanceFormVariable(variables); // 过滤一下，避免 ProcessInstance 系统级的变量被占用        variables.put(BpmConstants.PROCESS_INSTANCE_VARIABLE_STATUS, // 流程实例状态：审批中                BpmProcessInstanceStatusEnum.RUNNING.getStatus());        ProcessInstance instance = runtimeService.createProcessInstanceBuilder()                .processDefinitionId(definition.getId())                .businessKey(businessKey)                .name(definition.getName().trim())                .variables(variables)                .start();        return instance.getId();    }    @Override    public BpmProcessInstanceRespVO getProcessInstanceVO(String id) {        // 获得流程实例        HistoricProcessInstance processInstance = getHistoricProcessInstance(id);        if (processInstance == null) {            return null;        }        // 获得流程定义        ProcessDefinition processDefinition = processDefinitionService                .getProcessDefinition(processInstance.getProcessDefinitionId());        Assert.notNull(processDefinition, "流程定义({}) 不存在", processInstance.getProcessDefinitionId());        BpmProcessDefinitionInfoDO processDefinitionExt = processDefinitionService.getProcessDefinitionInfo(                processInstance.getProcessDefinitionId());        Assert.notNull(processDefinitionExt, "流程定义拓展({}) 不存在", id);        String bpmnXml = processDefinitionService.getProcessDefinitionBpmnXML(processInstance.getProcessDefinitionId());        // 获得 User        AdminUserRespDTO startUser = adminUserApi.getUser(NumberUtils.parseLong(processInstance.getStartUserId()));        DeptRespDTO dept = null;        if (startUser != null) {            dept = deptApi.getDept(startUser.getDeptId());        }        // 拼接结果        return BpmProcessInstanceConvert.INSTANCE.convert2(processInstance,                processDefinition, processDefinitionExt, bpmnXml, startUser, dept);    }    @Override    public void cancelProcessInstance(Long userId, @Valid BpmProcessInstanceCancelReqVO cancelReqVO) {        // 1.1 校验流程实例存在        ProcessInstance instance = getProcessInstance(cancelReqVO.getId());        if (instance == null) {            throw exception(PROCESS_INSTANCE_CANCEL_FAIL_NOT_EXISTS);        }        // 1.2 只能取消自己的        if (!Objects.equals(instance.getStartUserId(), String.valueOf(userId))) {            throw exception(PROCESS_INSTANCE_CANCEL_FAIL_NOT_SELF);        }        // 2. 通过删除流程实例，实现流程实例的取消,        // 删除流程实例，正则执行任务 ACT_RU_TASK. 任务会被删除。通过历史表查询        deleteProcessInstance(cancelReqVO.getId(),                BpmDeleteReasonEnum.CANCEL_PROCESS_INSTANCE.format(cancelReqVO.getReason()));        // 3. 进一步的处理，交给 updateProcessInstanceCancel 方法    }    @Override    public void updateProcessInstanceWhenCancel(FlowableCancelledEvent event) {        // 1. 判断是否为 Reject 不通过。如果是，则不进行更新.        // 因为，updateProcessInstanceReject 方法（审批不通过），已经进行更新了        if (BpmDeleteReasonEnum.isRejectReason((String) event.getCause())) {            return;        }        // 2. 更新流程实例 status        runtimeService.setVariable(event.getProcessInstanceId(), BpmConstants.PROCESS_INSTANCE_VARIABLE_STATUS,                BpmProcessInstanceStatusEnum.CANCEL.getStatus());        // 3. 发送流程实例的状态事件        // 注意：此时如果去查询 ProcessInstance 的话，字段是不全的，所以去查询了 HistoricProcessInstance        HistoricProcessInstance processInstance = getHistoricProcessInstance(event.getProcessInstanceId());        // 发送流程实例的状态事件        processInstanceResultEventPublisher.sendProcessInstanceResultEvent(                BpmProcessInstanceConvert.INSTANCE.convert(this, processInstance,                        BpmProcessInstanceStatusEnum.CANCEL.getStatus()));    }    @Override    public void updateProcessInstanceWhenApprove(ProcessInstance instance) {        // 1. 更新流程实例 status        runtimeService.setVariable(instance.getId(), BpmConstants.PROCESS_INSTANCE_VARIABLE_STATUS,                BpmProcessInstanceStatusEnum.APPROVE.getStatus());        // 2. 发送流程被【通过】的消息        messageService.sendMessageWhenProcessInstanceApprove(BpmProcessInstanceConvert.INSTANCE.convert2ApprovedReq(instance));        // 3. 发送流程实例的状态事件        // 注意：此时如果去查询 ProcessInstance 的话，字段是不全的，所以去查询了 HistoricProcessInstance        HistoricProcessInstance processInstance = getHistoricProcessInstance(instance.getId());        processInstanceResultEventPublisher.sendProcessInstanceResultEvent(                BpmProcessInstanceConvert.INSTANCE.convert(this, processInstance, BpmProcessInstanceStatusEnum.APPROVE.getStatus()));    }    @Override    @Transactional(rollbackFor = Exception.class)    public void updateProcessInstanceReject(String id, String reason) {        // 1. 更新流程实例 status        runtimeService.setVariable(id, BpmConstants.PROCESS_INSTANCE_VARIABLE_STATUS, BpmProcessInstanceStatusEnum.REJECT.getStatus());        // 2. 删除流程实例，以实现驳回任务时，取消整个审批流程        ProcessInstance processInstance = getProcessInstance(id);        deleteProcessInstance(id, StrUtil.format(BpmDeleteReasonEnum.REJECT_TASK.format(reason)));        // 3. 发送流程被【不通过】的消息        messageService.sendMessageWhenProcessInstanceReject(BpmProcessInstanceConvert.INSTANCE.convert2RejectReq(processInstance, reason));        // 4. 发送流程实例的状态事件        processInstanceResultEventPublisher.sendProcessInstanceResultEvent(                BpmProcessInstanceConvert.INSTANCE.convert(this, processInstance,                        BpmProcessInstanceStatusEnum.REJECT.getStatus()));    }    private void deleteProcessInstance(String id, String reason) {        runtimeService.deleteProcessInstance(id, reason);    }    @Override    public List<Long> getAssigneeByProcessInstanceIdAndTaskDefinitionKey(String processInstanceId, String taskDefinitionKey) {        // 1. 先从上下文获取，首次提交数据库中查询不到        List<Long> result = FlowableContextHolder.getAssigneeByTaskDefinitionKey(taskDefinitionKey);        if (CollUtil.isNotEmpty(result)) {            return result;        }        // 2. 从数据库中获取        // TODO @芋艿：指定审批人，这里的存储方案有问题，后续优化下//        BpmProcessInstanceExtDO instance = processInstanceExtMapper.selectByProcessInstanceId(processInstanceId);//        if (instance == null) {//            throw exception(PROCESS_INSTANCE_CANCEL_FAIL_NOT_EXISTS);//        }//        if (CollUtil.isNotEmpty(instance.getAssignee())) {//            return instance.getAssignee().get(taskDefinitionKey);//        }        return Collections.emptyList();    }}