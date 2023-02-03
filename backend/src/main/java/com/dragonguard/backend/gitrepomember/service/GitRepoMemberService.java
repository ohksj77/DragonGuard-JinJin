package com.dragonguard.backend.gitrepomember.service;

import com.dragonguard.backend.gitrepo.entity.GitRepo;
import com.dragonguard.backend.gitrepo.service.GitRepoService;
import com.dragonguard.backend.gitrepomember.dto.GitRepoMemberResponse;
import com.dragonguard.backend.gitrepomember.entity.GitRepoMember;
import com.dragonguard.backend.gitrepomember.mapper.GitRepoMemberMapper;
import com.dragonguard.backend.gitrepomember.repository.GitRepoMemberRepository;
import com.dragonguard.backend.member.entity.Member;
import com.dragonguard.backend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitRepoMemberService {

    private final GitRepoMemberRepository gitRepoMemberRepository;
    private final MemberService memberService;
    private final GitRepoService gitRepoService;
    private final GitRepoMemberMapper gitRepoMemberMapper;

    public void saveAllDto(List<GitRepoMemberResponse> gitRepoResponses, String gitRepo) {
        List<GitRepoMember> list = gitRepoResponses.stream().map(gitRepository -> {
//            Member member = memberService.findMemberByGithubId(gitRepository.getMemberName());
//            GitRepo gitRepoEntity = gitRepoService.findGitRepoByName(gitRepo);
            return gitRepoMemberMapper.toEntity(gitRepository, null, null);
        }).collect(Collectors.toList()); // TODO 깃 레포와 깃 레포 멤버 저장 (순서 조심)

        gitRepoMemberRepository.saveAll(list);
    }
}
