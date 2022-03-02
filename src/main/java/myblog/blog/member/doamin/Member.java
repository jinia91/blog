package myblog.blog.member.doamin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import myblog.blog.article.domain.Article;
import myblog.blog.infra.BasicEntity;
import myblog.blog.comment.domain.Comment;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


/*
    - 회원 엔티티
*/
@Entity
@SequenceGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ",
        initialValue = 1, allocationSize = 50)
@Getter
@Setter
public class Member extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    private String picUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String provider;

    private String providerId;

    @OneToMany(mappedBy = "member")
    private List<Article> articles = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> commentList = new ArrayList<>();

    protected Member() {}

    @Builder
    public Member(String username, String email, String picUrl, Role role,String userId, String provider, String providerId) {
        this.username = username;
        this.email = email;
        this.picUrl = picUrl;
        this.userId = userId;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    //비지니스로직

    /*
        - 유저명 변경 더티체킹 로직
    */
    public void changeUsername(String username) {
        this.username = username;
    }
}
