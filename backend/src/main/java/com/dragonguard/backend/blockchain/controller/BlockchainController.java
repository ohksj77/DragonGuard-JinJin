package com.dragonguard.backend.blockchain.controller;

import com.dragonguard.backend.blockchain.dto.response.BlockchainResponse;
import com.dragonguard.backend.blockchain.service.BlockchainService;
import com.dragonguard.backend.blockchain.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author 김승진
 * @description 블록체인 관련 요청을 처리하는 컨트롤러
 */

@RestController
@RequiredArgsConstructor
@DependsOn("blockchainConfig")
@RequestMapping("/blockchain")
public class BlockchainController {
    private final BlockchainService blockchainService;
    private final TransactionService transactionService;

    @PostConstruct
    public void initDeploy() {
        transactionService.deploy();
    }

    @GetMapping
    public ResponseEntity<List<BlockchainResponse>> getBlockchainInfo() {
        return ResponseEntity.ok(blockchainService.getBlockchainList());
    }
}
