package com.dragonguard.backend.batch;

import com.dragonguard.backend.domain.gitrepomember.entity.GitRepoMember;
import com.dragonguard.backend.domain.gitrepomember.repository.GitRepoMemberRepository;
import com.dragonguard.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GitRepoMemberWriter implements ItemWriter<List<GitRepoMember>> {
    private final MemberRepository memberRepository;
    private final GitRepoMemberRepository gitRepoMemberRepository;

    @Override
    public void write(final List<? extends List<GitRepoMember>> items) throws Exception {
        List<GitRepoMember> result = items.stream()
                .flatMap(List::stream)
                .peek(i -> memberRepository.findByGithubId(i.getMember().getGithubId()).orElseGet(() -> memberRepository.save(i.getMember())))
                .collect(Collectors.toList());

        gitRepoMemberRepository.saveAll(result);
    }
}
