package myblog.blog.member.adapter.outgoing.repository;

import myblog.blog.member.doamin.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaMemberRepository extends JpaRepository<Member, Long> {
    /*
        - Id로 유저 찾기
    */
    Optional<Member> findByUserId(String userId);
    /*
        - Email로 유저 찾기
    */
    Optional<Member> findByEmail(String email);

}
