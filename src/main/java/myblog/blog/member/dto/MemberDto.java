package myblog.blog.member.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter @Setter
public class MemberDto {

    private Long id;

    private String username;

    private String userId;

    private String email;

    private String picUrl;


}
