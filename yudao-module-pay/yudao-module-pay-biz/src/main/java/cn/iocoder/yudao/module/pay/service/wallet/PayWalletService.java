package cn.iocoder.yudao.module.pay.service.wallet;

import cn.iocoder.yudao.module.pay.dal.dataobject.wallet.PayWalletDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.wallet.PayWalletTransactionDO;

/**
 * 钱包 Service 接口
 *
 * @author jason
 */
public interface PayWalletService {

    /**
     * 获取钱包信息
     * @param userId 用户 id
     * @param userType 用户类型
     */
    PayWalletDO getPayWallet(Long userId, Integer userType);

    /**
     * 钱包支付
     * @param outTradeNo 外部订单号
     * @param price 金额
     */
    PayWalletTransactionDO pay(String outTradeNo, Integer price);

    /**
     * 钱包支付退款
     * @param outRefundNo 外部退款号
     * @param refundPrice 退款金额
     * @param reason  退款原因
     */
    PayWalletTransactionDO refund(String outRefundNo, Integer refundPrice, String reason);
}
