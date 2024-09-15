package cn.iocoder.yudao.module.iot.dal.mysql.thinkmodelfunction;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.iot.dal.dataobject.thinkmodelfunction.IotThinkModelFunctionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IoT 产品物模型 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface IotThinkModelFunctionMapper extends BaseMapperX<IotThinkModelFunctionDO> {

    default IotThinkModelFunctionDO selectByProductIdAndIdentifier(Long productId, String identifier) {
        return selectOne(new LambdaQueryWrapperX<IotThinkModelFunctionDO>().eq(IotThinkModelFunctionDO::getProductId, productId)
                .eq(IotThinkModelFunctionDO::getIdentifier, identifier));
    }

    default List<IotThinkModelFunctionDO> selectListByProductId(Long productId) {
        return selectList(new LambdaQueryWrapperX<IotThinkModelFunctionDO>().eq(IotThinkModelFunctionDO::getProductId, productId));
    }

    default List<IotThinkModelFunctionDO> selectListByProductIdAndType(Long productId, Integer type) {
        return selectList(new LambdaQueryWrapperX<IotThinkModelFunctionDO>().eq(IotThinkModelFunctionDO::getProductId, productId)
                .eq(IotThinkModelFunctionDO::getType, type));
    }
}