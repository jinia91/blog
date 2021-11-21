package myblog.blog.member.auth.userinfo;

import java.util.Map;

public interface Oauth2UserInfo {

    String getProviderId();

    String getProvider();

    String getEmail();

    String getUserName();

    String getPicture();

    Map<String, Object> getAttributes();


}
