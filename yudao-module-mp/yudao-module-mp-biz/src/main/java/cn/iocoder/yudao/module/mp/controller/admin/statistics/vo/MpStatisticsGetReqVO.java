package cn.iocoder.yudao.module.mp.controller.admin.statistics.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@ApiModel("管理后台 - 获得统计数据 Request VO")
@Data
public class MpStatisticsGetReqVO {

    @ApiModelProperty(value = "公众号账号的编号", required = true, example = "1024")
    @NotNull(message = "公众号账号的编号不能为空")
    private Long id;

    @ApiModelProperty(value = "查询时间范围")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @NotNull(message = "查询时间范围不能为空")
    private LocalDateTime[] date;

}
