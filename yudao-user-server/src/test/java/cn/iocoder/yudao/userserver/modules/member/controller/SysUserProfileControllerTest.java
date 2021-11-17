package cn.iocoder.yudao.userserver.modules.member.controller;

import cn.iocoder.yudao.userserver.modules.member.controller.user.SysUserProfileController;
import cn.iocoder.yudao.userserver.modules.member.service.user.MbrUserService;
import cn.iocoder.yudao.userserver.modules.system.service.sms.SysSmsCodeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link SysUserProfileController} 的单元测试类
 *
 * @author 宋天
 */
public class SysUserProfileControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private SysUserProfileController sysUserProfileController;

    @Mock
    private MbrUserService userService;

    @Mock
    private SysSmsCodeService smsCodeService;

    @Before
    public void setup() {
        // 初始化
        MockitoAnnotations.openMocks(this);

        // 构建mvc环境
        mockMvc = MockMvcBuilders.standaloneSetup(sysUserProfileController).build();
    }

    @Test
    public void testUpdateMobile_success() throws Exception {
        //模拟接口调用
        this.mockMvc.perform(post("/system/user/profile/update-mobile")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"mobile\":\"15819844280\",\"code\":\"123456\"}}"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }


}
