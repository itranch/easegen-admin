package cn.iocoder.yudao.module.crm.service.permission.bo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.crm.dal.dataobject.permission.CrmPermissionDO;
import cn.iocoder.yudao.module.crm.framework.enums.CrmBizTypeEnum;
import cn.iocoder.yudao.module.crm.framework.enums.CrmPermissionLevelEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * crm 数据权限 Update Req BO
 *
 * @author HUIHUI
 */
@Data
public class CrmPermissionUpdateReqBO {

    /**
     * 数据权限编号 {@link CrmPermissionDO#getId()}
     */
    @NotNull(message = "Crm 数据权限编号不能为空")
    private Long id;

    /**
     * 当前登录用户编号
     */
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    /**
     * Crm 类型
     */
    @NotNull(message = "Crm 类型不能为空")
    @InEnum(CrmBizTypeEnum.class)
    private Integer bizType;

    /**
     * 数据编号
     */
    @NotNull(message = "Crm 数据编号不能为空")
    private Long bizId;

    /**
     * 权限级别
     * 关联 {@link CrmPermissionLevelEnum}
     */
    @NotNull(message = "权限级别不能为空")
    @InEnum(CrmPermissionLevelEnum.class)
    private Integer permissionLevel;

}
