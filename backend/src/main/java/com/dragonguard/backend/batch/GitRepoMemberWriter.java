package com.dragonguard.backend.batch;

import com.dragonguard.backend.domain.gitrepomember.entity.GitRepoMember;
import com.dragonguard.backend.domain.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GitRepoMemberWriter implements ItemWriter<List<GitRepoMember>> {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void write(final List<? extends List<GitRepoMember>> items) {
        items.stream().flatMap(List::stream).forEach(i -> memberRepository.save(i.getMember()));
    }
}
