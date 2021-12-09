package myblog.blog.exception;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.dto.ArticleDtoForMain;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.service.CategoryService;
import myblog.blog.comment.dto.CommentDtoForLayout;
import myblog.blog.comment.service.CommentService;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ExceptionController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    private final CategoryService categoryService;
    private final CommentService commentService;

    @GetMapping("/error")
    public String errorView(Model model, HttpServletRequest request){

        CategoryForView categoryForView = categoryService.getCategoryForView();
        List<CommentDtoForLayout> comments = commentService.recentCommentList();
        //

        model.addAttribute("category",categoryForView);
        model.addAttribute("commentsList", comments);

        return "error";
    }

}
