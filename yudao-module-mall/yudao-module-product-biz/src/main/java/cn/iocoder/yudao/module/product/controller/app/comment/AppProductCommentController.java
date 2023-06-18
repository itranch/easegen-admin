package cn.iocoder.yudao.module.product.controller.app.comment;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.product.controller.app.comment.vo.AppCommentPageReqVO;
import cn.iocoder.yudao.module.product.controller.app.comment.vo.AppCommentStatisticsRespVO;
import cn.iocoder.yudao.module.product.controller.app.comment.vo.AppProductCommentRespVO;
import cn.iocoder.yudao.module.product.controller.app.property.vo.value.AppProductPropertyValueDetailRespVO;
import cn.iocoder.yudao.module.product.service.comment.ProductCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 商品评价")
@RestController
@RequestMapping("/product/comment")
@Validated
public class AppProductCommentController {

    @Resource
    private ProductCommentService productCommentService;

    // TODO @puhui999：可以实现下
    @GetMapping("/list")
    @Operation(summary = "获得最近的 n 条商品评价")
    @Parameters({
            @Parameter(name = "spuId", description = "商品 SPU 编号", required = true, example = "1024"),
            @Parameter(name = "count", description = "数量", required = true, example = "10")
    })
    public CommonResult<List<AppProductCommentRespVO>> getCommentList(@RequestParam("spuId") Long spuId,
                                                                      @RequestParam(value = "count", defaultValue = "10") Integer count) {
        List<AppProductPropertyValueDetailRespVO> list = new ArrayList<>();

        AppProductPropertyValueDetailRespVO item1 = new AppProductPropertyValueDetailRespVO();
        item1.setPropertyId(1L);
        item1.setPropertyName("颜色");
        item1.setValueId(1024L);
        item1.setValueName("红色");
        list.add(item1);

        AppProductPropertyValueDetailRespVO item2 = new AppProductPropertyValueDetailRespVO();
        item2.setPropertyId(2L);
        item2.setPropertyName("尺寸");
        item2.setValueId(2048L);
        item2.setValueName("大号");
        list.add(item2);

        AppProductPropertyValueDetailRespVO item3 = new AppProductPropertyValueDetailRespVO();
        item3.setPropertyId(3L);
        item3.setPropertyName("重量");
        item3.setValueId(3072L);
        item3.setValueName("500克");
        list.add(item3);

        // TODO 生成 mock 的数据
        AppProductCommentRespVO appCommentRespVO = new AppProductCommentRespVO();
        appCommentRespVO.setUserId((long) (new Random().nextInt(100000) + 10000));
        appCommentRespVO.setUserNickname("用户" + new Random().nextInt(100));
        appCommentRespVO.setUserAvatar("https://demo26.crmeb.net/uploads/attach/2021/11/15/a79f5d2ea6bf0c3c11b2127332dfe2df.jpg");
        appCommentRespVO.setId((long) (new Random().nextInt(100000) + 10000));
        appCommentRespVO.setAnonymous(new Random().nextBoolean());
        appCommentRespVO.setOrderId((long) (new Random().nextInt(100000) + 10000));
        appCommentRespVO.setOrderItemId((long) (new Random().nextInt(100000) + 10000));
        appCommentRespVO.setReplyStatus(new Random().nextBoolean());
        appCommentRespVO.setReplyUserId((long) (new Random().nextInt(100000) + 10000));
        appCommentRespVO.setReplyContent("回复内容" + new Random().nextInt(100));
        appCommentRespVO.setReplyTime(LocalDateTime.now().minusDays(new Random().nextInt(30)));
        appCommentRespVO.setCreateTime(LocalDateTime.now().minusDays(new Random().nextInt(30)));
        appCommentRespVO.setSpuId((long) (new Random().nextInt(100000) + 10000));
        appCommentRespVO.setSpuName("商品" + new Random().nextInt(100));
        appCommentRespVO.setSkuId((long) (new Random().nextInt(100000) + 10000));
        appCommentRespVO.setSkuProperties(list);
        appCommentRespVO.setScores(new Random().nextInt(5) + 1);
        appCommentRespVO.setDescriptionScores(new Random().nextInt(5) + 1);
        appCommentRespVO.setBenefitScores(new Random().nextInt(5) + 1);
        appCommentRespVO.setContent("评论内容" + new Random().nextInt(100));
        appCommentRespVO.setPicUrls(Arrays.asList("https://demo26.crmeb.net/uploads/attach/2021/11/15/a79f5d2ea6bf0c3c11b2127332dfe2df.jpg"));

        return success(Arrays.asList(appCommentRespVO));
    }

    @GetMapping("/page")
    @Operation(summary = "获得商品评价分页")
    public CommonResult<PageResult<AppProductCommentRespVO>> getCommentPage(@Valid AppCommentPageReqVO pageVO) {
        return success(productCommentService.getCommentPage(pageVO, Boolean.TRUE));
    }

    // TODO @puhui：get-statistics；方法改成 getCommentStatistics；getCommentPageTabsCount 也改掉哈
    @GetMapping("/getCommentStatistics")
    @Operation(summary = "获得商品的评价统计")
    public CommonResult<AppCommentStatisticsRespVO> getCommentPage(@Valid @RequestParam("spuId") Long spuId) {
        return success(productCommentService.getCommentPageTabsCount(spuId, Boolean.TRUE));
    }

}
