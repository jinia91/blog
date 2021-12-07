package myblog.blog.member.repository;

import myblog.blog.member.doamin.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    /*
        - Id로 유저 찾기
    */
    Member findByUserId(String userId);
    /*
        - Email로 유저 찾기
    */
    Member findByEmail(String email);

}
