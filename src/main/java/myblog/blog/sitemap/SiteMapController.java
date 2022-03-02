package myblog.blog.sitemap;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/*
    - siteMap.xml 요청
      - 작성 비용을 감안해 캐시처리
      - 조회 빈도가 아직 예측되지않으므로 캐시 만료를 12시간으로 설정
*/
@Controller
@RequiredArgsConstructor
public class SiteMapController {
    private final SiteMapService siteMapService;

    @GetMapping(value = "/sitemap",produces = "application/xml;charset=utf-8")
    public @ResponseBody String getSiteMap() {
        return siteMapService.getSiteMap();
    }
}
