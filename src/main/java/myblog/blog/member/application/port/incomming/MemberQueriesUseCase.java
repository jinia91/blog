package myblog.blog.member.application.port.incomming;

import myblog.blog.member.doamin.Member;

public interface MemberQueriesUseCase {
    Member findById(Long memberId);
}
