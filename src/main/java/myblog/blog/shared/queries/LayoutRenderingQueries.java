package myblog.blog.shared.queries;

import lombok.RequiredArgsConstructor;
import myblog.blog.category.appliacation.port.incomming.CategoryUseCase;
import myblog.blog.category.appliacation.port.response.CategoryViewForLayout;
import myblog.blog.comment.application.port.incomming.CommentDtoForLayout;
import myblog.blog.comment.application.CommentService;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LayoutRenderingQueries {

    private final CategoryUseCase categoryUseCase;
    private final CommentService commentService;

    /*
    - 레이아웃에 필요한 모델 담기
    */
    public void AddLayoutTo(Model model) {
        CategoryViewForLayout categoryViewForLayout = categoryUseCase.getCategoryViewForLayout();
        List<CommentDtoForLayout> comments = commentService.recentCommentList();
        model.addAttribute("category", categoryViewForLayout);
        model.addAttribute("commentsList", comments);
    }
}
