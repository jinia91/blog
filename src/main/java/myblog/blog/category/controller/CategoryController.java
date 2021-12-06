package myblog.blog.category.controller;

import lombok.RequiredArgsConstructor;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.dto.CategoryNormalDto;
import myblog.blog.category.service.CategoryService;
import myblog.blog.comment.dto.CommentDtoForSide;
import myblog.blog.comment.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CommentService commentService;
    private final ModelMapper modelMapper;

    /*
    - 카테고리 수정폼 조회
    */
    @GetMapping("/edit/category")
    public String editCategoryForm(Model model) {

        // DTO 매핑 전처리
        List<CategoryNormalDto> categoryList = categoryService.getCategoryForView();

        List<CategoryNormalDto> copyList = cloneList(categoryList);
        copyList.remove(0);

        CategoryForView categoryForView = CategoryForView.createCategory(categoryList);

        List<CommentDtoForSide> comments = commentService.recentCommentList()
                .stream()
                .map(comment ->
                        new CommentDtoForSide(comment.getId(), comment.getArticle().getId(), comment.getContent(),comment.isSecret()))
                .collect(Collectors.toList());
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
    public @ResponseBody String editCategory(@RequestBody List<CategoryNormalDto> categoryList){
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
