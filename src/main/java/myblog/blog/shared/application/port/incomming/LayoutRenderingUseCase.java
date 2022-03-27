package myblog.blog.shared.application.port.incomming;

import org.springframework.ui.Model;

public interface LayoutRenderingUseCase {
    void AddLayoutTo(Model model);
}
