package myblog.blog.member.service;

import myblog.blog.member.doamin.Member;
import myblog.blog.member.doamin.Role;
import myblog.blog.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class Oauth2MemberServiceTest {

    @Autowired
    Oauth2MemberService oauth2MemberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 권한테스트() throws Exception {
        // given
        Optional<Member> byId = memberRepository.findById(1L);
        Member admin = byId.get();


        // when

        // then
        assertThat(admin.getRole().toString()).isEqualTo(Role.ADMIN.toString());

    }

}