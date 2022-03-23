package myblog.blog.article.adapter.incomming.web;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.TempArticle;
import myblog.blog.article.application.TempArticleService;
import myblog.blog.article.application.port.response.TempArticleResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/*
    - 임시 게시물 조회, 저장을 위한 rest 컨트롤러
*/
@RestController
@RequiredArgsConstructor
public class TempArticleController {

    private final TempArticleService tempArticleService;

    /*
        - 임시 아티클 저장 요청
    */
    @PostMapping("/article/temp/autoSave")
    public String autoSaveTemp(@RequestBody TempArticleResponse tempArticleResponse){

        tempArticleService.saveTemp(new TempArticle(tempArticleResponse.getContent()));

        return "저장성공";
    }

    /*
        - 임시 아티클 조회
    */
    @GetMapping("/article/temp/getTemp")
    public @ResponseBody
    TempArticleResponse getTempArticle(){

        Optional<TempArticle> tempArticle = tempArticleService.getTempArticle();

        TempArticleResponse tempArticleResponse = new TempArticleResponse();
        tempArticleResponse.setContent(tempArticle.orElse(new TempArticle()).getContent());

        return tempArticleResponse;
    }
}
