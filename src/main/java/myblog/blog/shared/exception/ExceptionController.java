package myblog.blog.shared.exception;

import myblog.blog.shared.application.port.incomming.LayoutRenderingUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ExceptionController implements ErrorController {

    private final LayoutRenderingUseCase layoutRenderingUseCase;

    @GetMapping("/error")
    public String errorView(Model model) {
        layoutRenderingUseCase.AddLayoutTo(model);
        return "error";
    }

}
