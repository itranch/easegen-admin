package cn.iocoder.yudao.module.bpm.framework.flowable.core.enums;

/**
 * BPMN XML 常量信息
 *
 * @author 芋道源码
 */
public interface BpmnModelConstants {

    String BPMN_FILE_SUFFIX = ".bpmn";

    /**
     * BPMN 中的命名空间
     */
    String NAMESPACE = "http://flowable.org/bpmn";

    /**
     * BPMN UserTask 的扩展属性，用于标记候选人策略
     */
    String USER_TASK_CANDIDATE_STRATEGY = "candidateStrategy";
    /**
     * BPMN UserTask 的扩展属性，用于标记候选人参数
     */
    String USER_TASK_CANDIDATE_PARAM = "candidateParam";

    /**
     * BPMN ExtensionElement 的扩展属性，用于标记边界事件类型
     */
    String BOUNDARY_EVENT_TYPE = "boundaryEventType";

    // TODO @jason：这个命名，应该也要改哈
    /**
     * BPMN ExtensionElement 的扩展属性，用于标记用户任务超时执行动作
     */
    String USER_TASK_TIMEOUT_HANDLER_ACTION = "timeoutAction";

    // TODO @jason：1）是不是上面的 timeoutAction 改成 timeoutHandler；2）rejectHandlerType 改成 rejectHandler 哇？
    /**
     * BPMN ExtensionElement 的扩展属性，用于标记用户任务拒绝处理类型
     */
    String USER_TASK_REJECT_HANDLER_TYPE = "rejectHandlerType";
    /**
     * BPMN ExtensionElement 的扩展属性，用于标记用户任务拒绝后的回退的任务 Id
     */
    String USER_TASK_REJECT_RETURN_TASK_ID = "rejectReturnTaskId";

    /**
     * BPMN UserTask 的扩展属性，用于标记用户任务的审批方式
     */
    String USER_TASK_APPROVE_METHOD = "approveMethod";

    // TODO @jason：这个命名，可能有个 fieldsPermissions 更合适点。可能 formPermissions 会更更合适。
    /**
     * BPMN ExtensionElement 流程表单字段权限元素, 用于标记字段权限
     */
    String FORM_FIELD_PERMISSION_ELEMENT = "fieldsPermission";

    /**
     * BPMN ExtensionElement Attribute, 用于标记表单字段
     */
    String FORM_FIELD_PERMISSION_ELEMENT_FIELD_ATTRIBUTE = "field";
    /**
     * BPMN ExtensionElement Attribute, 用于标记表单权限
     */
    String FORM_FIELD_PERMISSION_ELEMENT_PERMISSION_ATTRIBUTE = "permission";

    /**
     * BPMN ExtensionElement 操作按钮设置元素, 用于审批节点操作按钮设置
     */
    String BUTTON_SETTING_ELEMENT = "buttonsSettings";
    /**
     * BPMN ExtensionElement Attribute, 用于标记按钮编号
     */
    String BUTTON_SETTING_ELEMENT_ID_ATTRIBUTE = "id";
    /**
     * BPMN ExtensionElement Attribute, 用于标记按钮显示名称
     */
    String BUTTON_SETTING_ELEMENT_DISPLAY_NAME_ATTRIBUTE = "displayName";

    /**
     * BPMN ExtensionElement Attribute, 用于标记按钮是否启用
     */
    String BUTTON_SETTING_ELEMENT_ENABLE_ATTRIBUTE = "enable";

}
