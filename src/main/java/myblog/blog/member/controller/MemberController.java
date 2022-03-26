package myblog.blog.member.controller;

import lombok.RequiredArgsConstructor;
import myblog.blog.category.appliacation.port.response.CategoryViewForLayout;
import myblog.blog.category.appliacation.CategoryService;
import myblog.blog.comment.application.port.incomming.CommentDtoForLayout;
import myblog.blog.comment.application.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final CategoryService categoryService;
    private final CommentService commentService;


    /*
        - 회원 로그인 폼 조회
    */
    @GetMapping("/login")
    public String loginFrom(@RequestParam(value = "error",required = false) String error, Model model){

        // 가입 실패시 에러 메시지 처리
        if(error!=null&&error.equals("duplicatedEmail")){
            model.addAttribute("errMsg","이미 가입된 이메일입니다.");
        }

        // 레이아웃 DTO 전처리
        CategoryViewForLayout categoryViewForLayout = categoryService.getCategoryViewForLayout();
        List<CommentDtoForLayout> comments = commentService.recentCommentList();
        //

        model.addAttribute("category", categoryViewForLayout);
        model.addAttribute("commentsList", comments);

        return "login";
    }
}
