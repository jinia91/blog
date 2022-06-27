package myblog.blog.badge;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@APIController
public class BadgeController {

    @GetMapping("/badges")
    String generateBadges() {
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"100\" height=\"100%\">" +"<circle cx=\"50\" cy=\"50\" r=\"30\" fill=\"red\">" +"</svg>";
    }
}
