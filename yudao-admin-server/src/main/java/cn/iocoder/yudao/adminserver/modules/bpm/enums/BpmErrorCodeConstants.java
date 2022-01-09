package cn.iocoder.yudao.adminserver.modules.bpm.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 工作流 错误码枚举类
 *
 * 工作流系统，使用 1-009-000-000 段
 */
public interface BpmErrorCodeConstants {

    // ==========  通用流程处理 模块 1-009-000-000 ==========
    ErrorCode HIGHLIGHT_IMG_ERROR = new ErrorCode(1009000002, "获取高亮流程图异常");

    // ========== OA 流程模块 1-009-001-000 ==========
    ErrorCode OA_LEAVE_NOT_EXISTS = new ErrorCode(1009001001, "请假申请不存在");
    ErrorCode OA_PM_POST_NOT_EXISTS = new ErrorCode(1009001002, "项目经理岗位未设置");
    ErrorCode OA_DEPART_PM_POST_NOT_EXISTS = new ErrorCode(1009001009, "部门的项目经理不存在");
    ErrorCode OA_BM_POST_NOT_EXISTS = new ErrorCode(1009001004, "部门经理岗位未设置");
    ErrorCode OA_DEPART_BM_POST_NOT_EXISTS = new ErrorCode(1009001005, "部门的部门经理不存在");
    ErrorCode OA_HR_POST_NOT_EXISTS = new ErrorCode(1009001006, "HR岗位未设置");
    ErrorCode OA_DAY_LEAVE_ERROR = new ErrorCode(1009001007, "请假天数必须>=1");

    // ========== 流程模型 1-009-002-000 ==========
    ErrorCode MODEL_KEY_EXISTS = new ErrorCode(1009002000, "已经存在流程标识为【{}】的流程");
    ErrorCode MODEL_NOT_EXISTS = new ErrorCode(1009002001, "流程模型不存在");
    ErrorCode MODEL_KEY_VALID = new ErrorCode(1009002002, "流程标识格式不正确，需要以字母或下划线开头，后接任意字母、数字、中划线、下划线、句点！");

    // ========== 流程定义 1-009-003-000 ==========
    ErrorCode PROCESS_DEFINITION_KEY_NOT_MATCH = new ErrorCode(1009003000, "流程定义的标识期望是({})，当前是({})，请修改 BPMN 流程图");
    ErrorCode PROCESS_DEFINITION_NAME_NOT_MATCH = new ErrorCode(1009003001, "流程定义的名字期望是({})，当前是({})，请修改 BPMN 流程图");
    ErrorCode PROCESS_DEFINITION_NOT_EXISTS = new ErrorCode(1009003002, "流程定义不存在");
    ErrorCode PROCESS_DEFINITION_IS_SUSPENDED = new ErrorCode(1009003002, "流程定义处于挂起状态");

    // ========== 流程实例 1-009-004-000 ==========
    ErrorCode PROCESS_INSTANCE_NOT_EXISTS = new ErrorCode(1009004000, "流程实例不存在");
    ErrorCode PROCESS_INSTANCE_CANCEL_FAIL_NOT_EXISTS = new ErrorCode(1009004001, "流程取消失败，流程不处于运行中");
    ErrorCode PROCESS_INSTANCE_CANCEL_FAIL_NOT_SELF = new ErrorCode(1009004002, "流程取消失败，该流程不是你发起的");

    // ========== 流程实例 1-009-005-000 ==========
    ErrorCode TASK_COMPLETE_FAIL_NOT_EXISTS = new ErrorCode(1009004000, "审批任务失败，原因：该任务不处于未审批");

    // ========== 动态表单模块 1-009-010-000 ==========
    ErrorCode FORM_NOT_EXISTS = new ErrorCode(1009010000, "动态表单不存在");
    ErrorCode FORM_FIELD_REPEAT = new ErrorCode(1009010000, "表单项({}) 和 ({}) 使用了相同的字段名({})");

}
