package myblog.blog.member.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

/*
    - 뷰단에 사용할 멈버 DTO
*/
@Getter @Setter
public class MemberDto {

    private Long id;

    private String username;

    private String userId;

    private String email;

    private String picUrl;

}
