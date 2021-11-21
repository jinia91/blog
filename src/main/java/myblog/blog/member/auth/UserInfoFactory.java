package myblog.blog.member.auth;

import myblog.blog.member.auth.userinfo.*;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class UserInfoFactory {

    private final static Map<String, Function<OAuth2User, Oauth2UserInfo>> userInfoFactoryMap;

    static {
        userInfoFactoryMap = new HashMap<>();
        userInfoFactoryMap.put("google", GoogleUserInfo::new);
        userInfoFactoryMap.put("facebook", FacebookUserInfo::new);
        userInfoFactoryMap.put("kakao", FacebookUserInfo::new);
        userInfoFactoryMap.put("naver", FacebookUserInfo::new);
    }

    public Oauth2UserInfo makeOauth2UserinfoOf(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {

        return userInfoFactoryMap
                .get(oAuth2UserRequest.getClientRegistration().getRegistrationId())
                .apply(oAuth2User);

    }


}
