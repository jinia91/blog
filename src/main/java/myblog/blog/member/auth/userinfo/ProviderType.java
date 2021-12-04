package myblog.blog.member.auth.userinfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProviderType {

    FACEBOOK("facebook"),
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver");

    private final String value;

}
