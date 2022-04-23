package myblog.blog.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
    - 회원가입 실패 핸들러
        이메일 중복시 간편 회원가입 실패
*/
@Component
public class LoginFailHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String errMsg = "";

        if(exception instanceof OAuth2AuthenticationException){
            errMsg = "duplicatedEmail";
            request.setAttribute("errMsg", errMsg);
        }

        setDefaultFailureUrl("/login?error="+errMsg);
        super.onAuthenticationFailure(request, response, exception);
    }
}
