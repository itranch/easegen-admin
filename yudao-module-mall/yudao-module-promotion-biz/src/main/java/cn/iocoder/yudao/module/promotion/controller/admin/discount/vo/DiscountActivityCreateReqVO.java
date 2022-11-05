package cn.iocoder.yudao.module.promotion.controller.admin.discount.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel("管理后台 - 限时折扣活动创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DiscountActivityCreateReqVO extends DiscountActivityBaseVO {

    /**
     * 商品列表
     */
    @NotNull(message = "商品列表不能为空")
    private List<Product> products;

}
