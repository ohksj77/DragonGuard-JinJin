package com.dragonguard.backend.domain.organization.controller;

import com.dragonguard.backend.domain.organization.dto.request.AddMemberRequest;
import com.dragonguard.backend.global.IdResponse;
import com.dragonguard.backend.domain.organization.dto.request.OrganizationRequest;
import com.dragonguard.backend.domain.organization.dto.response.OrganizationResponse;
import com.dragonguard.backend.domain.organization.entity.OrganizationType;
import com.dragonguard.backend.domain.organization.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author 김승진
 * @description 조직(회사, 대학교)관련 요청을 받아오는 컨트롤러
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<IdResponse<Long>> saveOrganization(@RequestBody @Valid OrganizationRequest organizationRequest) {
        return ResponseEntity.ok(organizationService.saveOrganization(organizationRequest));
    }

    @GetMapping("/search-id")
    public ResponseEntity<IdResponse<Long>> getOrganization(@RequestParam String name) {
        return ResponseEntity.ok(organizationService.findByName(name));
    }

    @PostMapping("/add-member")
    public ResponseEntity<IdResponse<Long>> addMemberToOrganization(@RequestBody @Valid AddMemberRequest addMemberRequest) {
        return ResponseEntity.ok(organizationService.addMemberAndSendEmail(addMemberRequest));
    }

    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> getOrganizations(@RequestParam OrganizationType type, Pageable pageable) {
        return ResponseEntity.ok(organizationService.findByType(type, pageable));
    }

    @GetMapping("/ranking/all")
    public ResponseEntity<List<OrganizationResponse>> getOrganizationRank(Pageable pageable) {
        return ResponseEntity.ok(organizationService.getOrganizationRank(pageable));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<OrganizationResponse>> getOrganizationRankByType(
            @RequestParam OrganizationType type, Pageable pageable) {
        return ResponseEntity.ok(organizationService.getOrganizationRankByType(type, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<OrganizationResponse>> searchOrganization(
            @RequestParam OrganizationType type, @RequestParam String name, Pageable pageable) {
        return ResponseEntity.ok(organizationService.searchOrganization(type, name, pageable));
    }
}
