package myblog.blog.member.auth;

import myblog.blog.member.controller.MemberController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserInfoFactoryTest {

    @Autowired
    UserInfoFactory userInfoFactory;



    @Test
    public void 싱글톤테스트() throws Exception {
    // given
//        UserInfoFactory userInfoFactory1 = new UserInfoFactory();
    // when

    // then

    }

}