package cn.iocoder.yudao.module.product.controller.app.spu.vo;

import cn.iocoder.yudao.module.product.dal.dataobject.sku.ProductSkuDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "用户 App - 商品 SPU 分页项 Response VO")
@Data
public class AppProductSpuPageItemRespVO {

    @Schema(description = "商品 SPU 编号", required = true, example = "1")
    private Long id;

    @Schema(description = "商品名称", required = true, example = "芋道")
    private String name;

    @Schema(description = "分类编号", required = true)
    private Long categoryId;

    @Schema(description = "商品封面图", required = true)
    private String picUrl;

    @Schema(description = "商品轮播图", required = true)
    private List<String> sliderPicUrls;

    @Schema(description = "商品价格，单位使用：分", required = true, example = "1024")
    private Integer price;

    // ========== SKU 相关字段 =========

    @Schema(description = "库存", required = true, example = "666")
    private Integer stock;

    // ========== 统计相关字段 =========

    @Schema(description = "商品销量", example = "1024")
    private Integer salesCount;

}
