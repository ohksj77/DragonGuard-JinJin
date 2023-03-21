package com.dragonguard.backend.result.repository;

import com.dragonguard.backend.result.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 김승진
 * @description 검색 결과를 DB에 저장 및 조회 요청을 하는 클래스
 */

@Repository
public interface ResultRepository extends JpaRepository<Result, String> {
    List<Result> findAllBySearchId(Long searchId);
}
