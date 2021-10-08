package cn.iocoder.yudao.userserver.modules.member.controller.auth;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.userserver.modules.member.controller.auth.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "认证")
@RestController
@RequestMapping("/")
@Validated
@Slf4j
public class MbrAuthController {

    @PostMapping("/login")
    @ApiOperation("使用手机 + 密码登录")
    public CommonResult<MbrAuthLoginRespVO> login(@RequestBody @Valid MbrAuthLoginReqVO reqVO) {
//        String token = authService.login(reqVO, getClientIP(), getUserAgent());
//        // 返回结果
//        return success(MbrAuthLoginRespVO.builder().token(token).build());
        return null;
    }

    @PostMapping("/sms-login")
    @ApiOperation("使用手机 + 验证码登录")
    public CommonResult<MbrAuthLoginRespVO> smsLogin(@RequestBody @Valid MbrAuthLoginReqVO reqVO) {
        return null;
    }

    @PostMapping("/send-sms-code")
    @ApiOperation("发送手机验证码")
    public CommonResult<Boolean> sendSmsCode(@RequestBody @Valid MbrAuthSendSmsReqVO reqVO) {
//        passportManager.sendSmsCode(sendSmsCodeDTO, HttpUtil.getIp(request));
//        // 返回成功
//        return success(true);
        return null;
    }

    @PostMapping("/reset-password")
    @ApiOperation(value = "重置密码", notes = "用户忘记密码时使用")
    public CommonResult<Boolean> resetPassword(@RequestBody @Valid MbrAuthResetPasswordReqVO reqVO) {
        return null;
    }

    // ========== 社交登录相关 ==========

    @GetMapping("/social-auth-redirect")
    @ApiOperation("社交授权的跳转")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "社交类型", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "redirectUri", value = "回调路径", dataTypeClass = String.class)
    })
    public CommonResult<String> socialAuthRedirect(@RequestParam("type") Integer type,
                                                   @RequestParam("redirectUri") String redirectUri) {
//        return CommonResult.success(socialService.getAuthorizeUrl(type, redirectUri));
        return null;
    }

    @PostMapping("/social-login")
    @ApiOperation("社交登录，使用 code 授权码")
        public CommonResult<MbrAuthLoginRespVO> socialLogin(@RequestBody @Valid MbrAuthSocialLoginReqVO reqVO) {
//        String token = authService.socialLogin(reqVO, getClientIP(), getUserAgent());
//        // 返回结果
//        return success(MbrAuthLoginRespVO.builder().token(token).build());
        return null;
    }

    @PostMapping("/social-login2")
    @ApiOperation("社交登录，使用 code 授权码 + 账号密码")
    public CommonResult<MbrAuthLoginRespVO> socialLogin2(@RequestBody @Valid MbrAuthSocialLogin2ReqVO reqVO) {
//        String token = authService.socialLogin2(reqVO, getClientIP(), getUserAgent());
//        // 返回结果
//        return success(MbrAuthLoginRespVO.builder().token(token).build());
        return null;
    }

    @PostMapping("/social-bind")
    @ApiOperation("社交绑定，使用 code 授权码")
    public CommonResult<Boolean> socialBind(@RequestBody @Valid MbrAuthSocialBindReqVO reqVO) {
//        authService.socialBind(getLoginUserId(), reqVO);
//        return CommonResult.success(true);
        return null;
    }

    @DeleteMapping("/social-unbind")
    @ApiOperation("取消社交绑定")
    public CommonResult<Boolean> socialUnbind(@RequestBody MbrAuthSocialUnbindReqVO reqVO) {
//        socialService.unbindSocialUser(getLoginUserId(), reqVO.getType(), reqVO.getUnionId());
//        return CommonResult.success(true);
        return null;
    }

}
