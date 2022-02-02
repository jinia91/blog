package myblog.blog.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.service.CategoryService;
import myblog.blog.comment.dto.CommentDtoForLayout;
import myblog.blog.comment.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

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
        CategoryForView categoryForView = categoryService.getCategoryForView();
        List<CommentDtoForLayout> comments = commentService.recentCommentList();
        //

        model.addAttribute("category",categoryForView);
        model.addAttribute("commentsList", comments);

        return "login";
    }
}
