package myblog.blog.base.config;

import lombok.RequiredArgsConstructor;
import myblog.blog.exception.LoginFailHandler;
import myblog.blog.member.doamin.Role;
import myblog.blog.member.service.Oauth2MemberService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Oauth2MemberService oauth2MemberService;
    private final LoginFailHandler loginFailHandler;

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
                .authorizeRequests()
                .antMatchers("/admin").hasRole(Role.ADMIN.name())
                .anyRequest().permitAll()

                .and()
                .formLogin()
                .loginPage("/login")

                .and()
                .logout()
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID","remember-me")


                .and()
                .oauth2Login()
                .loginPage("/login")
                .failureHandler(loginFailHandler)
                .userInfoEndpoint()
                .userService(oauth2MemberService)

        ;
    }
}
