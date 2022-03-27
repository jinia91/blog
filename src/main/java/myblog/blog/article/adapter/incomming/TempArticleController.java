package myblog.blog.article.adapter.incomming;

import myblog.blog.article.application.port.incomming.TempArticleUseCase;
import myblog.blog.article.application.port.incomming.TempArticleDto;
import myblog.blog.article.domain.TempArticle;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

/*
    - 임시 게시물 조회, 저장을 위한 rest 컨트롤러
*/
@RestController
@RequiredArgsConstructor
public class TempArticleController {

    private final TempArticleUseCase tempArticleUseCase;

    /*
        - 임시 아티클 저장 요청
    */
    @PostMapping("/article/temp/autoSave")
    public String autoSaveTemp(@RequestBody TempArticleDto tempArticleDto){

        tempArticleUseCase.saveTemp(new TempArticle(tempArticleDto.getContent()));
        return "저장성공";
    }

    /*
        - 임시 아티클 조회
    */
    @GetMapping("/article/temp/getTemp")
    public @ResponseBody
    TempArticleDto getTempArticle(){

        Optional<TempArticle> tempArticle = tempArticleUseCase.getTempArticle();
        TempArticleDto tempArticleDto = new TempArticleDto();
        tempArticleDto.setContent(tempArticle.orElse(new TempArticle()).getContent());
        return tempArticleDto;
    }
}
