package myblog.blog.member.auth;

import myblog.blog.member.auth.userinfo.*;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UserInfoFactory {

    private final static Map<ProviderType, Function<OAuth2User, Oauth2UserInfo>> userInfoFactoryMap;

    static {
        userInfoFactoryMap = new EnumMap<>(ProviderType.class);
        userInfoFactoryMap.put(ProviderType.GOOGLE, GoogleUserInfo::new);
        userInfoFactoryMap.put(ProviderType.FACEBOOK, FacebookUserInfo::new);
        userInfoFactoryMap.put(ProviderType.KAKAO, KakaoUserInfo::new);
        userInfoFactoryMap.put(ProviderType.NAVER, NaverUserInfo::new);
    }

    private static final Map<String, ProviderType> stringToEnum;

    static {
        stringToEnum =
                Stream.of(ProviderType.values())
                        .collect(Collectors.toMap(ProviderType::getValue, providerType->providerType));
    }

    public Oauth2UserInfo makeOauth2UserinfoOf(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {

        Optional<ProviderType> providerTypeOptional = fromString(oAuth2UserRequest.getClientRegistration().getRegistrationId());

        return userInfoFactoryMap
                .get(providerTypeOptional.orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소셜 로그인 API 제공자입니다.")))
                .apply(oAuth2User);

    }

    private Optional<ProviderType> fromString(String provider){
        return Optional.ofNullable(stringToEnum.get(provider));
    }

}
