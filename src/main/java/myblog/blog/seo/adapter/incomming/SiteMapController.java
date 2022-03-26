package myblog.blog.seo.adapter.incomming;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import myblog.blog.seo.application.port.incomming.SiteMapUseCase;

/*
    - siteMap.xml 요청
      - 작성 비용을 감안해 캐시처리
      - 조회 빈도가 아직 예측되지않으므로 캐시 만료를 12시간으로 설정
*/
@RestController
@RequiredArgsConstructor
public class SiteMapController {
    private final SiteMapUseCase siteMapUseCase;

    @GetMapping(value = "/sitemap",produces = "application/xml;charset=utf-8")
    String getSiteMap() {
        return siteMapUseCase.getSiteMap();
    }
}
