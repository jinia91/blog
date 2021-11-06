package myblog.blog.member.repository;

import myblog.blog.member.doamin.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByUserId(String userId);
    Member findByEmail(String email);

}
