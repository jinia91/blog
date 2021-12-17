package myblog.blog.member.auth.userinfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/*
    - 소셜 로그인 API 열거타입 정의
*/
@RequiredArgsConstructor
@Getter
public enum ProviderType {

    GOOGLE("google"),
    NAVER("naver");

    private final String value;

}
