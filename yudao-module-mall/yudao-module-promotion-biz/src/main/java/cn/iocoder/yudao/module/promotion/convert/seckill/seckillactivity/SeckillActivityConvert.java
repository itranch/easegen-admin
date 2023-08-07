package cn.iocoder.yudao.module.promotion.convert.seckill.seckillactivity;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.product.api.spu.dto.ProductSpuRespDTO;
import cn.iocoder.yudao.module.promotion.controller.admin.seckill.vo.activity.SeckillActivityCreateReqVO;
import cn.iocoder.yudao.module.promotion.controller.admin.seckill.vo.activity.SeckillActivityDetailRespVO;
import cn.iocoder.yudao.module.promotion.controller.admin.seckill.vo.activity.SeckillActivityRespVO;
import cn.iocoder.yudao.module.promotion.controller.admin.seckill.vo.activity.SeckillActivityUpdateReqVO;
import cn.iocoder.yudao.module.promotion.controller.admin.seckill.vo.product.SeckillProductBaseVO;
import cn.iocoder.yudao.module.promotion.controller.admin.seckill.vo.product.SeckillProductRespVO;
import cn.iocoder.yudao.module.promotion.dal.dataobject.seckill.seckillactivity.SeckillActivityDO;
import cn.iocoder.yudao.module.promotion.dal.dataobject.seckill.seckillactivity.SeckillProductDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface SeckillActivityConvert {

    SeckillActivityConvert INSTANCE = Mappers.getMapper(SeckillActivityConvert.class);

    SeckillActivityDO convert(SeckillActivityCreateReqVO bean);

    SeckillActivityDO convert(SeckillActivityUpdateReqVO bean);

    SeckillActivityRespVO convert(SeckillActivityDO bean);

    List<SeckillActivityRespVO> convertList(List<SeckillActivityDO> list);

    PageResult<SeckillActivityRespVO> convertPage(PageResult<SeckillActivityDO> page);

    default PageResult<SeckillActivityRespVO> convertPage(PageResult<SeckillActivityDO> page, List<SeckillProductDO> seckillProducts, List<ProductSpuRespDTO> spuList) {
        Map<Long, ProductSpuRespDTO> spuMap = CollectionUtils.convertMap(spuList, ProductSpuRespDTO::getId, c -> c);
        PageResult<SeckillActivityRespVO> pageResult = convertPage(page);
        pageResult.getList().forEach(item -> {
            item.setSpuName(spuMap.get(item.getSpuId()).getName());
            item.setPicUrl(spuMap.get(item.getSpuId()).getPicUrl());
            item.setProducts(convertList2(seckillProducts));
        });
        return pageResult;
    }

    SeckillActivityDetailRespVO convert1(SeckillActivityDO seckillActivity);

    default SeckillActivityDetailRespVO convert(SeckillActivityDO seckillActivity, List<SeckillProductDO> seckillProducts) {
        return convert1(seckillActivity).setProducts(convertList2(seckillProducts));
    }

    // TODO @puhui999：参数改成 activity、product 会不会干净一点哈
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "activityId", source = "activityDO.id"),
            @Mapping(target = "configIds", source = "activityDO.configIds"),
            @Mapping(target = "spuId", source = "activityDO.spuId"),
            @Mapping(target = "skuId", source = "vo.skuId"),
            @Mapping(target = "seckillPrice", source = "vo.seckillPrice"),
            @Mapping(target = "stock", source = "vo.stock"),
            @Mapping(target = "activityStartTime", source = "activityDO.startTime"),
            @Mapping(target = "activityEndTime", source = "activityDO.endTime")
    })
    SeckillProductDO convert(SeckillActivityDO activityDO, SeckillProductBaseVO vo);

    default List<SeckillProductDO> convertList(List<? extends SeckillProductBaseVO> products, SeckillActivityDO activityDO) {
        return CollectionUtils.convertList(products, item -> convert(activityDO, item).setActivityStatus(activityDO.getStatus()));
    }

    List<SeckillProductRespVO> convertList2(List<SeckillProductDO> productDOs);

}
