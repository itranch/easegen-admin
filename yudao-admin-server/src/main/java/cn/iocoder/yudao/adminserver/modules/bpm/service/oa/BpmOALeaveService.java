package cn.iocoder.yudao.adminserver.modules.bpm.service.oa;


import cn.iocoder.yudao.adminserver.modules.bpm.controller.oa.vo.*;
import cn.iocoder.yudao.adminserver.modules.bpm.dal.dataobject.leave.OALeaveDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * 请假申请 Service 接口
 *
 * @author jason
 * @author 芋道源码
 */
public interface BpmOALeaveService {

    /**
     * 创建请假申请
     *
     * @param userId 用户编号
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createLeave(Long userId, @Valid OALeaveCreateReqVO createReqVO);

    /**
     * 删除请假申请
     *
     * @param id 编号
     */
    void cancelLeave(Long id);

    /**
     * 获得请假申请
     *
     * @param id 编号
     * @return 请假申请
     */
    OALeaveDO getLeave(Long id);

    /**
     * 获得请假申请分页
     *
     * @param userId 用户编号
     * @param pageReqVO 分页查询
     * @return 请假申请分页
     */
    PageResult<OALeaveDO> getLeavePage(Long userId, OALeavePageReqVO pageReqVO);

}
