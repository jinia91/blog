package myblog.blog.shared.application;

import myblog.blog.category.appliacation.port.incomming.CategoryQueriesUseCase;
import myblog.blog.comment.application.port.incomming.CommentQueriesUseCase;
import myblog.blog.shared.application.port.incomming.LayoutRenderingUseCase;
import myblog.blog.category.appliacation.port.incomming.response.CategoryViewForLayout;
import myblog.blog.comment.application.port.incomming.response.CommentDtoForLayout;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LayoutRenderingQueries implements LayoutRenderingUseCase {

    private final CommentQueriesUseCase commentQueriesUseCase;
    private final CategoryQueriesUseCase categoryQueriesUseCase;

    /*
    - 레이아웃에 필요한 모델 담기
    */
    @Override
    public void AddLayoutTo(Model model) {
        var categoryViewForLayout = categoryQueriesUseCase.getCategoryViewForLayout();
        var comments = commentQueriesUseCase.recentCommentListForLayout();
        model.addAttribute("category", categoryViewForLayout);
        model.addAttribute("commentsList", comments);
    }
}
