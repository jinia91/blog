package myblog.blog.member.adapter.incomming;

import myblog.blog.shared.application.port.incomming.LayoutRenderingUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final LayoutRenderingUseCase layoutRenderingUseCase;

    /*
        - 회원 로그인 폼 조회
    */
    @GetMapping("/login")
    String loginFrom(@RequestParam(value = "error",required = false) String error, Model model){
        if(error!=null&&error.equals("duplicatedEmail")){
            model.addAttribute("errMsg","이미 가입된 이메일입니다.");
        }
        layoutRenderingUseCase.AddLayoutTo(model);
        return "login";
    }
}
