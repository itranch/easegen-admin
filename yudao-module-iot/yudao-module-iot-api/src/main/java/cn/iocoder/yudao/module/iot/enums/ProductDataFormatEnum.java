package cn.iocoder.yudao.module.iot.enums;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 产品数据格式枚举类
 * 1. 标准数据格式（JSON）2. 透传/自定义
 */
@AllArgsConstructor
@Getter
public enum ProductDataFormatEnum implements IntArrayValuable {

    JSON(1, "标准数据格式（JSON）"),
    SCRIPT(2, "透传/自定义");

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 描述
     */
    private final String description;

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(ProductDataFormatEnum::getType).toArray();

    @Override
    public int[] array() {
        return ARRAYS;
    }

}
