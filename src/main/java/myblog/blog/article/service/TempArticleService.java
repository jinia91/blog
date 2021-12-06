package myblog.blog.article.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.TempArticle;
import myblog.blog.article.repository.TempArticleRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TempArticleService {

    private final TempArticleRepository tempArticleRepository;

    /*
        - 자동 저장 로직
        - ID값 고정으로 머지를 작동시켜 임시글 DB에 1개 유지
    */
    public void saveTemp(TempArticle tempArticle){
        tempArticleRepository.save(tempArticle);
    }

    /*
        - 임시글 가져오기
    */
    public Optional<TempArticle> getTempArticle(){
        return tempArticleRepository.findById(1L);
    }

    /*
        - 임시글 삭제
    */
    public void deleteTemp(){
        Optional<TempArticle> deleteArticle = tempArticleRepository.findById(1L);
        deleteArticle.ifPresent(tempArticleRepository::delete);
    }

}
