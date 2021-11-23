package myblog.blog.member.auth.userinfo;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;


public class GoogleUserInfo implements Oauth2UserInfo{

    private Map<String,Object> attributes;

    public GoogleUserInfo(OAuth2User oAuth2User) {
        this.attributes = oAuth2User.getAttributes();
    }


    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getUserName() {
        return (String) attributes.get("name")+"#"+((String) attributes.get("sub")).substring(0,5);
    }

    @Override
    public String getPicture() {
        return (String) attributes.get("picture");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
}
