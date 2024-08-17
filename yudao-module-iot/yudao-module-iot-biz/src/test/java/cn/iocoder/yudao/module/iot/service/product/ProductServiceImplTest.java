package cn.iocoder.yudao.module.iot.service.product;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import jakarta.annotation.Resource;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;

import cn.iocoder.yudao.module.iot.controller.admin.product.vo.*;
import cn.iocoder.yudao.module.iot.dal.dataobject.product.ProductDO;
import cn.iocoder.yudao.module.iot.dal.mysql.product.ProductMapper;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Import;
import java.util.*;
import java.time.LocalDateTime;

import static cn.hutool.core.util.RandomUtil.*;
import static cn.iocoder.yudao.module.iot.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.*;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.*;
import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.*;
import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.*;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link ProductServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(ProductServiceImpl.class)
public class ProductServiceImplTest extends BaseDbUnitTest {

    @Resource
    private ProductServiceImpl productService;

    @Resource
    private ProductMapper productMapper;

    @Test
    public void testCreateProduct_success() {
        // 准备参数
        ProductSaveReqVO createReqVO = randomPojo(ProductSaveReqVO.class).setId(null);

        // 调用
        Long productId = productService.createProduct(createReqVO);
        // 断言
        assertNotNull(productId);
        // 校验记录的属性是否正确
        ProductDO product = productMapper.selectById(productId);
        assertPojoEquals(createReqVO, product, "id");
    }

    @Test
    public void testUpdateProduct_success() {
        // mock 数据
        ProductDO dbProduct = randomPojo(ProductDO.class);
        productMapper.insert(dbProduct);// @Sql: 先插入出一条存在的数据
        // 准备参数
        ProductSaveReqVO updateReqVO = randomPojo(ProductSaveReqVO.class, o -> {
            o.setId(dbProduct.getId()); // 设置更新的 ID
        });

        // 调用
        productService.updateProduct(updateReqVO);
        // 校验是否更新正确
        ProductDO product = productMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, product);
    }

    @Test
    public void testUpdateProduct_notExists() {
        // 准备参数
        ProductSaveReqVO updateReqVO = randomPojo(ProductSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> productService.updateProduct(updateReqVO), PRODUCT_NOT_EXISTS);
    }

    @Test
    public void testDeleteProduct_success() {
        // mock 数据
        ProductDO dbProduct = randomPojo(ProductDO.class);
        productMapper.insert(dbProduct);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbProduct.getId();

        // 调用
        productService.deleteProduct(id);
       // 校验数据不存在了
       assertNull(productMapper.selectById(id));
    }

    @Test
    public void testDeleteProduct_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> productService.deleteProduct(id), PRODUCT_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetProductPage() {
       // mock 数据
       ProductDO dbProduct = randomPojo(ProductDO.class, o -> { // 等会查询到
           o.setName(null);
           o.setIdentification(null);
           o.setDeviceType(null);
           o.setManufacturerName(null);
           o.setModel(null);
           o.setDataFormat(null);
           o.setProtocolType(null);
           o.setDescription(null);
           o.setStatus(null);
           o.setMetadata(null);
           o.setMessageProtocol(null);
           o.setProtocolName(null);
           o.setCreateTime(null);
       });
       productMapper.insert(dbProduct);
       // 测试 name 不匹配
       productMapper.insert(cloneIgnoreId(dbProduct, o -> o.setName(null)));
       // 测试 identification 不匹配
       productMapper.insert(cloneIgnoreId(dbProduct, o -> o.setIdentification(null)));
       // 测试 deviceType 不匹配
       productMapper.insert(cloneIgnoreId(dbProduct, o -> o.setDeviceType(null)));
       // 测试 manufacturerName 不匹配
       productMapper.insert(cloneIgnoreId(dbProduct, o -> o.setManufacturerName(null)));
       // 测试 model 不匹配
       productMapper.insert(cloneIgnoreId(dbProduct, o -> o.setModel(null)));
       // 测试 dataFormat 不匹配
       productMapper.insert(cloneIgnoreId(dbProduct, o -> o.setDataFormat(null)));
       // 测试 protocolType 不匹配
       productMapper.insert(cloneIgnoreId(dbProduct, o -> o.setProtocolType(null)));
       // 测试 description 不匹配
       productMapper.insert(cloneIgnoreId(dbProduct, o -> o.setDescription(null)));
       // 测试 status 不匹配
       productMapper.insert(cloneIgnoreId(dbProduct, o -> o.setStatus(null)));
       // 测试 metadata 不匹配
       productMapper.insert(cloneIgnoreId(dbProduct, o -> o.setMetadata(null)));
       // 测试 messageProtocol 不匹配
       productMapper.insert(cloneIgnoreId(dbProduct, o -> o.setMessageProtocol(null)));
       // 测试 protocolName 不匹配
       productMapper.insert(cloneIgnoreId(dbProduct, o -> o.setProtocolName(null)));
       // 测试 createTime 不匹配
       productMapper.insert(cloneIgnoreId(dbProduct, o -> o.setCreateTime(null)));
       // 准备参数
       ProductPageReqVO reqVO = new ProductPageReqVO();
       reqVO.setName(null);
       reqVO.setIdentification(null);
       reqVO.setDeviceType(null);
       reqVO.setManufacturerName(null);
       reqVO.setModel(null);
       reqVO.setDataFormat(null);
       reqVO.setProtocolType(null);
       reqVO.setDescription(null);
       reqVO.setStatus(null);
       reqVO.setMetadata(null);
       reqVO.setMessageProtocol(null);
       reqVO.setProtocolName(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       PageResult<ProductDO> pageResult = productService.getProductPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbProduct, pageResult.getList().get(0));
    }

}