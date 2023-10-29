package cn.iocoder.yudao.module.crm.service.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crm.controller.admin.business.vo.*;
import cn.iocoder.yudao.module.crm.convert.business.CrmBusinessConvert;
import cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessDO;
import cn.iocoder.yudao.module.crm.dal.mysql.business.CrmBusinessMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.crm.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.crm.framework.utils.AuthUtil.isReadAndWrite;

/**
 * 商机 Service 实现类
 *
 * @author ljlleo
 */
@Service
@Validated
public class CrmBusinessServiceImpl implements CrmBusinessService {

    @Resource
    private CrmBusinessMapper businessMapper;

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public Long createBusiness(CrmBusinessCreateReqVO createReqVO) {
        // 插入
        CrmBusinessDO business = CrmBusinessConvert.INSTANCE.convert(createReqVO);
        businessMapper.insert(business);
        // 返回
        return business.getId();
    }

    @Override
    public void updateBusiness(CrmBusinessUpdateReqVO updateReqVO) {
        // 校验存在
        validateBusinessExists(updateReqVO.getId());
        // 更新
        CrmBusinessDO updateObj = CrmBusinessConvert.INSTANCE.convert(updateReqVO);
        businessMapper.updateById(updateObj);
    }

    @Override
    public void deleteBusiness(Long id) {
        // 校验存在
        validateBusinessExists(id);
        // 删除
        businessMapper.deleteById(id);
    }

    private CrmBusinessDO validateBusinessExists(Long id) {
        CrmBusinessDO crmBusiness = businessMapper.selectById(id);
        if (crmBusiness == null) {
            throw exception(BUSINESS_NOT_EXISTS);
        }
        return crmBusiness;
    }

    @Override
    public CrmBusinessDO getBusiness(Long id) {
        return businessMapper.selectById(id);
    }

    @Override
    public List<CrmBusinessDO> getBusinessList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return ListUtil.empty();
        }
        return businessMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<CrmBusinessDO> getBusinessPage(CrmBusinessPageReqVO pageReqVO) {
        return businessMapper.selectPage(pageReqVO);
    }

    @Override
    public List<CrmBusinessDO> getBusinessList(CrmBusinessExportReqVO exportReqVO) {
        return businessMapper.selectList(exportReqVO);
    }

    // TODO @puhui999：动名词哈。transferBusiness
    @Override
    public void businessTransfer(CrmBusinessTransferReqVO reqVO, Long userId) {
        // 1.1 校验商机是否存在
        CrmBusinessDO business = validateBusinessExists(reqVO.getId());
        // 1.2 校验用户是否拥有读写权限
        if (!isReadAndWrite(business.getRwUserIds(), userId)) {
            throw exception(BUSINESS_TRANSFER_FAIL_PERMISSION_DENIED);
        }
        // TODO @puhui999：如果已经是该负责人，抛个业务异常；
        // 1.3 校验新负责人是否存在
        AdminUserRespDTO user = adminUserApi.getUser(reqVO.getOwnerUserId());
        if (user == null) {
            throw exception(BUSINESS_TRANSFER_FAIL_OWNER_USER_NOT_EXISTS);
        }

        // 2. 更新新的负责人
        CrmBusinessDO updateBusiness = CrmBusinessConvert.INSTANCE.convert(business, reqVO, userId);
        businessMapper.updateById(updateBusiness);

        // 4. TODO 记录商机转移日志
    }

}
