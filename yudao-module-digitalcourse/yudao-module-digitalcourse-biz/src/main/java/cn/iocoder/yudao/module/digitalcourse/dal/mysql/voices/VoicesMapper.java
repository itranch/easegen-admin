package cn.iocoder.yudao.module.digitalcourse.dal.mysql.voices;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.digitalcourse.dal.dataobject.voices.VoicesDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.digitalcourse.controller.admin.voices.vo.*;

/**
 * 声音管理 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface VoicesMapper extends BaseMapperX<VoicesDO> {

    default PageResult<VoicesDO> selectPage(VoicesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<VoicesDO>()
                .likeIfPresent(VoicesDO::getName, reqVO.getName())
                .eqIfPresent(VoicesDO::getLanguage, reqVO.getLanguage())
                .eqIfPresent(VoicesDO::getGender, reqVO.getGender())
                .eqIfPresent(VoicesDO::getQuality, reqVO.getQuality())
                .eqIfPresent(VoicesDO::getVoiceType, reqVO.getVoiceType())
                .betweenIfPresent(VoicesDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(VoicesDO::getStatus, reqVO.getStatus())
                .orderByDesc(VoicesDO::getId));
    }

}