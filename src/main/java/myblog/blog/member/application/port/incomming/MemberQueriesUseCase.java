package myblog.blog.member.application.port.incomming;

import myblog.blog.member.doamin.Member;

import java.util.Optional;

public interface MemberQueriesUseCase {
    Optional<Member> findById(Long memberId);
}
