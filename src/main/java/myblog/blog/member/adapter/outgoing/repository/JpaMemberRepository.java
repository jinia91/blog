package myblog.blog.member.adapter.outgoing.repository;

import myblog.blog.member.doamin.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMemberRepository extends JpaRepository<Member, Long> {
    /*
        - Id로 유저 찾기
    */
    Member findByUserId(String userId);
    /*
        - Email로 유저 찾기
    */
    Member findByEmail(String email);

}
