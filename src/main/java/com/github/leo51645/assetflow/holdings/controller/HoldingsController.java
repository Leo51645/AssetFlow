package com.github.leo51645.assetflow.holdings.controller;

import com.github.leo51645.assetflow.holdings.domain.dto.mapper.HoldingsDtoMapper;
import com.github.leo51645.assetflow.holdings.domain.dto.response.HoldingsResponseDto;
import com.github.leo51645.assetflow.holdings.domain.entity.HoldingsEntity;
import com.github.leo51645.assetflow.holdings.service.HoldingsService;
import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import com.github.leo51645.assetflow.investment_asset.service.InvestAssetService;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/holdings")
public class HoldingsController {

    private final HoldingsService holdingsService;
    private final HoldingsDtoMapper holdingsDtoMapper;

    @GetMapping("/me")
    public ResponseEntity<HoldingsResponseDto> getHoldings(@AuthenticationPrincipal UserEntity userEntity) {
        List<HoldingsEntity> holdings = holdingsService.getHoldingsByUser(userEntity);

        return ResponseEntity.ok(holdingsDtoMapper.toHoldingsResponseDto(holdings, userEntity));
    }
}
