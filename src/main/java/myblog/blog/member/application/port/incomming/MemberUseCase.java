package myblog.blog.member.application.port.incomming;

import myblog.blog.member.doamin.Member;

public interface MemberUseCase {
    Member findById(Long memberId);
}
