package myblog.blog.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MemberController {

    @GetMapping("/login")
    public String loginFrom(@RequestParam(value = "error",required = false) String error, Model model){

        if(error!=null&&error.equals("duplicatedEmail")){
            model.addAttribute("errMsg","이미 가입된 이메일입니다.");
        }

        return "login";

    }


}
