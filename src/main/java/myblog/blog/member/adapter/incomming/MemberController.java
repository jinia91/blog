package myblog.blog.member.adapter.incomming;

import lombok.RequiredArgsConstructor;
import myblog.blog.shared.queries.LayoutRenderingQueries;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final LayoutRenderingQueries layoutRenderingQueries;

    /*
        - 회원 로그인 폼 조회
    */
    @GetMapping("/login")
    String loginFrom(@RequestParam(value = "error",required = false) String error, Model model){
        if(error!=null&&error.equals("duplicatedEmail")){
            model.addAttribute("errMsg","이미 가입된 이메일입니다.");
        }
        layoutRenderingQueries.AddLayoutTo(model);
        return "login";
    }
}
