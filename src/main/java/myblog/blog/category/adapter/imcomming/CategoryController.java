package myblog.blog.category.adapter.imcomming;

import lombok.RequiredArgsConstructor;

import myblog.blog.category.appliacation.port.incomming.CategoryUseCase;
import myblog.blog.category.appliacation.port.response.CategoryViewForLayout;
import myblog.blog.category.appliacation.port.response.CategorySimpleDto;
import myblog.blog.comment.dto.CommentDtoForLayout;
import myblog.blog.comment.service.CommentService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryUseCase categoryUseCase;
    private final CommentService commentService;
    private final CategoryListValidator categorylistValidator;

    /*
    - 카테고리 수정폼 조회
    */
    @GetMapping("/category/edit")
    public String editCategoryForm(Model model) {

        List<CategorySimpleDto> categoryList = categoryUseCase.getCategorytCountList();
        List<CategorySimpleDto> copyList = new ArrayList<>(List.copyOf(categoryList));
        copyList.remove(0);
        CategoryViewForLayout categoryViewForLayout = CategoryViewForLayout.from(categoryList);
        List<CommentDtoForLayout> comments = commentService.recentCommentList();

        model.addAttribute("categoryForEdit", copyList);
        model.addAttribute("category", categoryViewForLayout);
        model.addAttribute("commentsList", comments);

        return "admin/categoryEdit";
    }
    /*
    - 카테고리 수정 요청
    */
    @PostMapping("/category/edit")
    public @ResponseBody
    String editCategory(@RequestBody List<CategorySimpleDto> categoryList, Errors errors) {
       // List DTO 검증을 위한 커스텀 validator
        categorylistValidator.validate(categoryList, errors);
        categoryUseCase.changeCategory(categoryList);
        return "변경 성공";
    }
}
