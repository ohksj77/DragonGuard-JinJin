package com.dragonguard.backend.domain.email.repository;

import com.dragonguard.backend.domain.email.entity.Email;
import com.dragonguard.backend.global.repository.EntityRepository;

/**
 * @author 김승진
 * @description 이메일 정보에 대한 DB 접근을 수행하는 클래스
 */

public interface JpaEmailRepository extends EntityRepository<Email, Long>, EmailRepository {}
