package myblog.blog.member.doamin;

import lombok.Builder;
import lombok.Getter;
import myblog.blog.article.domain.Article;
import myblog.blog.base.domain.BasicEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@SequenceGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ",
        initialValue = 1, allocationSize = 50)
@Getter
public class Member extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, unique = true)
    private String email;

    private String picUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String provider;

    private String providerId;

    @OneToMany(mappedBy = "member")
    private List<Article> articles;

    protected Member() {
    }

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

    public void changeUsername(String username) {
        this.username = username;
    }
}
