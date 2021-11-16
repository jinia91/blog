package myblog.blog.member.auth;


import myblog.blog.member.doamin.Member;
import myblog.blog.member.doamin.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class PrincipalDetails implements OAuth2User {

    private final Map<String, Object> attributes;
    private final Member member;


    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    public Long getMemberId(){
        return member.getId();
    }

    public String getMemberPicUrl(){
        return member.getPicUrl();
    }

    public Member getMember(){return member;}

    // Oauth2
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole().getValue()));
        return authorities;
    }

    @Override
    public String getName() {
        return member.getUsername();
    }
}
