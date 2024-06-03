package cn.iocoder.yudao.framework.ai.core.model.suno.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Suno API
 * <br>
 * 文档地址：https://github.com/status2xx/suno-api/blob/main/README_CN.md
 *
 * @Author xiaoxin
 * @Date 2024/6/3
 */
@Slf4j
public class SunoApi {

    public static final String DEFAULT_BASE_URL = "https://suno-9323szg26-status2xxs-projects.vercel.app";
    private final WebClient webClient;


    private final Predicate<HttpStatusCode> STATUS_PREDICATE = status -> !status.is2xxSuccessful();
    private final Function<ClientResponse, Mono<? extends Throwable>> EXCEPTION_FUNCTION = response -> response.bodyToMono(String.class)
            .handle((respBody, sink) -> {
                log.error("【suno-api】调用失败！resp: 【{}】", respBody);
                sink.error(new IllegalStateException("【suno-api】调用失败！"));
            });


    public SunoApi() {
        this.webClient = WebClient.builder()
                .baseUrl(DEFAULT_BASE_URL)
                .defaultHeaders((headers) -> headers.setContentType(MediaType.APPLICATION_JSON))
                .build();
    }

    public List<MusicData> generate(SunoApi.SunoReq sunReq) {
        return this.webClient.post()
                .uri("/api/generate")
                .body(Mono.just(sunReq), SunoApi.SunoReq.class)
                .retrieve()
                .onStatus(STATUS_PREDICATE, EXCEPTION_FUNCTION)
                .bodyToMono(new ParameterizedTypeReference<List<MusicData>>() {
                })
                .block();
    }

    public List<MusicData> doChatCompletion(String prompt) {
        return this.webClient.post()
                .uri("/v1/chat/completions")
                .body(Mono.just(new SunoReq(prompt)), SunoApi.SunoReq.class)
                .retrieve()
                .onStatus(STATUS_PREDICATE, EXCEPTION_FUNCTION)
                .bodyToMono(new ParameterizedTypeReference<List<MusicData>>() {
                })
                .block();
    }

    public LyricsData generateLyrics(String prompt) {
        return this.webClient.post()
                .uri("/api/generate_lyrics")
                .body(Mono.just(new SunoReq(prompt)), SunoApi.SunoReq.class)
                .retrieve()
                .onStatus(STATUS_PREDICATE, EXCEPTION_FUNCTION)
                .bodyToMono(LyricsData.class)
                .block();
    }


    public List<MusicData> selectById(String ids) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/get")
                        .queryParam("ids", ids)
                        .build())
                .retrieve()
                .onStatus(STATUS_PREDICATE, EXCEPTION_FUNCTION)
                .bodyToMono(new ParameterizedTypeReference<List<MusicData>>() {
                })
                .block();
    }


    public LimitData selectLimit() {
        return this.webClient.get()
                .uri("/api/get_limit")
                .retrieve()
                .onStatus(STATUS_PREDICATE, EXCEPTION_FUNCTION)
                .bodyToMono(LimitData.class)
                .block();
    }


    /**
     * 根据提示生成音频
     *
     * @param prompt           用于生成音乐音频的提示
     * @param tags             自定义模式才需要
     * @param title            自定义模式才需要
     * @param waitAudio        false表示后台模式，仅返回音频任务信息，需要调用get API获取详细的音频信息。
     *                         true表示同步模式，API最多等待100s，音频生成完毕后直接返回音频链接等信息，建议在GPT等agent中使用。
     * @param makeInstrumental 指示音乐音频是否为定制，如果为 true，则从歌词生成，否则从提示生成
     */
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    public record SunoReq(
            String prompt,
            String tags,
            String title,
            @JsonProperty("wait_audio") boolean waitAudio,
            @JsonProperty("make_instrumental") boolean makeInstrumental
    ) {
        public SunoReq(String prompt) {
            this(prompt, null, null, false, false);
        }

        public SunoReq(String prompt, String tags, String title) {
            this(prompt, tags, title, false, false);
        }
    }


    /**
     * SunoAPI 响应的音频数据。
     *
     * @param id                   音乐数据的 ID
     * @param title                音乐音频的标题
     * @param imageUrl             音乐音频的图片 URL
     * @param lyric                音乐音频的歌词
     * @param audioUrl             音乐音频的 URL
     * @param videoUrl             音乐视频的 URL
     * @param createdAt            音乐音频的创建时间
     * @param modelName
     * @param status
     * @param gptDescriptionPrompt
     * @param prompt               生成音乐音频的提示
     * @param type
     * @param tags
     */
    public record MusicData(
            String id,
            String title,
            @JsonProperty("image_url") String imageUrl,
            String lyric,
            @JsonProperty("audio_url") String audioUrl,
            @JsonProperty("video_url") String videoUrl,
            @JsonProperty("created_at") String createdAt,
            @JsonProperty("model_name") String modelName,
            String status,
            @JsonProperty("gpt_description_prompt") String gptDescriptionPrompt,
            String prompt,
            String type,
            String tags
    ) {
    }


    /**
     * SunoAPI 响应的歌词数据。
     *
     * @param text   歌词
     * @param title  标题
     * @param status 状态
     */
    public record LyricsData(
            String text,
            String title,
            String status
    ) {
    }


    /**
     * SunoAPI 响应的限额数据，目前每日免费50
     */
    public record LimitData(
            @JsonProperty("credits_left") Long creditsLeft,
            String period,
            @JsonProperty("monthly_limit") Long monthlyLimit,
            @JsonProperty("monthly_usage") Long monthlyUsage
    ) {
    }


}
