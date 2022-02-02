package myblog.blog.category.controller;

import lombok.RequiredArgsConstructor;
import myblog.blog.exception.CustomFormException;
import myblog.blog.exception.ListValidator;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.dto.CategoryNormalDto;
import myblog.blog.category.service.CategoryService;
import myblog.blog.comment.dto.CommentDtoForLayout;
import myblog.blog.comment.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CommentService commentService;
    private final ModelMapper modelMapper;
    private final ListValidator listValidator;

    /*
    - 카테고리 수정폼 조회
    */
    @GetMapping("/edit/category")
    public String editCategoryForm(Model model) {

        // DTO 매핑 전처리
        List<CategoryNormalDto> categoryList = categoryService.getCategorytCountList();
        List<CategoryNormalDto> copyList = cloneList(categoryList);
        copyList.remove(0);
        CategoryForView categoryForView = CategoryForView.createCategory(categoryList);
        List<CommentDtoForLayout> comments = commentService.recentCommentList();
        //

        model.addAttribute("categoryForEdit", copyList);
        model.addAttribute("category", categoryForView);
        model.addAttribute("commentsList", comments);

        return "admin/categoryEdit";

    }

    /*
    - 카테고리 수정 요청
    */
    @PostMapping("/category/edit")
    public @ResponseBody
    String editCategory(@RequestBody List<CategoryNormalDto> categoryList, Errors errors) {
       // List DTO 검증을 위한 커스텀 validator
        listValidator.validate(categoryList, errors);
        // 유효성 검사
        if (errors.hasErrors()) {
            throw new CustomFormException(Objects.requireNonNull(errors.getFieldError()).getDefaultMessage());
        }

        categoryService.changeCategory(categoryList);
        return "변경 성공";
    }
    private List<CategoryNormalDto> cloneList(List<CategoryNormalDto> categoryList) {
        return categoryList
                .stream()
                .map(categoryNormalDto ->
                        modelMapper.map(categoryNormalDto, CategoryNormalDto.class))
                .collect(Collectors.toList());
    }
}
