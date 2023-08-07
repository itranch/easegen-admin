package cn.iocoder.yudao.module.promotion.api.bargain;

import cn.iocoder.yudao.module.promotion.api.bargain.dto.BargainRecordCreateReqDTO;

import javax.validation.Valid;

// TODO @芋艿：后面也再撸撸这几个接口

/**
 * 砍价记录 API 接口
 *
 * @author HUIHUI
 */
public interface BargainRecordApi {

    /**
     * 创建开团记录
     *
     * @param reqDTO 请求 DTO
     */
    void createRecord(@Valid BargainRecordCreateReqDTO reqDTO);

}
