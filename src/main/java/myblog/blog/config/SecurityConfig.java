package myblog.blog.config;

import lombok.RequiredArgsConstructor;
import myblog.blog.member.service.Oauth2MemberService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Oauth2MemberService oauth2MemberService;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()

                .authorizeRequests()
                .antMatchers("/user").authenticated()
                .antMatchers("/admin").access("hasRole('ROLE_ADMIN')")
                .anyRequest()
                .permitAll()

                .and()
                .formLogin()

                .and()
                .logout()
                .logoutSuccessUrl("/")

                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(oauth2MemberService)


        ;
    }
}
