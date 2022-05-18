package myblog.blog.member.application;

import myblog.blog.member.application.port.incomming.response.PrincipalDetails;
import myblog.blog.member.application.port.incomming.response.userinfo.Oauth2UserInfo;
import myblog.blog.member.application.port.outgoing.MemberRepositoryPort;

import myblog.blog.member.doamin.Member;

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
public class Oauth2MemberService extends DefaultOAuth2UserService {

    private final MemberRepositoryPort memberRepositoryPort;
    private final UserInfoFactory userInfoFactory;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var oAuth2User = super.loadUser(userRequest);
        var userInfo =
                userInfoFactory.makeOauth2UserInfoOf(userRequest.getClientRegistration().getRegistrationId(), oAuth2User);
        var member = getOrJoinMember(userInfo);
        return new PrincipalDetails(member, userInfo.getAttributes());
    }

    private Member getOrJoinMember(Oauth2UserInfo userInfo) {
        var member = memberRepositoryPort.findByUserId(userInfo.getProviderId())
                .orElseGet(() -> signUpNewMember(userInfo));
        member.renewUsername(userInfo.getUserName());
        return member;
    }

    private Member signUpNewMember(Oauth2UserInfo userInfo) {
        memberRepositoryPort.findByEmail(userInfo.getEmail())
                .ifPresent(alreadyMember -> {
                    throw new OAuth2AuthenticationException("duplicateEmail");});
        var newMember = userInfo.toEntity();
        memberRepositoryPort.save(newMember);
        return newMember;
    }
}
