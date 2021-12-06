package myblog.blog.article.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

/*
    - 뷰단 페이징 박스 처리를 위한 핸들러
*/
@Getter @Setter
public class PagingBoxDto {

    private int curPageNum;
    private int lastPageNum;
    private int pNumForNextBtn;

    private int boxStartNum;
    private int boxEndNum;

    private final int displayPageBoxCnt = 5;
    private final int displayArticlePerPage = 5;

    // 스태틱 생성 메소드
    public static PagingBoxDto createOf(int page, int totalArticles) {

        PagingBoxDto box = new PagingBoxDto();
        box.curPageNum = page;
        box.lastPageNum = (int) (Math.ceil(totalArticles / (double) box.displayArticlePerPage));

        // 에러 핸들링
        if(box.curPageNum>box.lastPageNum){
            box.curPageNum = box.lastPageNum;
        }
        if(box.curPageNum<=0){
            box.curPageNum = 1;
        }

        // 페이징박스 시작 번호 계산
        if(box.curPageNum % box.displayPageBoxCnt == 0){
            box.boxStartNum = ((box.curPageNum / box.displayPageBoxCnt)-1) * box.displayPageBoxCnt + 1;
        }else {
            box.boxStartNum = (box.curPageNum / box.displayPageBoxCnt) * box.displayPageBoxCnt +1;
        }

        // 페이징 박스 끝번호 계산
        box.boxEndNum = (int) (Math.ceil(box.curPageNum / (double) box.displayPageBoxCnt) * box.displayPageBoxCnt);

        // 끝번호 예외처리
        if (box.boxEndNum > box.lastPageNum) {
            box.boxEndNum = box.lastPageNum;
        }
        if(box.boxEndNum <= 0){
            box.boxEndNum = 1;
        }

        // 페이징박스 다음버튼 만드는 마지막 페이지 번호
        box.pNumForNextBtn =box.boxEndNum - (box.boxEndNum % box.displayPageBoxCnt);

        return box;
    }
}
