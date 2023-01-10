package cn.iocoder.yudao.module.mp.service.message;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mp.controller.admin.message.vo.MpMessagePageReqVO;
import cn.iocoder.yudao.module.mp.controller.admin.message.vo.MpMessageSendReqVO;
import cn.iocoder.yudao.module.mp.dal.dataobject.message.MpMessageDO;
import cn.iocoder.yudao.module.mp.service.message.bo.MpMessageSendOutReqBO;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

import javax.validation.Valid;

/**
 * 粉丝消息表 Service 接口
 *
 * @author 芋道源码
 */
public interface MpMessageService {

    // TODO 芋艿：方法名要优化下
    /**
     * 获得粉丝消息表分页
     *
     * @param pageReqVO 分页查询
     * @return 粉丝消息表 分页
     */
    PageResult<MpMessageDO> getWxFansMsgPage(MpMessagePageReqVO pageReqVO);

    /**
     * 从公众号，接收到用户消息
     *
     * @param appId 微信公众号 appId
     * @param wxMessage 消息
     */
    void receiveMessage(String appId, WxMpXmlMessage wxMessage);

    /**
     * 使用公众号，给用户回复消息
     *
     * 例如说：自动回复、客服消息、菜单回复消息等场景
     *
     * 注意，该方法只是返回 WxMpXmlOutMessage 对象，不会真的发送消息
     *
     * @param sendReqBO 消息内容
     * @return 微信回复消息 XML
     */
    WxMpXmlOutMessage sendOutMessage(@Valid MpMessageSendOutReqBO sendReqBO);

    /**
     * 使用公众号，给用户发送【客服】消息
     *
     * 注意，该方法会真实发送消息
     *
     * @param sendReqVO 消息内容
     * @return 消息
     */
    MpMessageDO sendKefuMessage(MpMessageSendReqVO sendReqVO);

}
