package cn.iocoder.yudao.module.pay.convert.wallet;

import cn.iocoder.yudao.module.pay.controller.app.wallet.vo.recharge.AppPayWalletRechargeCreateReqVO;
import cn.iocoder.yudao.module.pay.controller.app.wallet.vo.recharge.AppPayWalletRechargeCreateRespVO;
import cn.iocoder.yudao.module.pay.dal.dataobject.wallet.PayWalletRechargeDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author jason
 */
@Mapper
public interface PayWalletRechargeConvert {

    PayWalletRechargeConvert INSTANCE = Mappers.getMapper(PayWalletRechargeConvert.class);

    @Mapping(target = "totalPrice", expression = "java(vo.getPayPrice() + vo.getBonusPrice() )")
    PayWalletRechargeDO convert(Long walletId, AppPayWalletRechargeCreateReqVO vo);

    AppPayWalletRechargeCreateRespVO convert(PayWalletRechargeDO bean);

}
