package myblog.blog.member.auth.userinfo;

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


}
