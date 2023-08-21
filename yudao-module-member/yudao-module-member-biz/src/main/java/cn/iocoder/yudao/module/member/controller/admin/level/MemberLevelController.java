package cn.iocoder.yudao.module.member.controller.admin.level;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.level.vo.level.*;
import cn.iocoder.yudao.module.member.convert.level.MemberLevelConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.level.MemberLevelDO;
import cn.iocoder.yudao.module.member.service.level.MemberLevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 会员等级")
@RestController
@RequestMapping("/member/level")
@Validated
public class MemberLevelController {

    @Resource
    private MemberLevelService levelService;

    @PostMapping("/create")
    @Operation(summary = "创建会员等级")
    @PreAuthorize("@ss.hasPermission('member:level:create')")
    public CommonResult<Long> createLevel(@Valid @RequestBody MemberLevelCreateReqVO createReqVO) {
        return success(levelService.createLevel(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新会员等级")
    @PreAuthorize("@ss.hasPermission('member:level:update')")
    public CommonResult<Boolean> updateLevel(@Valid @RequestBody MemberLevelUpdateReqVO updateReqVO) {
        levelService.updateLevel(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除会员等级")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('member:level:delete')")
    public CommonResult<Boolean> deleteLevel(@RequestParam("id") Long id) {
        levelService.deleteLevel(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得会员等级")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('member:level:query')")
    public CommonResult<MemberLevelRespVO> getLevel(@RequestParam("id") Long id) {
        MemberLevelDO level = levelService.getLevel(id);
        return success(MemberLevelConvert.INSTANCE.convert(level));
    }

    // TODO @疯狂：这个应该用不到哈
    @GetMapping("/list")
    @Operation(summary = "获得会员等级列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('member:level:query')")
    public CommonResult<List<MemberLevelRespVO>> getLevelList(@RequestParam("ids") Collection<Long> ids) {
        List<MemberLevelDO> list = levelService.getLevelList(ids);
        return success(MemberLevelConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获取会员等级精简信息列表", description = "只包含被开启的会员等级，主要用于前端的下拉选项")
    public CommonResult<List<MemberLevelSimpleRespVO>> getSimpleLevelList() {
        // 获用户列表，只要开启状态的
        List<MemberLevelDO> list = levelService.getEnableLevelList();
        // 排序后，返回给前端
        return success(MemberLevelConvert.INSTANCE.convertSimpleList(list));
    }

    // TODO @疯狂：是不是不做分页，直接 list 就好啦；返回的时候，按照经验排序下哈；
    @GetMapping("/page")
    @Operation(summary = "获得会员等级分页")
    @PreAuthorize("@ss.hasPermission('member:level:query')")
    public CommonResult<PageResult<MemberLevelRespVO>> getLevelPage(@Valid MemberLevelPageReqVO pageVO) {
        PageResult<MemberLevelDO> pageResult = levelService.getLevelPage(pageVO);
        return success(MemberLevelConvert.INSTANCE.convertPage(pageResult));
    }

}
