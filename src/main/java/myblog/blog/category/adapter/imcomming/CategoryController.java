package myblog.blog.category.adapter.imcomming;

import lombok.RequiredArgsConstructor;

import myblog.blog.category.appliacation.port.incomming.CategoryQueriesUseCase;
import myblog.blog.category.appliacation.port.incomming.CategoryUseCase;
import myblog.blog.category.appliacation.port.incomming.response.CategoryViewForLayout;
import myblog.blog.category.appliacation.port.incomming.response.CategorySimpleDto;
import myblog.blog.comment.application.port.incomming.CommentQueriesUseCase;
import myblog.blog.comment.application.port.incomming.response.CommentDtoForLayout;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryUseCase categoryUseCase;
    private final CategoryQueriesUseCase categoryQueriesUseCase;
    private final CommentQueriesUseCase commentQueriesUseCase;
    private final CategoryListValidator categorylistValidator;

    /*
    - 카테고리 수정폼 조회
    */
    @GetMapping("/category/edit")
    String editCategoryForm(Model model) {

        List<CategorySimpleDto> categoryList = categoryQueriesUseCase.getCategorytCountList();
        List<CategorySimpleDto> copyList = new ArrayList<>(List.copyOf(categoryList));
        copyList.remove(0);
        CategoryViewForLayout categoryViewForLayout = CategoryViewForLayout.from(categoryList);
        List<CommentDtoForLayout> comments = commentQueriesUseCase.recentCommentListForLayout();

        model.addAttribute("categoryForEdit", copyList);
        model.addAttribute("category", categoryViewForLayout);
        model.addAttribute("commentsList", comments);

        return "admin/categoryEdit";
    }
    /*
    - 카테고리 수정 요청
    */
    @PostMapping("/category/edit")
    @ResponseBody String editCategory(@RequestBody List<CategorySimpleDto> categoryList, Errors errors) {
       // List DTO 검증을 위한 커스텀 validator
        categorylistValidator.validate(categoryList, errors);
        categoryUseCase.changeCategory(categoryList);
        return "변경 성공";
    }
}
