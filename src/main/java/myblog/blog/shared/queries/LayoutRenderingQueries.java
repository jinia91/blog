package myblog.blog.shared.queries;

import lombok.RequiredArgsConstructor;
import myblog.blog.category.appliacation.port.incomming.CategoryQueriesUseCase;
import myblog.blog.category.appliacation.port.incomming.response.CategoryViewForLayout;
import myblog.blog.comment.application.port.incomming.CommentQueriesUseCase;
import myblog.blog.comment.application.port.incomming.response.CommentDtoForLayout;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LayoutRenderingQueries {

    private final CommentQueriesUseCase commentQueriesUseCase;
    private final CategoryQueriesUseCase categoryQueriesUseCase;

    /*
    - 레이아웃에 필요한 모델 담기
    */
    public void AddLayoutTo(Model model) {
        CategoryViewForLayout categoryViewForLayout = categoryQueriesUseCase.getCategoryViewForLayout();
        List<CommentDtoForLayout> comments = commentQueriesUseCase.recentCommentListForLayout();
        model.addAttribute("category", categoryViewForLayout);
        model.addAttribute("commentsList", comments);
    }
}
