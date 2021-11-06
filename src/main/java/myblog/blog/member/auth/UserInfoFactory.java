package myblog.blog.member.auth;

import myblog.blog.member.auth.userinfo.*;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class UserInfoFactory {

    public Oauth2UserInfo makeOauth2Userinfo(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {

        if (oAuth2UserRequest.getClientRegistration().getRegistrationId().equals("google")) {
            return new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (oAuth2UserRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            return new FacebookUserInfo(oAuth2User.getAttributes());
        } else if (oAuth2UserRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            return new KakaoUserInfo(oAuth2User.getAttributes());
        } else if (oAuth2UserRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            return new NaverUserInfo(oAuth2User.getAttribute("response"));
        }
        else return null;
    }


}
