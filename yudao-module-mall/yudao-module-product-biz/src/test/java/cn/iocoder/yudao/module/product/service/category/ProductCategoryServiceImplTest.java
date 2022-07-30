package cn.iocoder.yudao.module.product.service.category;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.product.controller.admin.category.vo.ProductCategoryCreateReqVO;
import cn.iocoder.yudao.module.product.controller.admin.category.vo.ProductCategoryListReqVO;
import cn.iocoder.yudao.module.product.controller.admin.category.vo.ProductCategoryUpdateReqVO;
import cn.iocoder.yudao.module.product.dal.dataobject.category.ProductCategoryDO;
import cn.iocoder.yudao.module.product.dal.mysql.category.ProductCategoryMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomLongId;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static cn.iocoder.yudao.module.product.enums.ErrorCodeConstants.PRODUCT_CATEGORY_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ProductCategoryServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(ProductCategoryServiceImpl.class)
public class ProductCategoryServiceImplTest extends BaseDbUnitTest {

    @Resource
    private ProductCategoryServiceImpl productCategoryService;

    @Resource
    private ProductCategoryMapper productCategoryMapper;

    @Test
    public void testCreateCategory_success() {
        // 准备参数
        ProductCategoryCreateReqVO reqVO = randomPojo(ProductCategoryCreateReqVO.class);
        // mock 父类
        ProductCategoryDO parentProductCategory = randomPojo(ProductCategoryDO.class, o -> o.setId(reqVO.getParentId()));
        productCategoryMapper.insert(parentProductCategory);

        // 调用
        Long categoryId = productCategoryService.createProductCategory(reqVO);
        // 断言
        assertNotNull(categoryId);
        // 校验记录的属性是否正确
        ProductCategoryDO category = productCategoryMapper.selectById(categoryId);
        assertPojoEquals(reqVO, category);
    }

    @Test
    public void testUpdateCategory_success() {
        // mock 数据
        ProductCategoryDO dbCategory = randomPojo(ProductCategoryDO.class);
        productCategoryMapper.insert(dbCategory);// @Sql: 先插入出一条存在的数据
        // 准备参数
        ProductCategoryUpdateReqVO reqVO = randomPojo(ProductCategoryUpdateReqVO.class, o -> {
            o.setId(dbCategory.getId()); // 设置更新的 ID
        });
        // mock 父类
        ProductCategoryDO parentProductCategory = randomPojo(ProductCategoryDO.class, o -> o.setId(reqVO.getParentId()));
        productCategoryMapper.insert(parentProductCategory);

        // 调用
        productCategoryService.updateProductCategory(reqVO);
        // 校验是否更新正确
        ProductCategoryDO category = productCategoryMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, category);
    }

    @Test
    public void testUpdateCategory_notExists() {
        // 准备参数
        ProductCategoryUpdateReqVO reqVO = randomPojo(ProductCategoryUpdateReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> productCategoryService.updateProductCategory(reqVO), PRODUCT_CATEGORY_NOT_EXISTS);
    }

    @Test
    public void testDeleteCategory_success() {
        // mock 数据
        ProductCategoryDO dbCategory = randomPojo(ProductCategoryDO.class);
        productCategoryMapper.insert(dbCategory);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbCategory.getId();

        // 调用
        productCategoryService.deleteProductCategory(id);
        // 校验数据不存在了
        assertNull(productCategoryMapper.selectById(id));
    }

    @Test
    public void testDeleteCategory_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> productCategoryService.deleteProductCategory(id), PRODUCT_CATEGORY_NOT_EXISTS);
    }

    @Test
    public void testGetCategoryList() {
        // mock 数据
        ProductCategoryDO dbCategory = randomPojo(ProductCategoryDO.class, o -> { // 等会查询到
            o.setName("奥特曼");
        });
        productCategoryMapper.insert(dbCategory);
        // 测试 name 不匹配
        productCategoryMapper.insert(cloneIgnoreId(dbCategory, o -> o.setName("奥特块")));
        // 准备参数
        ProductCategoryListReqVO reqVO = new ProductCategoryListReqVO();
        reqVO.setName("特曼");

        // 调用
        List<ProductCategoryDO> list = productCategoryService.getEnableProductCategoryList(reqVO);
        // 断言
        assertEquals(1, list.size());
        assertPojoEquals(dbCategory, list.get(0));
    }

}
