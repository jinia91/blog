package myblog.blog.member.auth.userinfo;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;


public class KakaoUserInfo implements Oauth2UserInfo {

    private Map<String, Object> attributes;

    public KakaoUserInfo(OAuth2User oAuth2User) {
        this.attributes = oAuth2User.getAttributes();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return ProviderType.KAKAO.getValue();
    }

    @Override
    public String getEmail() {
        Map<String,Object> kakao_account = (Map)attributes.get("kakao_account");
        return kakao_account.get("email").toString();
    }

    @Override
    public String getUserName() {
        Map<String,Object> kakao_account = (Map)attributes.get("kakao_account");
        Map<String,Object> profile = (Map) kakao_account.get("profile");
        return profile.get("nickname").toString()+ "#"+ ((String) attributes.get("id")).substring(0,5);
    }

    @Override
    public String getPicture() {
        Map<String,Object> kakao_account = (Map)attributes.get("kakao_account");
        Map<String,Object> profile = (Map) kakao_account.get("profile");
        return profile.get("profile_image_url").toString();

    }
}
