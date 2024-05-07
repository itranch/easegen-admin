package cn.iocoder.yudao.module.ai.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.model.AiChatModalAddReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.model.AiChatModalListReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.model.AiChatModalListRes;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.model.AiChatModalRes;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiChatModalDO;

/**
 * ai modal
 *
 * @author fansili
 * @time 2024/4/24 19:42
 * @since 1.0
 */
public interface AiChatModalService {

    /**
     * ai modal - 列表
     *
     * @param req
     * @return
     */
    PageResult<AiChatModalListRes> list(AiChatModalListReqVO req);

    /**
     * ai modal - 添加
     *
     * @param req
     */
    void add(AiChatModalAddReqVO req);

    /**
     * ai modal - 更新
     *
     * @param id
     * @param req
     */
    void update(Long id, AiChatModalAddReqVO req);

    /**
     * ai modal - 删除
     *
     * @param id
     */
    void delete(Long id);

    /**
     * 获取 - 获取 modal
     *
     * @param modalId
     * @return
     */
    AiChatModalRes getChatModalOfValidate(Long modalId);

    /**
     * 校验 - 是否存在
     *
     * @param id
     * @return
     */
    AiChatModalDO validateExists(Long id);

    /**
     * 校验 - 校验是否可用
     *
     * @param chatModal
     */
    void validateAvailable(AiChatModalRes chatModal);
}
