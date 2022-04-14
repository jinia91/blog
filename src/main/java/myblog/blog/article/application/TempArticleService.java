package myblog.blog.article.application;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.application.port.incomming.TempArticleDto;
import myblog.blog.article.application.port.incomming.TempArticleUseCase;
import myblog.blog.article.application.port.outgoing.TempArticleRepositoryPort;
import myblog.blog.article.domain.TempArticle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TempArticleService implements TempArticleUseCase {

    private final TempArticleRepositoryPort tempArticleRepositoryPort;

    /*
        - 자동 저장 로직
        - ID값 고정으로 머지를 작동시켜 임시글 DB에 1개 유지
    */
    @Override
    public void saveTemp(String tempArticleContents){
        tempArticleRepositoryPort.save(new TempArticle(tempArticleContents));
    }

    /*
        - 임시글 가져오기
    */
    @Override
    public TempArticleDto getTempArticle(){
        var tempArticle = tempArticleRepositoryPort.findById(1L);
        var tempArticleDto = new TempArticleDto();
        tempArticleDto.setContent(tempArticle.orElse(new TempArticle()).getContent());
        return tempArticleDto;
    }

    /*
        - 임시글 삭제
    */
    @Override
    public void deleteTemp(){
        var deleteArticle = tempArticleRepositoryPort.findById(1L);
        deleteArticle.ifPresent(tempArticleRepositoryPort::delete);
    }
}
