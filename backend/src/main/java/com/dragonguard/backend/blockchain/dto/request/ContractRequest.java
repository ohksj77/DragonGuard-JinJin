package com.dragonguard.backend.blockchain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContractRequest {
    private String address;
    private String contributeType;
    @Setter
    private BigInteger amount;
    private String githubId;
}