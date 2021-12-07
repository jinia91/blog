package myblog.blog.member.auth;

import myblog.blog.member.auth.userinfo.*;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
    - 로그인 루트에 따른 UserInfo 생성 클래스
        팩터리 메서드 패턴으로 하위타입 반환
*/
@Component
public class UserInfoFactory {

    // 하위타입 생성 메소드와 열거타입 매핑
    private final static Map<ProviderType, Function<OAuth2User, Oauth2UserInfo>> userInfoFactoryMap;
    static {
        userInfoFactoryMap = new EnumMap<>(ProviderType.class);
        userInfoFactoryMap.put(ProviderType.GOOGLE, GoogleUserInfo::new);
        userInfoFactoryMap.put(ProviderType.FACEBOOK, FacebookUserInfo::new);
        userInfoFactoryMap.put(ProviderType.NAVER, NaverUserInfo::new);
    }

    // String 파라미터를 열거타입으로 컨버팅하기위한 매핑
    private static final Map<String, ProviderType> stringToEnum;
    static {
        stringToEnum =
                Stream.of(ProviderType.values())
                        .collect(Collectors.toMap(ProviderType::getValue, providerType->providerType));
    }

    /*
        - 팩토리 메소드
    */
    public Oauth2UserInfo makeOauth2UserinfoOf(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {

        Optional<ProviderType> providerTypeOptional = fromString(oAuth2UserRequest.getClientRegistration().getRegistrationId());

        return userInfoFactoryMap
                .get(providerTypeOptional.orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소셜 로그인 API 제공자입니다.")))
                .apply(oAuth2User);

    }

    /*
        - String을 열거타입으로 컨버팅 로직
            존재하지 않는 요청위험 고려해서 Optional처리
    */
    private Optional<ProviderType> fromString(String provider){
        return Optional.ofNullable(stringToEnum.get(provider));
    }

}
