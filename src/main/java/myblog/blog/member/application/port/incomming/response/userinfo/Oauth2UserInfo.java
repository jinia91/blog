package myblog.blog.member.application.port.incomming.response.userinfo;

import myblog.blog.member.doamin.Member;
import myblog.blog.member.doamin.Role;

import java.util.Map;

/*
    - 팩토리 메서드 패턴을 위한 상위타입 인터페이스
*/
public interface Oauth2UserInfo {

    String getProviderId();

    String getProvider();

    String getEmail();

    String getUserName();

    String getPicture();

    Map<String, Object> getAttributes();

    default Member toEntity(){
        return Member.builder()
                .username(this.getUserName())
                .picUrl(this.getPicture())
                .email(this.getEmail())
                .userId(this.getProviderId())
                .providerId(this.getProviderId())
                .provider(this.getProvider())
                .role(Role.USER)
                .build();
    }
}
