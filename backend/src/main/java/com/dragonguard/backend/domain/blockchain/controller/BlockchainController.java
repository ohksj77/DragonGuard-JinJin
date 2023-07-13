package com.dragonguard.backend.domain.blockchain.controller;

import com.dragonguard.backend.domain.blockchain.dto.response.BlockchainResponse;
import com.dragonguard.backend.domain.blockchain.service.BlockchainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 김승진
 * @description 블록체인 관련 요청을 처리하는 컨트롤러
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/blockchain")
public class BlockchainController {
    private final BlockchainService blockchainService;

    @GetMapping
    public ResponseEntity<List<BlockchainResponse>> getBlockchainInfo() {
        return ResponseEntity.ok(blockchainService.getBlockchainList());
    }

    @PostMapping("/update") // todo 클라이언트 수정 후 삭제
    public ResponseEntity<List<BlockchainResponse>> updateAndGetBlockchainInfo() {
        return ResponseEntity.ok().build();
    }
}
