package com.dragonguard.backend.domain.result.service;

import com.dragonguard.backend.domain.result.entity.Result;
import com.dragonguard.backend.domain.result.mapper.ResultMapper;
import com.dragonguard.backend.domain.result.repository.ResultRepository;
import com.dragonguard.backend.domain.search.dto.client.GitRepoSearchClientResponse;
import com.dragonguard.backend.domain.search.dto.client.UserClientResponse;
import com.dragonguard.backend.domain.search.dto.kafka.ScrapeResult;
import com.dragonguard.backend.domain.search.dto.response.GitRepoResultResponse;
import com.dragonguard.backend.domain.search.dto.response.UserResultSearchResponse;
import com.dragonguard.backend.domain.search.entity.Search;
import com.dragonguard.backend.global.exception.EntityNotFoundException;
import com.dragonguard.backend.global.service.EntityLoader;
import com.dragonguard.backend.global.service.TransactionService;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author 김승진
 * @description 검색 결과에 대한 서비스 로직을 수행하는 클래스
 */

@TransactionService
@RequiredArgsConstructor
public class ResultServiceImpl implements EntityLoader<Result, Long>, ResultService {
    private final ResultRepository resultRepository;
    private final ResultMapper resultMapper;

    public void saveAllResultsWithSearch(final List<ScrapeResult> results, final Long searchId, final List<Result> resultList) {
        results.stream()
                .filter(entity -> resultRepository.existsByNameAndSearchId(entity.getFull_name(), searchId))
                .map(result -> resultMapper.toEntity(result.getFull_name(), searchId))
                .filter(r -> !resultList.contains(r))
                .forEach(resultRepository::save);
    }

    public List<Result> findAllBySearchId(final Long searchId) {
        return resultRepository.findAllBySearchId(searchId);
    }

    @Override
    public void deleteAllLastResults(final Search search) {
        resultRepository.findAllBySearchId(search.getId()).forEach(Result::delete);
    }

    @Override
    public UserResultSearchResponse saveResult(final UserClientResponse response, final Search search) {
        Result result = resultRepository.save(resultMapper.toEntity(response, search.getId()));
        return resultMapper.toUserResponse(result);
    }

    @Override
    public GitRepoResultResponse saveAndGetGitRepoResponse(final GitRepoSearchClientResponse request, final Search search) {
        Result result = resultRepository.save(resultMapper.toEntity(request.getFullName(), search.getId()));
        return resultMapper.toGitRepoResponse(result.getId(), request);
    }

    @Override
    public Result loadEntity(final Long id) {
        return resultRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}