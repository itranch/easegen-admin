package cn.iocoder.yudao.module.ai.service.music;

import cn.iocoder.yudao.framework.ai.core.model.suno.api.AceDataSunoApi;
import cn.iocoder.yudao.module.ai.controller.admin.music.vo.SunoReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.music.vo.SunoRespVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Author xiaoxin
 * @Date 2024/5/29
 */
@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

    private final AceDataSunoApi aceDataSunoApi;

    @Override
    public SunoRespVO musicGen(SunoReqVO sunoReqVO) {
        AceDataSunoApi.SunoResp sunoResp = aceDataSunoApi.musicGen(new AceDataSunoApi.SunoReq(
                sunoReqVO.getPrompt(),
                sunoReqVO.getLyric(),
                sunoReqVO.isCustom(),
                sunoReqVO.getTitle(),
                sunoReqVO.getStyle(),
                sunoReqVO.getCallbackUrl()
        ));
        return SunoRespVO.convertFrom(sunoResp);
    }
}
