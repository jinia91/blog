package myblog.blog.member.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.member.auth.userinfo.Oauth2UserInfo;
import myblog.blog.member.auth.UserInfoFactory;
import myblog.blog.member.repository.MemberRepository;
import myblog.blog.member.auth.PrincipalDetails;
import myblog.blog.member.doamin.Member;
import myblog.blog.member.doamin.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class Oauth2MemberService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final UserInfoFactory userInfoFactory;

    // 앱 구동시 ADMIN 계정 Insert
    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.picUrl}")
    private String adminPicUrl;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.providerId}")
    private String adminProviderId;
    @Value("${admin.provider}")
    private String adminProvider;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        Oauth2UserInfo userInfo =
                userInfoFactory.makeOauth2UserinfoOf(userRequest, super.loadUser(userRequest));

        Member member = getOrJoinMember(userInfo);

        return new PrincipalDetails(member, userInfo.getAttributes());
    }

    private Member getOrJoinMember(Oauth2UserInfo userInfo) {

        Member member = memberRepository.findByUserId(userInfo.getProviderId());

        if(member == null) {

            if(memberRepository.findByEmail(userInfo.getEmail()) != null)
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

            memberRepository.save(member);
        }

        if(!member.getUsername().equals(userInfo.getUserName())){
            member.changeUsername(userInfo.getUserName());
        }

        return member;
    }

    @PostConstruct
    public void insertAdmin(){

        Member admin = memberRepository.findByEmail(adminEmail);

        if(admin == null){
            admin = Member.builder()
                    .username(adminUsername+"#"+adminProviderId.substring(0,5))
                    .email(adminEmail)
                    .picUrl(adminPicUrl)
                    .userId(adminProviderId)
                    .providerId(adminProviderId)
                    .provider(adminProvider)
                    .role(Role.ADMIN)
                    .build();

            memberRepository.save(admin);
        }

    }

}
