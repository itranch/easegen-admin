package cn.iocoder.yudao.module.digitalcourse.controller.admin.coursemedia.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 课程媒体 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CourseMediaRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "12757")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "状态 (1、合成中，2、成功，3、失败)", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态 (1、合成中，2、成功，3、失败)")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "时长")
    @ExcelProperty("时长")
    private Long duration;

    @Schema(description = "合成时间")
    @ExcelProperty("合成时间")
    private String finishTime;

    @Schema(description = "媒体类型", example = "1")
    @ExcelProperty("媒体类型")
    private String mediaType;

    @Schema(description = "名称", example = "芋艿")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "媒体链接", example = "https://www.iocoder.cn")
    @ExcelProperty("媒体链接")
    private String previewUrl;

    @Schema(description = "进度（预留）")
    @ExcelProperty("进度（预留）")
    private Integer progress;

    @Schema(description = "课程id", example = "9112")
    @ExcelProperty("课程id")
    private Long courseId;

    @Schema(description = "课程名称", example = "李四")
    @ExcelProperty("课程名称")
    private String courseName;

}