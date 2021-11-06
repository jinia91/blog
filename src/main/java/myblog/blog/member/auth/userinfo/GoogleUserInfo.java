package myblog.blog.member.auth.userinfo;

import java.util.Map;

public class GoogleUserInfo implements Oauth2UserInfo{

    private Map<String,Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
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
        return (String) attributes.get("name");
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
