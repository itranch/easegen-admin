package cn.iocoder.yudao.module.erp.service.sale;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.controller.admin.sale.vo.order.ErpSaleOrderPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.sale.vo.order.ErpSaleOrderSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpSaleOrderDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpSaleOrderItemDO;
import cn.iocoder.yudao.module.erp.dal.mysql.sale.ErpSaleOrderItemMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.sale.ErpSaleOrderMapper;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import cn.iocoder.yudao.module.erp.enums.ErpAuditStatus;
import cn.iocoder.yudao.module.erp.service.finance.ErpAccountService;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;

// TODO 芋艿：记录操作日志

/**
 * ERP 销售订单 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ErpSaleOrderServiceImpl implements ErpSaleOrderService {

    @Resource
    private ErpSaleOrderMapper saleOrderMapper;
    @Resource
    private ErpSaleOrderItemMapper saleOrderItemMapper;

    @Resource
    private ErpNoRedisDAO noRedisDAO;

    @Resource
    private ErpProductService productService;
    @Resource
    private ErpCustomerService customerService;
    @Resource
    private ErpAccountService accountService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSaleOrder(ErpSaleOrderSaveReqVO createReqVO) {
        // 1.1 校验订单项的有效性
        List<ErpSaleOrderItemDO> saleOrderItems = validateSaleOrderItems(createReqVO.getItems());
        // 1.2 校验客户
        customerService.validateCustomer(createReqVO.getCustomerId());
        // 1.3 校验结算账户
        if (createReqVO.getAccountId() != null) {
            accountService.validateAccount(createReqVO.getAccountId());
        }
        // 1.4 生成调拨单号，并校验唯一性
        String no = noRedisDAO.generate(ErpNoRedisDAO.SALE_ORDER_NO_PREFIX);
        if (saleOrderMapper.selectByNo(no) != null) {
            throw exception(SALE_ORDER_NO_EXISTS);
        }

        // 2.1 插入订单
        ErpSaleOrderDO saleOrder = BeanUtils.toBean(createReqVO, ErpSaleOrderDO.class, in -> in
                .setNo(no).setStatus(ErpAuditStatus.PROCESS.getStatus()));
        calculateTotalPrice(saleOrder, saleOrderItems);
        saleOrderMapper.insert(saleOrder);
        // 2.2 插入订单项
        saleOrderItems.forEach(o -> o.setOrderId(saleOrder.getId()));
        saleOrderItemMapper.insertBatch(saleOrderItems);
        return saleOrder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSaleOrder(ErpSaleOrderSaveReqVO updateReqVO) {
        // 1.1 校验存在
        ErpSaleOrderDO saleOrder = validateSaleOrderExists(updateReqVO.getId());
        if (ErpAuditStatus.APPROVE.getStatus().equals(saleOrder.getStatus())) {
            throw exception(SALE_ORDER_UPDATE_FAIL_APPROVE, saleOrder.getNo());
        }
        // 1.2 校验客户
        customerService.validateCustomer(updateReqVO.getCustomerId());
        // 1.3 校验结算账户
        if (updateReqVO.getAccountId() != null) {
            accountService.validateAccount(updateReqVO.getAccountId());
        }
        // 1.4 校验订单项的有效性
        List<ErpSaleOrderItemDO> saleOrderItems = validateSaleOrderItems(updateReqVO.getItems());

        // 2.1 更新订单
        ErpSaleOrderDO updateObj = BeanUtils.toBean(updateReqVO, ErpSaleOrderDO.class);
        calculateTotalPrice(updateObj, saleOrderItems);
        saleOrderMapper.updateById(updateObj);
        // 2.2 更新订单项
        updateSaleOrderItemList(updateReqVO.getId(), saleOrderItems);
    }

    private void calculateTotalPrice(ErpSaleOrderDO saleOrder, List<ErpSaleOrderItemDO> saleOrderItems) {
        saleOrder.setTotalCount(getSumValue(saleOrderItems, ErpSaleOrderItemDO::getCount, BigDecimal::add));
        saleOrder.setTotalProductPrice(getSumValue(saleOrderItems, ErpSaleOrderItemDO::getTotalPrice, BigDecimal::add, BigDecimal.ZERO));
        saleOrder.setTotalTaxPrice(getSumValue(saleOrderItems, ErpSaleOrderItemDO::getTaxPrice, BigDecimal::add, BigDecimal.ZERO));
        saleOrder.setTotalPrice(saleOrder.getTotalProductPrice().add(saleOrder.getTotalTaxPrice()));
        // 计算优惠价格
        if (saleOrder.getDiscountPercent() == null) {
            saleOrder.setDiscountPercent(BigDecimal.ZERO);
        }
        saleOrder.setDiscountPrice(MoneyUtils.priceMultiply(saleOrder.getTotalPrice(), saleOrder.getDiscountPercent()));
        saleOrder.setTotalPrice(saleOrder.getTotalPrice().subtract(saleOrder.getDiscountPrice()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSaleOrderStatus(Long id, Integer status) {
        boolean approve = ErpAuditStatus.APPROVE.getStatus().equals(status);
        // 1.1 校验存在
        ErpSaleOrderDO saleOrder = validateSaleOrderExists(id);
        // 1.2 校验状态
        if (saleOrder.getStatus().equals(status)) {
            throw exception(approve ? SALE_ORDER_APPROVE_FAIL : SALE_ORDER_PROCESS_FAIL);
        }
        // TODO @芋艿：需要校验是不是有入库、有退货

        // 2. 更新状态
        int updateCount = saleOrderMapper.updateByIdAndStatus(id, saleOrder.getStatus(),
                new ErpSaleOrderDO().setStatus(status));
        if (updateCount == 0) {
            throw exception(approve ? SALE_ORDER_APPROVE_FAIL : SALE_ORDER_PROCESS_FAIL);
        }
    }

    private List<ErpSaleOrderItemDO> validateSaleOrderItems(List<ErpSaleOrderSaveReqVO.Item> list) {
        // 1. 校验产品存在
        List<ErpProductDO> productList = productService.validProductList(
                convertSet(list, ErpSaleOrderSaveReqVO.Item::getProductId));
        Map<Long, ErpProductDO> productMap = convertMap(productList, ErpProductDO::getId);
        // 2. 转化为 ErpSaleOrderItemDO 列表
        return convertList(list, o -> BeanUtils.toBean(o, ErpSaleOrderItemDO.class, item -> {
            item.setProductUnitId(productMap.get(item.getProductId()).getUnitId());
            item.setTotalPrice(MoneyUtils.priceMultiply(item.getProductPrice(), item.getCount()));
            if (item.getTotalPrice() == null) {
                return;
            }
            item.setTaxPrice(MoneyUtils.priceMultiply(item.getTotalPrice(), item.getTaxPercent()));
            item.setTotalPrice(item.getTotalPrice().add(item.getTaxPrice()));
        }));
    }

    private void updateSaleOrderItemList(Long id, List<ErpSaleOrderItemDO> newList) {
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<ErpSaleOrderItemDO> oldList = saleOrderItemMapper.selectListByOrderId(id);
        List<List<ErpSaleOrderItemDO>> diffList = diffList(oldList, newList, // id 不同，就认为是不同的记录
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(o -> o.setOrderId(id));
            saleOrderItemMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            saleOrderItemMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            saleOrderItemMapper.deleteBatchIds(convertList(diffList.get(2), ErpSaleOrderItemDO::getId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSaleOrder(List<Long> ids) {
        // 1. 校验不处于已审批
        List<ErpSaleOrderDO> saleOrders = saleOrderMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(saleOrders)) {
            return;
        }
        saleOrders.forEach(saleOrder -> {
            if (ErpAuditStatus.APPROVE.getStatus().equals(saleOrder.getStatus())) {
                throw exception(SALE_ORDER_DELETE_FAIL_APPROVE, saleOrder.getNo());
            }
        });

        // 2. 遍历删除，并记录操作日志
        saleOrders.forEach(saleOrder -> {
            // 2.1 删除订单
            saleOrderMapper.deleteById(saleOrder.getId());
            // 2.2 删除订单项
            saleOrderItemMapper.deleteByOrderId(saleOrder.getId());
        });
    }

    private ErpSaleOrderDO validateSaleOrderExists(Long id) {
        ErpSaleOrderDO saleOrder = saleOrderMapper.selectById(id);
        if (saleOrder == null) {
            throw exception(SALE_ORDER_NOT_EXISTS);
        }
        return saleOrder;
    }

    @Override
    public ErpSaleOrderDO getSaleOrder(Long id) {
        return saleOrderMapper.selectById(id);
    }

    @Override
    public PageResult<ErpSaleOrderDO> getSaleOrderPage(ErpSaleOrderPageReqVO pageReqVO) {
        return saleOrderMapper.selectPage(pageReqVO);
    }

    // ==================== 订单项 ====================

    @Override
    public List<ErpSaleOrderItemDO> getSaleOrderItemListByOrderId(Long orderId) {
        return saleOrderItemMapper.selectListByOrderId(orderId);
    }

    @Override
    public List<ErpSaleOrderItemDO> getSaleOrderItemListByOrderIds(Collection<Long> orderIds) {
        if (CollUtil.isEmpty(orderIds)) {
            return Collections.emptyList();
        }
        return saleOrderItemMapper.selectListByOrderIds(orderIds);
    }

}