package myblog.blog.member.adapter.outgoing.repository;

import myblog.blog.member.application.port.outgoing.MemberRepositoryPort;
import myblog.blog.member.doamin.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberRepositoryAdapter implements MemberRepositoryPort {
    private final JpaMemberRepository jpaMemberRepository;

    @Override
    public Member findByEmail(String email) {
        return jpaMemberRepository.findByEmail(email);
    }
    @Override
    public void save(Member member) {
        jpaMemberRepository.save(member);
    }
    @Override
    public Optional<Member> findById(Long memberId) {
        return jpaMemberRepository.findById(memberId);
    }
    @Override
    public Member findByUserId(String providerId) {
        return jpaMemberRepository.findByUserId(providerId);
    }
}
