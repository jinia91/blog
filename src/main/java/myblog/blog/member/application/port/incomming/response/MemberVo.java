package myblog.blog.member.application.port.incomming.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import myblog.blog.member.doamin.Member;

/*
    - 뷰단에 사용할 멈버 DTO
*/
@Getter
@AllArgsConstructor
public class MemberVo {

    private Long id;

    private String username;

    private String userId;

    private String email;

    private String picUrl;

    static public MemberVo from(Member member){
        return new MemberVo(member.getId(),
                member.getUsername(),
                member.getUserId(),
                member.getEmail(),
                member.getPicUrl());
    }
}
