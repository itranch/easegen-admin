package cn.iocoder.yudao.module.trade.controller.admin.aftersale;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.trade.controller.admin.aftersale.vo.TradeAfterSaleDisagreeReqVO;
import cn.iocoder.yudao.module.trade.service.aftersale.TradeAfterSaleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Api(tags = "管理后台 - 交易售后")
@RestController
@RequestMapping("/trade/after-sale")
@Validated
@Slf4j
public class TradeAfterSaleController {

    @Resource
    private TradeAfterSaleService afterSaleService;

    @PutMapping("/agree")
    @ApiOperation("同意售后")
    @ApiImplicitParam(name = "id", value = "售后编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('trade:after-sale:agree')")
    public CommonResult<Boolean> agreeAfterSale(@RequestParam("id") Long id) {
        afterSaleService.agreeAfterSale(getLoginUserId(), id);
        return success(true);
    }

    @PutMapping("/disagree")
    @ApiOperation("拒绝售后")
    @PreAuthorize("@ss.hasPermission('trade:after-sale:disagree')")
    public CommonResult<Boolean> disagreeAfterSale(@RequestBody TradeAfterSaleDisagreeReqVO confirmReqVO) {
        afterSaleService.disagreeAfterSale(getLoginUserId(), confirmReqVO);
        return success(true);
    }

    @PutMapping("/receive")
    @ApiOperation("确认收货")
    @ApiImplicitParam(name = "id", value = "售后编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('trade:after-sale:receive')")
    public CommonResult<Boolean> receiveAfterSale(@RequestParam("id") Long id) {
        afterSaleService.receiveAfterSale(getLoginUserId(), id);
        return success(true);
    }

    @PostMapping("/refund")
    @ApiOperation(value = "确认退款")
    @ApiImplicitParam(name = "id", value = "售后编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('trade:after-sale:refund')")
    public CommonResult<Boolean> refundAfterSale(@RequestParam("id") Long id) {
        afterSaleService.refundAfterSale(getLoginUserId(), getClientIP(), id);
        return success(true);
    }

}
