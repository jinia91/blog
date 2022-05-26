package myblog.blog.member.application;

import lombok.RequiredArgsConstructor;
import myblog.blog.member.application.port.incomming.MemberQueriesUseCase;
import myblog.blog.member.application.port.outgoing.MemberRepositoryPort;

import myblog.blog.member.doamin.Member;

import myblog.blog.member.doamin.NotFoundMemberException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueries implements MemberQueriesUseCase {
    private final MemberRepositoryPort memberRepositoryPort;
    @Override
    public Optional<Member> findById(Long memberId) {
        return memberRepositoryPort.findById(memberId);
    }
}
