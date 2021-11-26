package myblog.blog.article.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.TempArticle;
import myblog.blog.article.dto.TempArticleDto;
import myblog.blog.article.repository.TempArticleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TempArticleService {

    private final TempArticleRepository tempArticleRepository;

    public TempArticle saveTemp(TempArticleDto tempArticleDto){

        TempArticle tempArticle = new TempArticle(tempArticleDto.getContent());

        // 머지로 쿼리 한번만 날리기
        tempArticleRepository.save(tempArticle);

        return tempArticle;

    }

    public Optional<TempArticle> getTempArticle(){

        return tempArticleRepository.findById(1L);

    }

    public void deleteTemp(){
        Optional<TempArticle> deleteArticle = tempArticleRepository.findById(1L);
        deleteArticle.ifPresent(tempArticleRepository::delete);
    }

}
