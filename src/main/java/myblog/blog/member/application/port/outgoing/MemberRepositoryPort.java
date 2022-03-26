package myblog.blog.member.application.port.outgoing;

import myblog.blog.member.doamin.Member;

import java.util.Optional;

public interface MemberRepositoryPort {
    Member findByEmail(String email);
    void save(Member member);
    Optional<Member> findById(Long memberId);
    Member findByUserId(String providerId);
}
