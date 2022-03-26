package myblog.blog.shared.queries;

import lombok.RequiredArgsConstructor;
import myblog.blog.category.appliacation.port.incomming.CategoryUseCase;
import myblog.blog.category.appliacation.port.incomming.response.CategoryViewForLayout;
import myblog.blog.comment.application.port.incomming.CommentUseCase;
import myblog.blog.comment.application.port.incomming.response.CommentDtoForLayout;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LayoutRenderingQueries {

    private final CategoryUseCase categoryUseCase;
    private final CommentUseCase commentUseCase;

    /*
    - 레이아웃에 필요한 모델 담기
    */
    public void AddLayoutTo(Model model) {
        CategoryViewForLayout categoryViewForLayout = categoryUseCase.getCategoryViewForLayout();
        List<CommentDtoForLayout> comments = commentUseCase.recentCommentListForLayout();
        model.addAttribute("category", categoryViewForLayout);
        model.addAttribute("commentsList", comments);
    }
}
