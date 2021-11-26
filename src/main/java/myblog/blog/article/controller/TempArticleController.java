package myblog.blog.article.controller;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.TempArticle;
import myblog.blog.article.dto.TempArticleDto;
import myblog.blog.article.service.TempArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class TempArticleController {

    private final TempArticleService tempArticleService;

    @PostMapping("/article/temp/autoSave")
    public String autoSaveTemp(@RequestBody TempArticleDto tempArticleDto){

        tempArticleService.saveTemp(tempArticleDto);

        return "OK";

    }

    @GetMapping("/article/temp/getTemp")
    public @ResponseBody TempArticleDto getTempArticle(){

        Optional<TempArticle> tempArticle = tempArticleService.getTempArticle();

        TempArticleDto tempArticleDto = new TempArticleDto();
        tempArticleDto.setContent(tempArticle.orElse(new TempArticle()).getContent());

        return tempArticleDto;

    }

}
