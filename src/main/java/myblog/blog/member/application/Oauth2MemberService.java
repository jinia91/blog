package myblog.blog.member.application;

import myblog.blog.member.application.port.incomming.MemberUseCase;
import myblog.blog.member.application.port.incomming.response.PrincipalDetails;
import myblog.blog.member.application.port.incomming.response.userinfo.Oauth2UserInfo;
import myblog.blog.member.application.port.outgoing.MemberRepositoryPort;

import myblog.blog.member.doamin.Member;
import myblog.blog.member.doamin.Role;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class Oauth2MemberService extends DefaultOAuth2UserService implements MemberUseCase {

    private final MemberRepositoryPort memberRepositoryPort;
    private final UserInfoFactory userInfoFactory;

    /*
        - OAuth2 인증 로그인
    */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Oauth2UserInfo userInfo =
                userInfoFactory.makeOauth2UserInfoOf(userRequest, oAuth2User);
        Member member = getOrJoinMember(userInfo);
        return new PrincipalDetails(member, userInfo.getAttributes());
    }

    /*
        - 회원가입 or 로그인 로직
    */
    private Member getOrJoinMember(Oauth2UserInfo userInfo) {
        //DB에서 조회해서 존재시 로그인처리, 미존재시 가입처리
        Member member = memberRepositoryPort.findByUserId(userInfo.getProviderId());
        if(member == null) {
            //Email 중복검증
            if(memberRepositoryPort.findByEmail(userInfo.getEmail()) != null)
                throw new OAuth2AuthenticationException("duplicateEmail");
            member = Member.builder()
                    .username(userInfo.getUserName())
                    .picUrl(userInfo.getPicture())
                    .email(userInfo.getEmail())
                    .userId(userInfo.getProviderId())
                    .providerId(userInfo.getProviderId())
                    .provider(userInfo.getProvider())
                    .role(Role.USER)
                    .build();
            memberRepositoryPort.save(member);
        }
        // 유저 네임 변경시 더티체킹으로 유저네임 변경
        if(!member.getUsername().equals(userInfo.getUserName())){
            member.changeUsername(userInfo.getUserName());
        }
        return member;
    }

    @Override
    public Member findById(Long memberId) {
        return memberRepositoryPort.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("NotFoundMemberException"));
    }
}
