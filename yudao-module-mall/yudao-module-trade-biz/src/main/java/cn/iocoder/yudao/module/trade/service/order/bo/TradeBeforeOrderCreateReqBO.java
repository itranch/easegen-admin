package cn.iocoder.yudao.module.trade.service.order.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 订单创建之前 Request BO
 *
 * @author HUIHUI
 */
@Data
public class TradeBeforeOrderCreateReqBO {

    @NotNull(message = "订单类型不能为空")
    private Integer orderType;

    // ========== 秒杀活动相关字段 ==========

    @Schema(description = "秒杀活动编号", example = "1024")
    private Long seckillActivityId;

    // ========== 拼团活动相关字段 ==========

    @Schema(description = "拼团活动编号", example = "1024")
    private Long combinationActivityId;

    @Schema(description = "拼团团长编号", example = "2048")
    private Long combinationHeadId;

    @Schema(description = "砍价活动编号", example = "123")
    private Long bargainActivityId;

    @NotNull(message = "购买数量不能为空")
    private Integer count;

    @NotNull(message = "活动商品不能为空")
    private Item item;

    @Data
    @Valid
    public static class Item {

        @NotNull(message = "SPU 编号不能为空")
        private Long spuId;

        @NotNull(message = "SKU 编号活动商品不能为空")
        private Long skuId;

        @NotNull(message = "购买数量不能为空")
        private Integer count;

    }

}
