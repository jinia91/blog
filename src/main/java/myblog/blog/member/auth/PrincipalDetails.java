package myblog.blog.member.auth;

import myblog.blog.member.doamin.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/*
    - 멤버 객체를 래핑한 커스텀 Principal 클래스
*/
public class PrincipalDetails implements OAuth2User {

    private final Map<String, Object> attributes;
    private final Member member;

    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }
    public Member getMember(){return member;}

    // 타임리프 뷰단처리를 위한 편의 메소드
    public Long getMemberId(){
        return member.getId();
    }
    public String getMemberPicUrl(){
        return member.getPicUrl();
    }

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
