package myblog.blog.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.service.CategoryService;
import myblog.blog.comment.dto.CommentDtoForSide;
import myblog.blog.comment.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final CategoryService categoryService;
    private final CommentService commentService;

    @GetMapping("/login")
    public String loginFrom(@RequestParam(value = "error",required = false) String error, Model model){

        if(error!=null&&error.equals("duplicatedEmail")){
            model.addAttribute("errMsg","이미 가입된 이메일입니다.");
        }

        CategoryForView categoryForView = categoryService.getCategoryForView();
        model.addAttribute("category",categoryForView);
        List<CommentDtoForSide> comments = commentService.recentCommentList()
                .stream()
                .map(comment ->
                        new CommentDtoForSide(comment.getId(), comment.getArticle().getId(), comment.getContent()))
                .collect(Collectors.toList());
        model.addAttribute("commentsList", comments);


        return "login";

    }


}
