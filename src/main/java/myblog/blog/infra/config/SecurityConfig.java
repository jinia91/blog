package myblog.blog.infra.config;

import lombok.RequiredArgsConstructor;
import myblog.blog.infra.exception.LoginFailHandler;
import myblog.blog.member.doamin.Role;
import myblog.blog.member.application.Oauth2MemberService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Oauth2MemberService oauth2MemberService;
    private final LoginFailHandler loginFailHandler;

    /*
        - 인가 절차 제외 리소스
    */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/css/**", "/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 인가
                .authorizeRequests()
                .antMatchers("/article/write", "/article/edit","/article/delete","/edit/category", "/category/edit").hasRole(Role.ADMIN.name())
                .antMatchers("/comment/write","/comment/delete").authenticated()
                .anyRequest().permitAll()

                // 로그아웃
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID","remember-me")

                //csrf
                .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())


                // OAuth2 로그인 인증
                .and()
                .oauth2Login()
                .loginPage("/login")
                .failureHandler(loginFailHandler)
                .userInfoEndpoint()
                .userService(oauth2MemberService)
        ;
    }
}
