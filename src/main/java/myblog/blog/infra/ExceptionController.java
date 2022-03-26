package myblog.blog.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myblog.blog.shared.queries.LayoutRenderingQueries;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ExceptionController implements ErrorController {

    private final LayoutRenderingQueries layoutRenderingQueries;

    @GetMapping("/error")
    public String errorView(Model model) {
        layoutRenderingQueries.AddLayoutTo(model);
        return "error";
    }

}
