package cn.iocoder.yudao.module.ai.dal.mysql.knowledge;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeBaseDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 知识库基础信息 Mapper
 *
 * @author xiaoxin
 */
@Mapper
public interface AiKnowledgeBaseMapper extends BaseMapperX<AiKnowledgeBaseDO> {
}
