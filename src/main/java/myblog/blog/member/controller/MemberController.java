package myblog.blog.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myblog.blog.category.dto.CategoryForMainView;
import myblog.blog.category.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final CategoryService categoryService;

    @GetMapping("/login")
    public String loginFrom(@RequestParam(value = "error",required = false) String error, Model model){

        log.info("중복 조회 이슈 체크, {}");

        if(error!=null&&error.equals("duplicatedEmail")){
            model.addAttribute("errMsg","이미 가입된 이메일입니다.");
        }

        CategoryForMainView categoryForView = categoryService.getCategoryForView();

        model.addAttribute("category",categoryForView);


        return "login";

    }


}
