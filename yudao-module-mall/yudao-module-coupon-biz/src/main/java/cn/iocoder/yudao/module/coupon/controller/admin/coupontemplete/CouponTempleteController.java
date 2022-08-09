package cn.iocoder.yudao.module.coupon.controller.admin.coupontemplete;

import cn.iocoder.yudao.module.coupon.controller.admin.coupontemplete.vo.*;
import cn.iocoder.yudao.module.coupon.convert.CouponTemplete.CouponTempleteConvert;
import cn.iocoder.yudao.module.coupon.dal.dataobject.CouponTemplete.CouponTempleteDO;
import cn.iocoder.yudao.module.coupon.service.CouponTemplete.CouponTempleteService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.*;

import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;



@Api(tags = "管理后台 - 优惠券模板")
@RestController
@RequestMapping("/CouponTemplete/")
@Validated
public class CouponTempleteController {

    @Resource
    private CouponTempleteService Service;

    @PostMapping("/create")
    @ApiOperation("创建优惠券模板")
    @PreAuthorize("@ss.hasPermission('CouponTemplete::create')")
    public CommonResult<Long> create(@Valid @RequestBody CouponTempleteCreateReqVO createReqVO) {
        return success(Service.create(createReqVO));
    }

    @PutMapping("/update")
    @ApiOperation("更新优惠券模板")
    @PreAuthorize("@ss.hasPermission('CouponTemplete::update')")
    public CommonResult<Boolean> update(@Valid @RequestBody CouponTempleteUpdateReqVO updateReqVO) {
        Service.update(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除优惠券模板")
    @ApiImplicitParam(name = "id", value = "编号", required = true, dataTypeClass = Long.class)
    @PreAuthorize("@ss.hasPermission('CouponTemplete::delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        Service.delete(id);
        return success(true);
    }

    @GetMapping("/get")
    @ApiOperation("获得优惠券模板")
    @ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = Long.class)
    @PreAuthorize("@ss.hasPermission('CouponTemplete::query')")
    public CommonResult<CouponTempleteRespVO> get(@RequestParam("id") Long id) {
        CouponTempleteDO couponTempleteDO = Service.get(id);
        return success(CouponTempleteConvert.INSTANCE.convert(couponTempleteDO));
    }

    @GetMapping("/list")
    @ApiOperation("获得优惠券模板列表")
    @ApiImplicitParam(name = "ids", value = "编号列表", required = true, example = "1024,2048", dataTypeClass = List.class)
    @PreAuthorize("@ss.hasPermission('CouponTemplete::query')")
    public CommonResult<List<CouponTempleteRespVO>> getList(@RequestParam("ids") Collection<Long> ids) {
        List<CouponTempleteDO> list = Service.getList(ids);
        return success(CouponTempleteConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @ApiOperation("获得优惠券模板分页")
    @PreAuthorize("@ss.hasPermission('CouponTemplete::query')")
    public CommonResult<PageResult<CouponTempleteRespVO>> getPage(@Valid CouponTempletePageReqVO pageVO) {
        PageResult<CouponTempleteDO> pageResult = Service.getPage(pageVO);
        return success(CouponTempleteConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @ApiOperation("导出优惠券模板 Excel")
    @PreAuthorize("@ss.hasPermission('CouponTemplete::export')")
    @OperateLog(type = EXPORT)
    public void exportExcel(@Valid CouponTempleteExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<CouponTempleteDO> list = Service.getList(exportReqVO);
        // 导出 Excel
        List<CouponTempleteExcelVO> datas = CouponTempleteConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "优惠券模板.xls", "数据", CouponTempleteExcelVO.class, datas);
    }

}
