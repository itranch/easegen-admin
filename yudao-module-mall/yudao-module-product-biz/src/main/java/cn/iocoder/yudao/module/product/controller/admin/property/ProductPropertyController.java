package cn.iocoder.yudao.module.product.controller.admin.property;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.product.controller.admin.property.vo.property.*;
import cn.iocoder.yudao.module.product.convert.property.ProductPropertyConvert;
import cn.iocoder.yudao.module.product.dal.dataobject.property.ProductPropertyDO;
import cn.iocoder.yudao.module.product.dal.dataobject.property.ProductPropertyValueDO;
import cn.iocoder.yudao.module.product.service.property.ProductPropertyService;
import cn.iocoder.yudao.module.product.service.property.ProductPropertyValueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Api(tags = "管理后台 - 商品属性项")
@RestController
@RequestMapping("/product/property")
@Validated
public class ProductPropertyController {

    @Resource
    private ProductPropertyService productPropertyService;
    @Resource
    private ProductPropertyValueService productPropertyValueService;

    @PostMapping("/create")
    @ApiOperation("创建属性项")
    @PreAuthorize("@ss.hasPermission('product:property:create')")
    public CommonResult<Long> createProperty(@Valid @RequestBody ProductPropertyCreateReqVO createReqVO) {
        return success(productPropertyService.createProperty(createReqVO));
    }

    @PutMapping("/update")
    @ApiOperation("更新属性项")
    @PreAuthorize("@ss.hasPermission('product:property:update')")
    public CommonResult<Boolean> updateProperty(@Valid @RequestBody ProductPropertyUpdateReqVO updateReqVO) {
        productPropertyService.updateProperty(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除属性项")
    @ApiImplicitParam(name = "id", value = "编号", required = true, dataTypeClass = Long.class)
    @PreAuthorize("@ss.hasPermission('product:property:delete')")
    public CommonResult<Boolean> deleteProperty(@RequestParam("id") Long id) {
        productPropertyService.deleteProperty(id);
        return success(true);
    }

    @GetMapping("/get")
    @ApiOperation("获得属性项")
    @ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = Long.class)
    @PreAuthorize("@ss.hasPermission('product:property:query')")
    public CommonResult<ProductPropertyRespVO> getProperty(@RequestParam("id") Long id) {
        return success(ProductPropertyConvert.INSTANCE.convert(productPropertyService.getProperty(id)));
    }

    @GetMapping("/list")
    @ApiOperation("获得属性项列表")
    @PreAuthorize("@ss.hasPermission('product:property:query')")
    public CommonResult<List<ProductPropertyRespVO>> getPropertyList(@Valid ProductPropertyListReqVO listReqVO) {
        return success(ProductPropertyConvert.INSTANCE.convertList(productPropertyService.getPropertyList(listReqVO)));
    }

    @GetMapping("/page")
    @ApiOperation("获得属性项分页")
    @PreAuthorize("@ss.hasPermission('product:property:query')")
    public CommonResult<PageResult<ProductPropertyRespVO>> getPropertyPage(@Valid ProductPropertyPageReqVO pageVO) {
        return success(ProductPropertyConvert.INSTANCE.convertPage(productPropertyService.getPropertyPage(pageVO)));
    }

    @GetMapping("/get-value-list")
    @ApiOperation("获得属性项列表")
    @PreAuthorize("@ss.hasPermission('product:property:query')")
    public CommonResult<List<ProductPropertyAndValueRespVO>> getPropertyAndValueList(@Valid ProductPropertyListReqVO listReqVO) {
        // 查询属性项
        List<ProductPropertyDO> keys = productPropertyService.getPropertyList(listReqVO);
        if (CollUtil.isEmpty(keys)) {
            return success(Collections.emptyList());
        }
        // 查询属性值
        List<ProductPropertyValueDO> values = productPropertyValueService.getPropertyValueListByPropertyId(
                convertSet(keys, ProductPropertyDO::getId));
        return success(ProductPropertyConvert.INSTANCE.convertList(keys, values));
    }

}
