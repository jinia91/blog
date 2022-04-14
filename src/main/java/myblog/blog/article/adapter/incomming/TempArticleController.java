package myblog.blog.article.adapter.incomming;

import myblog.blog.article.application.port.incomming.TempArticleUseCase;
import myblog.blog.article.application.port.incomming.TempArticleDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/*
    - 임시 게시물 조회, 저장을 위한 rest 컨트롤러
*/
@RestController
@RequiredArgsConstructor
public class TempArticleController {

    private final TempArticleUseCase tempArticleUseCase;

    @PostMapping("/article/temp/autoSave")
    public String autoSaveTemp(@RequestBody TempArticleDto tempArticleDto){
        tempArticleUseCase.saveTemp(tempArticleDto.getContent());
        return "저장성공";
    }

    @GetMapping("/article/temp/getTemp")
    public @ResponseBody TempArticleDto getTempArticle(){
        return tempArticleUseCase.getTempArticle();
    }
}
