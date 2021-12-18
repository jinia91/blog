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
@Transactional
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


    /*
        - OAuth2 인증 로그인
    */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Oauth2UserInfo userInfo =
                userInfoFactory.makeOauth2UserinfoOf(userRequest, oAuth2User);

        Member member = getOrJoinMember(userInfo);

        return new PrincipalDetails(member, userInfo.getAttributes());
    }

    /*
        - 회원가입 or 로그인 로직
    */
    private Member getOrJoinMember(Oauth2UserInfo userInfo) {

        //DB에서 조회해서 존재시 로그인처리, 미존재시 가입처리
        Member member = memberRepository.findByUserId(userInfo.getProviderId());

        if(member == null) {

            //Email 중복검증
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

        // 유저 네임 변경시 더티체킹으로 유저네임 변경
        if(!member.getUsername().equals(userInfo.getUserName())){
            member.changeUsername(userInfo.getUserName());
        }

        return member;
    }

    /*
        - 앱 구동시 ADMIN 계정 INSERT
    */
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
