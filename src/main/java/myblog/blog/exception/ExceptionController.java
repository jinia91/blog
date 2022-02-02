package myblog.blog.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myblog.blog.layout.LayoutDtoFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ExceptionController implements ErrorController {

    private final LayoutDtoFactory layoutDtoFactory;

    @GetMapping("/error")
    public String errorView(Model model) {
        layoutDtoFactory.AddLayoutTo(model);
        return "error";
    }

}
