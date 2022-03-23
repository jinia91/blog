package myblog.blog.seo.adapter.incomming;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import myblog.blog.article.application.port.incomming.RssUseCase;

    /*
        - rss 피드 발행 요청
          - rss 발행 비용을 감안해 캐시처리
          - 발행빈도가 아직 예측되지않으므로 캐시 만료를 12시간으로 설정
    */
@Controller
@RequiredArgsConstructor
public class RssController {
    private final RssUseCase rssUseCase;

    @GetMapping(value = "/rss",produces = "application/xml;charset=utf-8")
    public @ResponseBody String rssFeed() {
        return rssUseCase.getRssFeed();
    }
}
