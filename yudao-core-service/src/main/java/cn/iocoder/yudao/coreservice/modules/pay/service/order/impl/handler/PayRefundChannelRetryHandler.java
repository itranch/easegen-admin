package cn.iocoder.yudao.coreservice.modules.pay.service.order.impl.handler;

import cn.iocoder.yudao.coreservice.modules.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.coreservice.modules.pay.dal.dataobject.order.PayRefundDO;
import cn.iocoder.yudao.coreservice.modules.pay.dal.mysql.order.PayOrderCoreMapper;
import cn.iocoder.yudao.coreservice.modules.pay.dal.mysql.order.PayRefundCoreMapper;
import cn.iocoder.yudao.coreservice.modules.pay.enums.order.PayRefundStatusEnum;
import cn.iocoder.yudao.coreservice.modules.pay.service.order.PayRefundAbstractChannelPostHandler;
import cn.iocoder.yudao.coreservice.modules.pay.service.order.dto.PayRefundPostReqDTO;
import cn.iocoder.yudao.framework.pay.core.enums.PayChannelRespEnum;
import org.springframework.stereotype.Service;

/**
 * 支付退款订单渠道返回重试的后置处理类
 * {@link PayChannelRespEnum#RETRY_FAILURE}
 */
@Service
public class PayRefundChannelRetryHandler extends PayRefundAbstractChannelPostHandler {


    public PayRefundChannelRetryHandler(PayOrderCoreMapper payOrderCoreMapper,
                                        PayRefundCoreMapper payRefundMapper) {
        super(payOrderCoreMapper, payRefundMapper);
    }

    @Override
    public PayChannelRespEnum[] supportHandleResp() {
        return new PayChannelRespEnum[] {PayChannelRespEnum.RETRY_FAILURE};
    }

    @Override
    public void handleRefundChannelResp(PayRefundPostReqDTO req) {

        PayRefundDO updateRefundDO = new PayRefundDO();
        //更新退款单表
        updateRefundDO.setId(req.getRefundId())
                .setStatus(PayRefundStatusEnum.UNKNOWN_RETRY.getStatus())
                .setChannelErrorCode(req.getChannelErrCode())
                .setChannelErrorMsg(req.getChannelErrMsg());
        updatePayRefund(updateRefundDO);

        PayOrderDO updateOrderDO = new PayOrderDO();
        //更新订单表
        updateOrderDO.setId(req.getOrderId())
                .setRefundTimes(req.getRefundedTimes() + 1);
        updatePayOrder(updateOrderDO);

        //TODO 发起重试任务
    }
}
